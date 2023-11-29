#!/bin/bash
# Mentioning the Kernel to use the bash shell.

psql_host=$1
psql_port_number=$2
db_name=$3
psql_user=$4
psql_password=$5

# Function to insert the data into the database inside 'host_info' table.
insert_data_into_database() {

  insert_query="
    INSERT INTO host_info (
      hostname, cpu_number,
      cpu_architecture, cpu_model,
      cpu_mhz, l2_cache,
      timestamp, total_mem
    )
    VALUES (
      '$host_name', '$cpu_number',
      '$cpu_architecture', '$cpu_model_name',
      '$cpu_frequency', '$l2_cache',
      '$timestamp', '$total_memory'
    );
  "

  export PGPASSWORD=$psql_password
  psql -h "$psql_host" -p "$psql_port_number" -d "$db_name" -U "$psql_user" -c "$insert_query"

  # Return the exit status of the previously executed command.
  return $?
}

if [ "$#" -ne 5 ]; then
  echo "Illegal number of Arguments. Please provide appropriate command line arguments."
  exit 1
fi

# Storing the details of the CPU in a variable to avoid running the command multiple times.
cpu_details=$(lscpu)

# The timestamp in a specific format when all these values were recorded.
timestamp=$(date '+%F %H:%M:%S')

# The full name of the host machine.
host_name=$(hostname -f)

# The amount of logical cores.
cpu_number=$(echo "$cpu_details" | grep -E "^CPU\(s\):" |  awk '{print $2}' | xargs)

# The architecture of the CPU (32 bit or 64 bit).
cpu_architecture=$(echo "$cpu_details" | grep -E "^Architecture:\s+" | awk '{print $2}' | xargs)

# The model of the CPU.
cpu_model_name=$(echo "$cpu_details" | grep -E "^Model name:" | sed 's/Model name:\s*//')

# The CPU Clock speed / Frequency in MHz (The amount of instructions CPU can process in one second).
cpu_frequency=$(echo "$cpu_details" | grep -E "^CPU MHz:" | awk '{print $3}' | xargs)

# L2 Cache (Small high speed memory storage between CPU and the RAM providing faster access).
l2_cache=$(echo "$cpu_details" | grep -E "^L2 cache:" | awk '{print $3}' | grep -oE '[0-9]+')

# Total Memory (Random Access Memory - RAM).
total_memory=$(free -m | awk 'NR==2 {print $2}')

insert_data_into_database

exit $?
