package org.example.persistence.dao;

import lombok.AllArgsConstructor;
import org.example.dto.CardDatails;

import java.sql.Connection;

@AllArgsConstructor
public class CardDAO {
    private Connection connection;

    public CardDatails findById(final Long id){
        return null;
    }
}
