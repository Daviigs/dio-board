--liquibase formatted sql
--changeset davi:202502251606
--comment: set unblock_reason nullable

ALTER TABLE BLOCK MODIFY COLUMN unblock_reason VARCHAR(255) NULL;

--rollback ALTER TABLE BLOCK MODIFY COLUMN unblock_reason VARCHAR(255) NOT NULL;