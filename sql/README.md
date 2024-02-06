# Structured Query Language
## Overview
This project covers Relational Databases and Structured Query Language (SQL) in detail. The purpose of this project is to introduce one to the realm of Relational Databases along with working with large sets of data interfacing with the data using SQL.

## Quickstart
1. Create a Docker container with psql image inside it.
2. Once the container is created, run the container in detached mode in the background.
3. Use Dbeaver or PgAdmin4 client to easily manage the queries, databases and relations on the psql instance.
4. Once setup, run the `clubdata.sql` file to automatically initialize the database, tables and populate the relations with the actual working data.
5. Practice SQL Queries by referring to `queries.sql` file.


## SQL Queries
### Data Definition Language
##### Setting up the Tables (DDL)
```sql
-- Creating a new table members in the cd schema.
CREATE TABLE IF NOT EXISTS cd.members (
    memid		INTEGER NOT NULL,
    surname		VARCHAR(200) NOT NULL,
    firstname		VARCHAR(200) NOT NULL,
    address		VARCHAR(300) NOT NULL,
    zipcode 		INTEGER NOT NULL,
    telephone 		VARCHAR(20) NOT NULL,
    recommendedby	INTEGER,
    joindate 		TIMESTAMP NOT NULL,
    CONSTRAINT memid_pk PRIMARY KEY (memid),
    CONSTRAINT fk_members_recommendedby FOREIGN KEY (recommendedby)
    REFERENCES cd.members(memid) ON DELETE SET NULL
);


-- Creating a new table facilities in the cd schema.
CREATE TABLE IF NOT EXISTS cd.facilities (
    facid			INTEGER NOT NULL,
    name 			VARCHAR(100) NOT NULL,
    membercost			NUMERIC NOT NULL,
    guestcost			NUMERIC NOT NULL,
    initialoutlay		NUMERIC NOT NULL,
    montlymaintenance	        NUMERIC NOT NULL,
    CONSTRAINT facid_pk PRIMARY KEY (facid)
);


-- Creating a new table bookings in the cd schema.
CREATE TABLE IF NOT EXISTS cd.bookings (
    bookid		INTEGER NOT NULL,
    facid 		INTEGER NOT NULL,
    memid		INTEGER NOT NULL,
    starttime	        TIMESTAMP NOT NULL,
    slots 		INTEGER NOT NULL,
    CONSTRAINT bookid_pk PRIMARY KEY (bookid),
    CONSTRAINT fk_facid FOREIGN KEY (facid) REFERENCES cd.facilities(facid),
    CONSTRAINT fk_memid FOREIGN KEY (memid) REFERENCES cd.members (memid)
);
```

##### Inserting Data Inside the Table
```sql
-- Inserting a new record of Spa inside the facilities table.
INSERT INTO cd.facilities (
    facid, name, membercost, guestcost, initialoutlay, monthlymaintenance
)
VALUES (
    9, 'Spa', 20, 30, 100000, 800
);

-- Inserting a new record differently.
INSERT INTO cd.facilities (
    facid, name, membercost, guestcost, initialoutlay, monthlymaintenance
)
VALUES (
        (SELECT MAX(facid) FROM cd.facilities) + 1,
        'Spa', 20, 30, 100000, 800
);
```

##### Updating Data Inside the Table.
```sql
-- Updating the value inside the table.
UPDATE cd.facilities 
    SET initialoutlay = 8000
    WHERE facid = 1;
    

-- Updating the values without constants.
UPDATE 
    cd.facilities
SET 
    guestcost = (SELECT guestcost * 1.1
	            FROM cd.facilities 
		    WHERE facid = 0),
    membercost = (SELECT membercost * 1.1
		    FROM cd.facilities
		    WHERE facid = 0)
WHERE 
    facid = 1;
```

##### Deleting the Data Inside the Table.
```sql
-- Deleting the recods from the table based on the condition.
DELETE FROM 
    cd.bookings 
WHERE 
    bookid = 37;


-- Delete all the records.
DELETE FROM 
    cd.bookings;
```

