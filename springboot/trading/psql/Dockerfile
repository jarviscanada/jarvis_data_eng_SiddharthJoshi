FROM postgres:latest

# Create all schemas for the project
COPY ./01_schema.sql /docker-entrypoint-initdb.d

# Populate the quote table with some data to start with
COPY ./02_dml.sql /docker-entrypoint-initdb.d
