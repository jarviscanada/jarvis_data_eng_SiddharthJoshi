#!/bin/bash
# Mentioning the Kernel to use the bash shell.

# Fetching the arguments from the command line.
psql_host=$1
psql_port_number=$2
db_name=$3
psql_user=$4
psql_password=$5

# Function to insert the records in the database.
insert_data() {

  host_id_subquery="(
    SELECT id FROM host_info
      WHERE hostname='$host_name'
  )"

  insert_query="
    INSERT INTO host_usage (
      timestamp,
      host_id, memory_free,
      cpu_idle, cpu_kernel,
      disk_io, disk_available
    )
    VALUES (
      '$timestamp',
      ($host_id_subquery), '$free_memory',
      '$cpu_idle', '$cpu_kernel',
      '$disk_io', '$disk_available'
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

# Storing the stats of the machine and the host name in the variables.
vmstats_mb=$(vmstat --unit M)
host_name=$(hostname -f)

# The timestamp in a specific format when all these values were recorded.
timestamp=$(date '+%F %H:%M:%S')

# Amount of total free memory in Mb.
free_memory=$(echo "$vmstats_mb" | awk '{print $4}' | tail -1 | xargs)

# The percentage of time CPU spends in the idle state.
cpu_idle=$(echo "$vmstats_mb" | awk '{print $15}' | tail -1 | xargs)

# The percentage of time CPU spent in Kernel processes.
cpu_kernel=$(echo "$vmstats_mb" | awk '{print $14}' | tail -1 | xargs)

# This represents current number of I/O Operations in progress.
disk_io=$(vmstat -d | awk 'NR == 3 {print $10}')

# The amount of disk available on the file system '\' in MB.
disk_available=$(df -BM / | awk 'NR == 2 {print $4}' | grep -oE '[0-9]+')

insert_data

exit $?