### Data Manipulation Language
#### SQL Basics
###### Question 1: How can you produce a list of facilities that charge a fee to members, and that fee is less than 1/50th of the monthly maintenance cost? Return the facid, facility name, member cost, and monthly maintenance of the facilities in question.
```sql
SELECT
    facid, name, membercost, monthlymaintenance
FROM
    cd.facilities
WHERE
    membercost > 0 AND
    membercost < (monthlymaintenance / 50);
```

###### Question 2: How can you produce a list of all facilities with the word 'Tennis' in their name?
```sql
SELECT *
FROM
    cd.facilities
WHERE
    name LIKE '%Tennis%';
```

###### Question 3: How can you retrieve the details of facilities with ID 1 and 5? Try to do it without using the OR operator.
```sql
SELECT *
FROM
    cd.facilities
WHERE
    facid IN (1, 5);
```

###### Question 4: How can you produce a list of members who joined after the start of September 2012? Return the memid, surname, firstname, and joindate of the members in question.
```sql
SELECT
    memid, surname, firstname, joindate
FROM
    cd.members
WHERE
    joindate >= '2012-09-01';
```

###### Question 5: Produce a combined list of all surnames and all facility names.
```sql
SELECT
    surname
FROM
    cd.members

UNION

SELECT
    name
FROM
    cd.facilities;
```

#### SQL Joins
###### Question 1: Produce a list of the start times for bookings by members named 'David Farrell'?
```sql
SELECT 
    starttime
FROM 
    cd.bookings
INNER JOIN 
    cd.members
    ON bookings.memid = members.memid
WHERE
    firstname LIKE 'David' AND
    surname LIKE 'Farrell';
```

###### Question 2: Produce a list of the start times for bookings for tennis courts, for the date '2012-09-21'? Return a list of start time and facility name pairings, ordered by the time.
```sql
SELECT 
    starttime AS start, name
FROM 
    cd.bookings
INNER JOIN 
    cd.facilities 
    ON bookings.facid = facilities.facid	
WHERE 
    name LIKE 'Tennis Court%' AND
    starttime >= '2012-09-21' AND
    starttime < '2012-09-22'
ORDER BY 
    starttime;
```

###### Question 3: How can you output a list of all members, including the individual who recommended them (if any)? Ensure that results are ordered by (surname, firstname).
```sql
SELECT 
    mems.firstname AS memfname,
    mems.surname AS memsname, 
    recs.firstname AS recfname,
    recs.surname AS recsname
FROM 
    cd.members AS mems
LEFT JOIN 
    cd.members AS recs
    ON recs.memid = mems.recommendedby
ORDER BY 
    mems.surname, 
    mems.firstname;
```

###### Question 4: How can you output a list of all members who have recommended another member? Ensure that there are no duplicates in the list, and that results are ordered by (surname, firstname).
```sql
SELECT DISTINCT 
    mems.firstname AS memfname,
    mems.surname AS memsname
FROM 
    cd.members AS mems
INNER JOIN 
    cd.members AS recs
    ON mems.memid = recs.recommendedby
ORDER BY 
    mems.surname, mems.firstname;
```

###### Question 5: How can you output a list of all members, including the individual who recommended them (if any), without using any joins? Ensure that there are no duplicates in the list, and that each firstname + surname pairing is formatted as a column and ordered.
```sql
SELECT DISTINCT 
    (firstname || ' ' || surname) AS member,
	(
	    SELECT 
	        (firstname || ' ' || surname) AS recommender 
		FROM 
		    cd.members AS recs
		WHERE 
		    recs.memid = mems.recommendedby
		) 
FROM
    cd.members AS mems
ORDER BY 
    member;
```

#### SQL Aggregations
###### Question 1: Produce a count of the number of recommendations each member has made. Order by member ID.
```sql
SELECT 
    recommendedby,
    COUNT(*) AS count 
FROM
    cd.members
WHERE 
    recommendedby IS NOT NULL
GROUP BY
    recommendedby
ORDER BY 
    recommendedby;
```

