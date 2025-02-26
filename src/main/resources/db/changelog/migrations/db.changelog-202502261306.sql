--liquibase formatted sql
--changeset davi:202502251606
--comment: boards_columns table create

CREATE TABLE BOARDS_COLUMNS(

    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    `order` int NOT NULL,
    kind VARCHAR(8) NOT NULL,
    board_id BIGINT NOT NULL,
    CONSTRAINT board__boards_columns_fk FOREIGN KEY(board_id) REFERENCES BOARDS(id) ON DELETE CASCADE,
    CONSTRAINT id_order_uk UNIQUE KEY unique_board_id_order (board_id, `order`)
) ENGINE=InnoDB;


--rollback DROP TABLE BOARDS
