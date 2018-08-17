--###################################
-- CREATE INITIAL SUMMARY OUTPUT
--###################################
CREATE OR REPLACE PROCEDURE initial_ouput AS
  sql1  INT;
BEGIN
  DBMS_OUTPUT.PUT_LINE('Original Record Counts:' || sql1);
  SELECT COUNT(*) 
  INTO sql1
  FROM Movies;
  DBMS_OUTPUT.PUT_LINE(RPAD('Movies:', 20) || sql1);
  
  SELECT COUNT(*) 
  INTO sql1
  FROM MoviesGenres;
  DBMS_OUTPUT.PUT_LINE(RPAD('MoviesGenres:', 20) || sql1);
  
  SELECT COUNT(*) 
  INTO sql1
  FROM Ratings;
  DBMS_OUTPUT.PUT_LINE(RPAD('Ratings:', 20) || sql1);
  
  SELECT COUNT(*) 
  INTO sql1
  FROM Users;
  DBMS_OUTPUT.PUT_LINE(RPAD('Users:', 20) || sql1);  
  
  SELECT COUNT(*) 
  INTO sql1
  FROM Age_Groups;
  DBMS_OUTPUT.PUT_LINE(RPAD('Age_Groups:', 20) || sql1);  
  
  SELECT COUNT(*) 
  INTO sql1
  FROM Occupation_Groups;
  DBMS_OUTPUT.PUT_LINE(RPAD('Occupation_Groups:', 20) || sql1);  
  DBMS_OUTPUT.PUT_LINE('--------------------------------');   
END initial_ouput;
/


--###################################
-- ALTER INITIAL MOVIES TABLE
--###################################
CREATE OR REPLACE PROCEDURE manipulate_movies AS
  CURSOR MoviesRowCursor IS
    SELECT *
    FROM Movies
    FOR UPDATE;
  row_var           Movies%ROWTYPE;
  newtitle          Movies.MovieTitle%TYPE;
  foundyearmade     Movies.YearMade%TYPE;
  category1         Movies.Category%TYPE;
  category2         Movies.Category%TYPE;
  category3         Movies.Category%TYPE;
  category4         Movies.Category%TYPE;
  category5         Movies.Category%TYPE;
  category6         Movies.Category%TYPE;
  alterstatement    VARCHAR2(1000) :=
    'ALTER TABLE Movies
      DROP COLUMN Category';
BEGIN
  OPEN MoviesRowCursor;
  LOOP
    FETCH MoviesRowCursor INTO row_var;
    EXIT WHEN MoviesRowCursor%NOTFOUND;
    --manipulate strings
    foundyearmade := REGEXP_SUBSTR(row_var.MovieTitle, '\d{4}');
    newtitle      := REGEXP_REPLACE(row_var.MovieTitle, '\(\d{4}\)', '');
    category1     := REGEXP_SUBSTR(row_var.Category, '[^|]+', 1, 1);
    category2     := REGEXP_SUBSTR(row_var.Category, '[^|]+', 1, 2);
    category3     := REGEXP_SUBSTR(row_var.Category, '[^|]+', 1, 3);
    category4     := REGEXP_SUBSTR(row_var.Category, '[^|]+', 1, 4);
    category5     := REGEXP_SUBSTR(row_var.Category, '[^|]+', 1, 5);
    category6     := REGEXP_SUBSTR(row_var.Category, '[^|]+', 1, 6);
   
   --insert rows into the MoviesGenres table
   IF category1 IS NOT NULL THEN
    INSERT INTO MoviesGenres (MovieID, Genre)
      VALUES (row_var.MovieID, category1);
   END IF; 
  IF category2 IS NOT NULL THEN
    INSERT INTO MoviesGenres (MovieID, Genre)
      VALUES (row_var.MovieID, category2);
   END IF; 
   IF category3 IS NOT NULL THEN
    INSERT INTO MoviesGenres (MovieID, Genre)
      VALUES (row_var.MovieID, category3);
   END IF; 
   IF category4 IS NOT NULL THEN
    INSERT INTO MoviesGenres (MovieID, Genre)
      VALUES (row_var.MovieID, category4);
   END IF; 
   IF category5 IS NOT NULL THEN
    INSERT INTO MoviesGenres (MovieID, Genre)
      VALUES (row_var.MovieID, category5);
   END IF; 
   IF category6 IS NOT NULL THEN
    INSERT INTO MoviesGenres (MovieID, Genre)
      VALUES (row_var.MovieID, category6);
   END IF; 
     
    --update Movies table
    UPDATE Movies
    SET MovieTitle = newtitle,
        YearMade = foundyearmade
    WHERE CURRENT OF MoviesRowCursor;
        
  END LOOP;
  DBMS_OUTPUT.PUT_LINE('Number of records updated in manipulate_movies: ' ||
    TO_CHAR(MoviesRowCursor%ROWCOUNT)); 
  CLOSE MoviesRowCursor;
  EXECUTE IMMEDIATE alterstatement;
EXCEPTION
  WHEN DUP_VAL_ON_INDEX THEN
    DBMS_OUTPUT.PUT_LINE('Primary key violation from title: ' || newtitle);
    ROLLBACK;
END manipulate_movies;
/




