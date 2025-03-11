package org.example.persistence.dao;

import lombok.AllArgsConstructor;
import org.example.dto.CardDetailsDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.example.persistence.converter.OffsetDateTimeConverter.toOffsetDateTime;

@AllArgsConstructor
public class CardDAO {
    private final Connection connection;

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        var sql =
                """
                SELECT c.id,
                       c.title,
                       c.description,
                       b.blocked_at,
                       b.blocked_reason,
                       c.board_column_id,
                       bc.name,
                       (SELECT COUNT(sub_b.id)
                        FROM BLOCKS sub_b
                        WHERE sub_b.card_id = c.id) AS blocks_amount
                FROM CARDS c
                LEFT JOIN BLOCKS b
                    ON c.id = b.card_id
                   AND b.unblocked_at IS NULL
                INNER JOIN BOARDS_COLUMNS bc
                    ON bc.id = c.board_column_id
                WHERE c.id = ?;
                """;
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.execute();
            var resultSet = statement.getResultSet();
            if (resultSet.next()) {
                var dto = new CardDetailsDTO(
                        resultSet.getLong("c.id"),
                        resultSet.getString("c.title"),
                        resultSet.getString("c.description"),
                        resultSet.getString("b.blocked_reason") != null, // Verifica se está bloqueado
                        toOffsetDateTime(resultSet.getTimestamp("b.blocked_at")),
                        resultSet.getString("b.blocked_reason"),
                        resultSet.getInt("blocks_amount"),
                        resultSet.getLong("c.board_column_id"),
                        resultSet.getString("bc.name")
                );
                return Optional.of(dto);
            }
        }
        return Optional.empty();
    }
}
