#!/bin/bash
# The first line is Shebang explicitly mentioning the kernel to use bash shell.

# Getting a hold of the arguments from command line
command=$1
database_username=$2
database_password=$3

# If docker is not running already, then run it.
sudo systemctl status docker || sudo systemctl start docker

# Checking the status of the container.
docker container inspect jrvs-psql

# Capturing the exit status of the previous command.
container_status=$?

# Using a Switch Case if the end-user wants to create an psql instance.
case $command in
  create)

    # Checking the status of the container if it is already created.
    if [ $container_status -eq 0 ]; then
      echo "The Container already exists."
      exit 1
    fi

    # Checking the amount of Command Line arguments.
    if [ $# -ne 3 ]; then
      echo "Creating a psql instance requires a username and a password. Please try again."
      exit 1
    fi

    # Creating a volume for the container.
    docker volume create pgdata

    # Starting the container once it is created.
    docker run --name jrvs-psql -e POSTGRES_PASSWORD="$database_password" \
      -e POSTGRES_USER="$database_username" -d -v pgdata:/var/lib/postgresql/data -p 5432:5432 postgres:9.6-alpine

    # Exiting with the status of the last executed command.
    exit $?
  ;;

  # If the command is start or stop.
  start|stop)

    # If the 'docker inspect ...' command exited with status 1, then container is not running.
    if [ $container_status -eq 1 ]; then
      exit 1
    fi

    # Starting / Stopping the container based on the command.
    docker container "$command" jrvs-psql
    exit $?
  ;;

  *)
    echo "Illegal Command/s."
    echo "Appropriate Commands: start | stop | create"
    exit 1
  ;;

esac

exit 0