--###################################
-- ALTER RATINGS TABLE
--###################################
CREATE OR REPLACE PROCEDURE manipulate_ratings_date AS
  CURSOR RatingsRowCursor IS
    SELECT  Timestamp,
            TrueDate
    FROM Ratings
    FOR UPDATE;
  row_var         RatingsRowCursor%ROWTYPE;
	newdate 	      DATE;
  alter_statement   VARCHAR2(1000) :=
    'ALTER TABLE Ratings
      DROP COLUMN Timestamp';   
BEGIN
  OPEN RatingsRowCursor;
  LOOP
    FETCH RatingsRowCursor INTO row_var;
    EXIT WHEN RatingsRowCursor%NOTFOUND;
    --calculate the true date from seconds since the epoch
    newdate := 
      to_date('19700101', 'YYYYMMDD') + ( 1 / 24 / 60 / 60 ) * row_var.Timestamp;
  
    --update the TrueDate
    UPDATE  Ratings
    SET     TrueDate = newdate
    WHERE   CURRENT OF RatingsRowCursor;
  END LOOP;
  DBMS_OUTPUT.PUT_LINE('Number of records updated in manipulate_ratings_date: ' 
    || TO_CHAR(RatingsRowCursor%ROWCOUNT));
  CLOSE RatingsRowCursor;
  EXECUTE IMMEDIATE alter_statement;
END manipulate_ratings_date;  
/


  
--###################################
-- CREATE FINAL SUMMARY OUTPUT
--###################################
CREATE OR REPLACE PROCEDURE final_ouput AS
  sql1  INT;
BEGIN
  DBMS_OUTPUT.PUT_LINE('Final Record Counts:' || sql1);
  SELECT COUNT(*) 
  INTO sql1
  FROM Movies;
  DBMS_OUTPUT.PUT_LINE(RPAD('Movies:', 20) || sql1);
  
  SELECT COUNT(*) 
  INTO sql1
  FROM MoviesGenres;
  DBMS_OUTPUT.PUT_LINE(RPAD('MoviesGenres:', 20) || sql1);
  
  SELECT COUNT(*) 
  INTO sql1
  FROM Ratings;
  DBMS_OUTPUT.PUT_LINE(RPAD('Ratings:', 20) || sql1);
  
  SELECT COUNT(*) 
  INTO sql1
  FROM Users;
  DBMS_OUTPUT.PUT_LINE(RPAD('Users:', 20) || sql1);  
  
  SELECT COUNT(*) 
  INTO sql1
  FROM Age_Groups;
  DBMS_OUTPUT.PUT_LINE(RPAD('Age_Groups:', 20) || sql1);  
  
  SELECT COUNT(*) 
  INTO sql1
  FROM Occupation_Groups;
  DBMS_OUTPUT.PUT_LINE(RPAD('Occupation_Groups:', 20) || sql1);  
  DBMS_OUTPUT.PUT_LINE('--------------------------------');   
END final_ouput;
/

--###################################
-- CALL PROCEDURES
--###################################
BEGIN
  initial_ouput;
  manipulate_movies;
  manipulate_ratings_date;
  final_ouput;
END;
/





--###################################
-- INTERESTING QUERY
--###################################
CREATE OR REPLACE PROCEDURE sql_query AS
	CURSOR occ_cursor IS
		SELECT *
		FROM Occupation_Groups;
  CURSOR join_cursor (occ Occupation_Groups.Occupation%TYPE) IS
		SELECT
			ROUND(AVG(join2.Rating),2) Avg_Rating,
			MoviesGenres.Genre
		FROM 
		    MoviesGenres INNER JOIN (
		      SELECT * 
		      FROM Ratings INNER JOIN (
		        SELECT *
		        FROM Users INNER JOIN OCCUPATION_GROUPS
		          ON Users.OccupationID = Occupation_Groups.OccupationID
		          ) join1
		      ON Ratings.UserID = join1.UserID
		      ) join2
		      ON MoviesGenres.MovieID = join2.MovieID
		WHERE Occupation = occ     
		GROUP BY Genre
		ORDER BY Avg_Rating DESC;
  join_var    join_cursor%ROWTYPE; 
  i           INT;
BEGIN
	DBMS_OUTPUT.PUT_LINE('Top 5 genres per occupation group:');
	FOR rec in occ_cursor 
	LOOP
    i := 0; --reset i for each occupation group
    DBMS_OUTPUT.PUT_LINE('');
    DBMS_OUTPUT.PUT_LINE(UPPER(rec.Occupation) || ':');
    DBMS_OUTPUT.PUT_LINE(RPAD('Rank',7) || RPAD('Genre', 15) || 'Avg. Rating');
    DBMS_OUTPUT.PUT_LINE(RPAD('-', 34, '-'));
    OPEN join_cursor(rec.Occupation);
    LOOP
      i := i +1;
      FETCH join_cursor INTO join_var;
      EXIT WHEN i > 5;
      DBMS_OUTPUT.PUT_LINE(RPAD(TO_CHAR(i) || '.', 7) || 
        RPAD(join_var.Genre, 15) || 
        TO_CHAR(join_var.Avg_Rating));
    END LOOP;
    CLOSE join_cursor;
  END LOOP;
END sql_query;
/

BEGIN
 sql_query;
END;
/
		
		
/*
Notes:
USE %ROWCOUNT ON THE CURSOR
DELETE 'FOR UPDATE' from cursors which aren't updating the row they're on (locking is expensive)
*/		