###### Question 2: Produce a list of the total number of slots booked per facility. For now, just produce an output table consisting of facility id and slots, sorted by facility id.
```sql
SELECT 
    facid, 
    SUM(slots) AS "total slots" 
FROM
    cd.bookings
GROUP BY
    facid
ORDER BY 
    facid
```

###### Question 3: Produce a list of the total number of slots booked per facility in the month of September 2012. Produce an output table consisting of facility id and slots, sorted by the number of slots.
```sql
SELECT
    facid,
    SUM(slots) AS "Total Slots"
FROM 
    cd.bookings
WHERE 
    starttime >= '2012-09-01' 
    AND starttime < '2012-10-01'
GROUP BY 
    facid
ORDER BY 
    "Total Slots";
```

###### Question 4: Produce a list of the total number of slots booked per facility per month in the year of 2012. Produce an output table consisting of facility id and slots, sorted by the id and month.
```sql
SELECT
    facid,
    (EXTRACT (MONTH FROM starttime)) AS month,
    SUM(slots) AS "Total Slots"
FROM
    cd.bookings
WHERE 
    starttime >= '2012-01-01'
    AND starttime < '2013-01-01'
GROUP BY 
    facid, month
ORDER BY
    facid, month;
```

###### Question 5: Find the total number of members (including guests) who have made at least one booking.
```sql
SELECT
    COUNT(DISTINCT memid)
FROM
    cd.bookings;
```

###### Question 6: Produce a list of each member name, id, and their first booking after September 1st 2012. Order by member ID.
```sql
SELECT
    surname, firstname, members.memid, MIN(starttime)
FROM 
    cd.members
INNER JOIN 
    cd.bookings
    ON members.memid = bookings.memid
WHERE
    starttime > '2012-09-01'
GROUP BY 
    surname, firstname, members.memid
ORDER BY 
    members.memid;
```

###### Question 7: Produce a list of member names, with each row containing the total member count. Order by join date, and include guest members.
```sql
SELECT 
    COUNT(*) OVER(), firstname, surname
FROM 
    cd.members
ORDER BY 
    joindate;
```

###### Question 8: Produce a monotonically increasing numbered list of members (including guests), ordered by their date of joining. Remember that member IDs are not guaranteed to be sequential.
```sql
SELECT 
    ROW_NUMBER() OVER(ORDER BY joindate), 
    firstname, surname
FROM 
    cd.members
ORDER BY 
    joindate;
```

###### Question 9: Output the facility id that has the highest number of slots booked. Ensure that in the event of a tie, all tieing results get output.
```sql
SELECT facid, total 
FROM (
    SELECT 
	facid, 
	SUM(slots) total, 
	RANK() OVER (ORDER BY SUM(slots) DESC) RANK
    FROM 
        cd.bookings
    GROUP BY 
	facid
) AS ranked
WHERE 
    RANK = 1;
```

#### SQL Strings
###### Question 1: Output the names of all members, formatted as 'Surname, Firstname'
```sql
SELECT 
    (surname || ', ' || firstname) AS name
FROM 
    cd.members;
```

###### Question 2: You've noticed that the club's member table has telephone numbers with very inconsistent formatting. You'd like to find all the telephone numbers that contain parentheses, returning the member ID and telephone number sorted by member ID.
```sql
SELECT 
    memid, telephone
FROM 
    cd.members
WHERE 
    telephone LIKE '(%'
ORDER BY 
    memid;
```

###### Question 3: You'd like to produce a count of how many members you have whose surname starts with each letter of the alphabet. Sort by the letter, and don't worry about printing out a letter if the count is 0.
```sql
SELECT 
    SUBSTR(members.surname, 1, 1) AS letter,
    COUNT(*) AS count
FROM
    cd.members
GROUP BY 
    letter
ORDER BY 
    letter;
```