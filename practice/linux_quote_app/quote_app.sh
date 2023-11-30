#!/bin/bash

# Checking the number of command line arguments.
if [ $# -ne 9 ]; then
  echo "Inappropriate amount of Command Line Arguments."
  exit 1
fi

# Extracting the values from command line arguments.
api_key=$1
psql_host=$2
psql_port=$3
psql_database=$4
psql_username=$5
psql_password=$6
company_symbol_one=$7
company_symbol_two=$8
company_symbol_three=$9

# Getting the Microsoft's data from the Endpoint.
msft_data=$(curl --request GET \
	--url 'https://alpha-vantage.p.rapidapi.com/query?function=GLOBAL_QUOTE&symbol=MSFT&datatype=json' \
	--header 'X-RapidAPI-Host: alpha-vantage.p.rapidapi.com' \
	--header 'X-RapidAPI-Key: '"$api_key"'')

echo "$msft_data"

# Validating whether the API Endpoint was successful or not.
global_quote_msft=$(echo "$msft_data" | jq '."Global Quote"')
if [ "$global_quote_msft" == "{}" ];
  then
    echo "Error getting the Quotes data for Microsoft. Aborting"
    exit 1
fi

# Extracting the values from the JSON file.
company_symbol=$(echo "$global_quote_msft" | jq '."01. symbol"')
open=$(echo "$global_quote_msft" | jq '."02. open" | tonumber')
high=$(echo "$global_quote_msft" | jq '."03. high" | tonumber')
low=$(echo "$global_quote_msft" | jq '."04. low" | tonumber')
price=$(echo "$global_quote_msft" | jq '."05. price" | tonumber')
volume=$(echo "$global_quote_msft" | jq '."06. volume" | tonumber')

# Generating a Query for Insertion.
insert_query="
  INSERT INTO quotes (
    symbol, open,
    high, low,
    price, volume
  )
  VALUES (
    '$company_symbol', '$open',
    '$high', '$low',
    '$price', '$volume'
  );
"

export PGPASSWORD=$psql_password
psql -h "$psql_host" -p "$psql_port" -d "$psql_database" -U "$psql_username" -c "$insert_query"

# Getting the Amazon's data from the Endpoint.
amzn_data=$(curl --request GET \
	--url 'https://alpha-vantage.p.rapidapi.com/query?function=GLOBAL_QUOTE&symbol=AMZN&datatype=json' \
	--header 'X-RapidAPI-Host: alpha-vantage.p.rapidapi.com' \
	--header 'X-RapidAPI-Key: 0e59127ce5msh1d2d1843608a49cp190fb0jsn45a36b4b0fb8')

global_quote_amzn=$(echo "$amzn_data" | jq '."Global Quote"')
if [ "$global_quote_amzn" == "{}" ];
  then
    echo "Error getting the Quotes data for Amazon. Aborting"
    exit 1
fi

# Extracting the values from the JSON file.
company_symbol=$(echo "$global_quote_amzn" | jq '."01. symbol"')
open=$(echo "$global_quote_amzn" | jq '."02. open" | tonumber')
high=$(echo "$global_quote_amzn" | jq '."03. high" | tonumber')
low=$(echo "$global_quote_amzn" | jq '."04. low" | tonumber')
price=$(echo "$global_quote_amzn" | jq '."05. price" | tonumber')
volume=$(echo "$global_quote_amzn" | jq '."06. volume" | tonumber')

# Updating the Insert Query with updated values.
insert_query="
  INSERT INTO quotes (
    symbol, open,
    high, low,
    price, volume
  )
  VALUES (
    '$company_symbol', '$open',
    '$high', '$low',
    '$price', '$volume'
  );
"

psql -h "$psql_host" -p "$psql_port" -d "$psql_database" -U "$psql_username" -c "$insert_query"

# Getting the Apple's data from the Endpoint.
aapl_data=$(curl --request GET \
	--url 'https://alpha-vantage.p.rapidapi.com/query?function=GLOBAL_QUOTE&symbol=AAPL&datatype=json' \
	--header 'X-RapidAPI-Host: alpha-vantage.p.rapidapi.com' \
	--header 'X-RapidAPI-Key: 0e59127ce5msh1d2d1843608a49cp190fb0jsn45a36b4b0fb8')

global_quote_aapl=$(echo "$aapl_data" | jq '."Global Quote"')
if [ "$global_quote_aapl" == "{}" ];
  then
    echo "Error getting the Quotes data for Amazon. Aborting"
    exit 1
fi

# Extracting the values from the JSON file.
company_symbol=$(echo "$global_quote_aapl" | jq '."01. symbol"')
open=$(echo "$global_quote_aapl" | jq '."02. open" | tonumber')
high=$(echo "$global_quote_aapl" | jq '."03. high" | tonumber')
low=$(echo "$global_quote_aapl" | jq '."04. low" | tonumber')
price=$(echo "$global_quote_aapl" | jq '."05. price" | tonumber')
volume=$(echo "$global_quote_aapl" | jq '."06. volume" | tonumber')

# Updating the insert query with updated values.
insert_query="
  INSERT INTO quotes (
    symbol, open,
    high, low,
    price, volume
  )
  VALUES (
    '$company_symbol', '$open',
    '$high', '$low',
    '$price', '$volume'
  );
"

psql -h "$psql_host" -p "$psql_port" -d "$psql_database" -U "$psql_username" -c "$insert_query"

exit 0
