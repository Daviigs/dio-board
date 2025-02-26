--linquibase formatted sql
--changeset davi:202502251606
--comment: boards table create

CREATE TABLE BOARDS(

    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
) ENGINE=InooDB;


--rollback DROP TABLE BOARDS
