package org.example.persistence.dao;

import com.mysql.cj.jdbc.StatementImpl;
import lombok.AllArgsConstructor;
import org.example.dto.BoardColumnDTO;
import org.example.persistence.entity.BoardColumnEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.example.persistence.entity.BoardColumnKindEnum.findByName;

@AllArgsConstructor
public class BoardColumnDAO {

    private final Connection connection;

    public BoardColumnEntity insert(final BoardColumnEntity entity) throws SQLException{
        var sql = "INSERT INTO BOARDS_COLUMNS (name, `order`, kind, board_id) VALUES (?, ?, ?, ?)";
        try (var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setString(i++, entity.getName());
            statement.setInt(i++, entity.getOrder());
            statement.setString(i++, entity.getKind().name());
            statement.setLong(i, entity.getBoard().getId());
            statement.executeUpdate();
            if (statement instanceof StatementImpl impl){
                entity.setId(impl.getLastInsertID());
            }
            return entity;
        }
    }

    public List<BoardColumnEntity> findByBoardId(Long id) throws SQLException {
        List<BoardColumnEntity> entities = new ArrayList<>();
        var sql = "SELECT id, name, `order` FROM BOARDS_COLUMNS WHERE board_id = ? ORDER BY `order`";
        try (var statement = connection.prepareStatement(sql)){
            statement.setLong(1, id);
            statement.executeQuery();
            var result = statement.getResultSet();
            while(result.next()){
                var entity = new BoardColumnEntity();
                entity.setId(result.getLong("id"));
                entity.setName(result.getString("name"));
                entity.setOrder(result.getInt("order"));
                entity.setKind(findByName(result.getString("kind")));
                entities.add(entity);
            }
            return entities;
        }
    }

    public List<BoardColumnDTO> findByBoardIdWithDetails(Long id) throws SQLException {
        List<BoardColumnDTO> dtos = new ArrayList<>();
        var sql =
                """
                SELECT 
                bc.id, 
                bc.name,

                bc.kind,
                COUNT(c.id) AS cards_amount
                 FROM BOARDS_COLUMNS bc
                 LEFT JOIN CARDS c ON c.board_column_id = bc.id
                 WHERE bc.board_id = ? 
                 GROUP BY bc.id
                 ORDER BY bc.`order`
                """;
        try (var statement = connection.prepareStatement(sql)){
            statement.setLong(1, id);
            statement.executeQuery();
            var result = statement.getResultSet();
            while(result.next()){
                var dto = new BoardColumnDTO(
                        result.getLong("bc.id"),
                        result.getString("bc.name"),
                        findByName(result.getString("bc.kind")),
                        result.getInt("cards_amount")
                );
                dtos.add(dto);
            }
            return dtos;
        }
    }
}
