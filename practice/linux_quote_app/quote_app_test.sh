#!/bin/bash

# This function generates insertion query and inserts the data inside the database.
function insert_data() {

  # Generating a Query for Insertion.
  insert_query="
    INSERT INTO quotes (
      symbol, open,
      high, low,
      price, volume
    )
    VALUES (
      '$1', '$2',
      '$3', '$4',
      '$5', '$6'
    );
  "

  # Exporting the password as environment variable to bypass the password prompt.
  export PGPASSWORD=$psql_password

  # Connecting with the psql instance inside the docker container.
  psql -h "$psql_host" -p "$psql_port" -d "$psql_database" -U "$psql_username" -c "$insert_query"
}

# Checking the number of command line arguments.
if [ $# -lt 7 ]; then
  echo "Inappropriate amount of Command Line Arguments."
  exit 1
fi

# Extracting the values from command line arguments.
psql_host=$2
psql_port=$3
psql_database=$4
psql_username=$5
psql_password=$6

counter=0
for argument in "$@"
do
  if [ "$counter" -gt 5 ]
    then

      # Getting the Microsoft's data from the Endpoint.
      json_response=$(curl --request GET \
      	--url 'https://alpha-vantage.p.rapidapi.com/query?function=GLOBAL_QUOTE&symbol='"$argument"'&datatype=json' \
      	--header 'X-RapidAPI-Host: alpha-vantage.p.rapidapi.com' \
      	--header 'X-RapidAPI-Key: '"$1"'')

      # Validating whether the API Endpoint was successful or not.
      company_data=$(echo "$json_response" | jq '."Global Quote"')
      if [ "$company_data" == "{}" ];
        then
          echo "Error getting the Quotes data for $argument. Please make sure the company's symbol is accurate."
          exit 1
      fi

      # Extracting the values from the JSON file.
      company_symbol=$(echo "$company_data" | jq -r '."01. symbol"')
      open=$(echo "$company_data" | jq '."02. open" | tonumber')
      high=$(echo "$company_data" | jq '."03. high" | tonumber')
      low=$(echo "$company_data" | jq '."04. low" | tonumber')
      price=$(echo "$company_data" | jq '."05. price" | tonumber')
      volume=$(echo "$company_data" | jq '."06. volume" | tonumber')

      insert_data "$company_symbol" "$open" "$high" "$low" "$price" "$volume"
  fi
  ((counter+=1))
done

exit 0
