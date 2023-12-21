-- SECTION 1 - DDL (Includes Creating the tables and adding, removing and updating the data inside that table).
-- Creating a new table members in the cd schema.
CREATE TABLE IF NOT EXISTS cd.members (
	memid			INTEGER NOT NULL,
	surname			VARCHAR(200) NOT NULL,
	firstname		VARCHAR(200) NOT NULL,
	address			VARCHAR(300) NOT NULL,
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
    facid				INTEGER NOT NULL,
    name 				VARCHAR(100) NOT NULL,
    membercost			NUMERIC NOT NULL,
    guestcost			NUMERIC NOT NULL,
    initialoutlay		NUMERIC NOT NULL,
    monthlymaintenance	NUMERIC NOT NULL,
    CONSTRAINT facid_pk PRIMARY KEY (facid)
);


-- Creating a new table bookings in the cd schema.
CREATE TABLE IF NOT EXISTS cd.bookings (
    bookid		INTEGER NOT NULL,
    facid 		INTEGER NOT NULL,
    memid		INTEGER NOT NULL,
    starttime	TIMESTAMP NOT NULL,
    slots 		INTEGER NOT NULL,
    CONSTRAINT bookid_pk PRIMARY KEY (bookid),
    CONSTRAINT fk_facid FOREIGN KEY (facid) REFERENCES cd.facilities(facid),
    CONSTRAINT fk_memid FOREIGN KEY (memid) REFERENCES cd.members (memid)
);

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


-- Updating the value inside the table.
UPDATE
    cd.facilities
SET
    initialoutlay = 8000
WHERE
    facid = 1;


-- Updating the values without constants.
UPDATE cd.facilities
SET
    guestcost = (SELECT guestcost * 1.1
                 FROM cd.facilities
                 WHERE facid = 0),
    membercost = (SELECT membercost * 1.1
                  FROM cd.facilities
                  WHERE facid = 0)
WHERE facid = 1;


-- Deleting the records from the table based on the condition.
DELETE FROM
    cd.bookings
WHERE
    bookid = 37;


-- Delete all the records.
DELETE FROM
    cd.bookings;


-- SECTION 2 - Basics of the SQL Queries.
-- Producing a list of all the facilities with 'Tennis' in their name.
SELECT *
FROM
    cd.facilities
WHERE
    name LIKE '%Tennis%';


-- Producing the list of facilities which charge less than 50th of the monthly maintenance cost.
SELECT
    facid, name, membercost, monthlymaintenance
FROM
    cd.facilities
WHERE
    membercost > 0 AND
    membercost < (monthlymaintenance / 50);


-- Produce a list of all facilities that have facid either 1 or 2.
SELECT *
FROM
    cd.facilities
WHERE
    facid IN (1, 5);

-- List of members who joined after the start of September 2012.
SELECT
    memid, surname, firstname, joindate
FROM
    cd.members
WHERE
    joindate >= '2012-09-01';


-- Combined list of all the surname of members along with the facilities name.
SELECT
    surname
FROM
    cd.members

UNION

SELECT
    name
FROM
    cd.facilities;


-- SECTION 3 - Joins
-- List of the start times for bookings by members named 'David Farrell'.
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


-- List of the start times for bookings for tennis courts, for the date '2012-09-21'.
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


-- List of all members, including the individual who recommended them (if any) ordered by (surname, firstname).
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


-- Unique list of all members who have recommended another member ordered by (surname, firstname)
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


-- list of all members, including the individual who recommended them (if any), without using any joins.
SELECT DISTINCT
    (firstname || ' ' || surname) AS member,
    (
        SELECT (firstname || ' ' || surname) AS recommender
        FROM cd.members AS recs
        WHERE recs.memid = mems.recommendedby
    )
FROM
    cd.members AS mems
ORDER BY
    member;


-- SECTION 4 - Aggregations.
-- Count of the number of recommendations each member has made ordered by member ID.
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


-- List of the total number of slots booked per facility
SELECT
    facid,
    SUM(slots) AS "total slots"
FROM
    cd.bookings
GROUP BY
    facid
ORDER BY
    facid;


-- List of the total number of slots booked per facility in the month of September 2012.
SELECT
    facid,
    SUM(slots) AS "Total Slots"
FROM
    cd.bookings
WHERE
    starttime >= '2012-09-01' AND
    starttime < '2012-10-01'
GROUP BY
    facid
ORDER BY
    "Total Slots";


-- List of the total number of slots booked per facility per month in the year of 2012
SELECT
    facid,
    (EXTRACT (MONTH FROM starttime)) AS month,
    SUM(slots) AS "Total Slots"
FROM
    cd.bookings
WHERE
    starttime >= '2012-01-01' AND
    starttime < '2013-01-01'
GROUP BY
    facid, month
ORDER BY
    facid, month;


-- Total number of members (including guests) who have made at least one booking.
SELECT
    COUNT(DISTINCT memid)
FROM
    cd.bookings;


-- List of each member name, id, and their first booking after September 1st 2012. Order by member ID.
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


-- List of member names, with each row containing the total member count.
SELECT
    COUNT(*) OVER(), firstname, surname
FROM
    cd.members
ORDER BY
    joindate;


-- Monotonically increasing numbered list of members (including guests), ordered by their date of joining.
SELECT
    ROW_NUMBER() OVER(ORDER BY joindate),
    firstname, surname
FROM
    cd.members
ORDER BY
    joindate;


-- Facility id/s that has the highest number of slots booked.
SELECT facid, total
FROM (
         SELECT
             facid,
             SUM(slots) total,
             RANK() OVER (ORDER BY SUM(slots) DESC) RANK
         FROM cd.bookings
         GROUP BY facid
     ) AS ranked
WHERE
    RANK = 1;


-- SECTION 5 - String Manipulations.
-- Names of all members, formatted as 'Surname, Firstname'
SELECT
    (surname || ', ' || firstname) AS name
FROM
    cd.members;


-- Find all the telephone numbers that contain parentheses, returning the member ID and telephone number sorted by member ID.
SELECT
    memid, telephone
FROM
    cd.members
WHERE
    telephone LIKE '(%'
ORDER BY
    memid;


-- Count of how many members you have whose surname starts with each letter of the alphabet.
SELECT
    SUBSTR(members.surname, 1, 1) AS letter,
    COUNT(*) AS count
FROM
    cd.members
GROUP BY
    letter
ORDER BY
    letter;
