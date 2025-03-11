package org.example.service;

import lombok.AllArgsConstructor;
import org.example.dto.BoardColumnInfoDTO;
import org.example.exception.CardBlockedException;
import org.example.exception.CardFinishedException;
import org.example.exception.EntityNotFoundException;
import org.example.persistence.dao.CardDAO;
import org.example.persistence.entity.CardEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.example.persistence.entity.BoardColumnKindEnum.FINAL;


@AllArgsConstructor
public class CardService {

    private final Connection connection;

    public CardEntity create(final CardEntity entity) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            dao.insert(entity);
            connection.commit();
            return entity;
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public void moveToNextColumn(final Long cardId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {

        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(() -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId)));
            if (dto.blocked()) {
                throw new CardBlockedException("O card %s está bloqueado, é necessario desbloquea-lo para mover".formatted(cardId));
            }
            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board"));
            if (currentColumn.kind().equals(FINAL)) {
                throw new CardFinishedException("O card já foi finalizado");
            }
            var nextColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1)
                    .findFirst().orElseThrow();
            dao.moveToColumn(nextColumn.id(), cardId);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }

    }

}
