Drop Table IF EXISTS Employee;

CREATE TABLE Employee
(
    name   varchar(255) NOT NULL,
    salary float       NOT NULL,
    PRIMARY KEY (name)
);