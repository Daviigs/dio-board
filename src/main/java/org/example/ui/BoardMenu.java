package org.example.ui;


import lombok.AllArgsConstructor;
import org.example.persistence.entity.BoardEntity;

import java.util.Scanner;

@AllArgsConstructor
public class BoardMenu {

    private final Scanner sc =  new Scanner(System.in);

    private final BoardEntity entity;

    public void execute() {
        System.out.printf("Bem vindo ao board %s, selecione a operação desejada: ", entity.getId());

        var optinal = -1;
        while (optinal!=9){
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

            switch (optinal){
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


    }

    private void createCard() {
    }

    private void moveCard() {
        
    }

    private void blockCard() {
        
    }

    private void unblockCard() {
        
    }

    private void cancelCard() {
    }

    private void showBoard() {
    }

    private void showColumn() {
    }

    private void showCard() {
    }
}
