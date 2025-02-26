package org.example.service;

import lombok.AllArgsConstructor;
import org.example.persistence.dao.BoardDAO;

import java.sql.Connection;
import java.sql.SQLException;

@AllArgsConstructor
public class BoardService {

    private final Connection connection;

    public boolean delete(final Long id)throws SQLException {
        var dao = new BoardDAO(connection);
        try {
            if(!dao.exists(id)) {
                return false;
            }
                dao.delete(id);
                connection.commit();
                return true;
        }catch (SQLException e){
            connection.rollback();
            throw e;
        }

    }
}
