--liquibase formatted sql

--changeset AinLume:1
--comment: Добавление поля version для таблицы reservation
alter table reservation
    add column version int not null default 0;