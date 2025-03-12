package org.example.ui;


import lombok.AllArgsConstructor;
import org.example.dto.BoardColumnInfoDTO;
import org.example.persistence.entity.BoardColumnEntity;
import org.example.persistence.entity.BoardEntity;
import org.example.persistence.entity.CardEntity;
import org.example.service.BoardColumnQueryService;
import org.example.service.BoardQueryService;
import org.example.service.CardQueryService;
import org.example.service.CardService;

import java.sql.SQLException;
import java.util.Scanner;

import static org.example.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class BoardMenu {

    private final Scanner sc = new Scanner(System.in).useDelimiter("\n");

    private final BoardEntity entity;

    public void execute() {
        try {
            System.out.printf("Bem vindo ao board %s, selecione a operação desejada:\n ", entity.getId());

            var optinal = -1;
            while (optinal != 9) {
                System.out.println("1 - Criar um card");
                System.out.println("2 - Mover um card");
                System.out.println("3 - Bloquear um card");
                System.out.println("4 - Desbloquear um card");
                System.out.println("5 - Cancelar um card");
                System.out.println("6 - Visualizar Board");
                System.out.println("7 - Visualizar colunas com cards");
                System.out.println("8 - Visualizar card");
                System.out.println("9 - Voltar para o menu anterior");
                System.out.println("0 - Sair");
                optinal = sc.nextInt();

                switch (optinal) {
                    case 1 -> createCard();
                    case 2 -> moveCard();
                    case 3 -> blockCard();
                    case 4 -> unblockCard();
                    case 5 -> cancelCard();
                    case 6 -> showBoard();
                    case 7 -> showColumn();
                    case 8 -> showCard();
                    case 0 -> System.exit(0);
                    default -> System.out.println("Opção invalida, informe um opção do menu");
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    private void createCard() throws SQLException {
        var card = new CardEntity();
        System.out.printf("Informe o título do card:");
        card.setTitle(sc.next());
        System.out.printf("Informe o descrição do card:");
        card.setDescription(sc.next());
        card.setBoardColumn(entity.getInitialColumn());
        try (var connection = getConnection()) {
            new CardService(connection).create(card);
        }

    }

    private void moveCard()throws SQLException {
        System.out.println("Informe o id do card que deseja mover para a próxima coluna");
        var cardId = sc.nextLong();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try (var connection = getConnection()) {
            new CardService(connection).moveToNextColumn(cardId,boardColumnsInfo );
        }catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void blockCard() throws SQLException{
        System.out.println("Informe o id do card que será bloqueado");
        var cardId = sc.nextLong();
        System.out.println("Informe o motivo do bloqueio do card");
        var reason = sc.next();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()) {
            new CardService(connection).block(cardId,reason, boardColumnsInfo);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }

    }

    private void unblockCard()throws SQLException {
        System.out.println("Informe o id do card que será desbloqueado");
        var cardId = sc.nextLong();
        System.out.println("Informe o motivo do desbloqueio do card");
        var reason = sc.next();
        try(var connection = getConnection()) {
            new CardService(connection).unblock(cardId,reason);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void cancelCard()throws SQLException {
        System.out.println("Informe o id do card que deseja mover para a coluna de cancelamento");
        var cardId = sc.nextLong();
        var cancelColumn = entity.getCancelColumn();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try (var connection = getConnection()) {
            new CardService(connection).cancel(cardId,cancelColumn.getId() ,boardColumnsInfo );
        }catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void showBoard() throws SQLException {
        try (var connection = getConnection()) {
            var optional = new BoardQueryService(connection).showBoardDetails(entity.getId());
            optional.ifPresent(b -> {
                System.out.printf("Board [%s, %s]", b.id(), b.name());
                b.columns().forEach(c -> {
                    System.out.printf("Coluna [%s] tipo: [%s] tem %s cards\n", c.name(), c.kind(), c.cardsAmount());
                });
            });


        }
    }

    private void showColumn() throws SQLException {
        System.out.printf("Escolha uma coluna do board %s\n", entity.getName());
        var columnsIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumn = -1L;
        while (!columnsIds.contains(selectedColumn)) {
            entity.getBoardColumns().forEach(c -> System.out.printf("%s - %s [%s]\n", c.getId(), c.getName(), c.getKind()));
            selectedColumn = sc.nextLong();
        }
        try (var connection = getConnection()) {
            var column = new BoardColumnQueryService(connection).findById(selectedColumn);
            column.ifPresent(co -> {
                System.out.printf("Coluna %s tipo %s\n", co.getName(), co.getKind());
                co.getCards().forEach(ca -> System.out.printf("Card %s - %s\nDescrição: %s\n", ca.getId(), ca.getTitle(), ca.getDescription()));
            });
        }

    }

    private void showCard() throws SQLException {
        System.out.print("Informe o id do card que deseja visualizar: ");
        var selectedCardId = sc.nextLong();

        try (var connection = getConnection()) {
            new CardQueryService(connection).findById(selectedCardId)
                    .ifPresentOrElse(c -> {
                                System.out.printf("Card %d - %s.\n", c.id(), c.title());
                                System.out.printf("Descrição: %s\n", c.desciption());
                                System.out.printf(c.blocked() ? "Está bloqueado. Motivo: %s\n" : "Não está bloqueado.\n", c.blockReason());
                                System.out.printf("Já foi bloqueado %d vezes.\n", c.blocksAmount());
                                System.out.printf("Está no momento na coluna %s - %s\n", c.columnId(), c.columnName());
                            },
                            () -> System.out.printf("Não existe um card com o ID %d\n", selectedCardId));
        }
    }

}
