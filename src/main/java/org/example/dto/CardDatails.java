package org.example.dto;

import java.time.OffsetDateTime;

public record CardDatails(Long id,
                          boolean blocked,
                          OffsetDateTime blockedAt,
                          String blockReason,
                          int blockedAmount,
                          Long columnId,
                          String columnName) {

}
