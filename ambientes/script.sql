CREATE TABLE pessoas
(
    id         serial PRIMARY KEY,
    nome       VARCHAR(32),
    apelido    VARCHAR(32),
    nascimento DATE
);