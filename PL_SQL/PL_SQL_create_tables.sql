--###################################
-- CREATE TABLES
--###################################
CREATE OR REPLACE PROCEDURE create_tables AS
  Movies_tbl        VARCHAR(1000) :=
    'CREATE TABLE Movies
    (
      MovieId INT, 
      MovieTitle VARCHAR2(100), 
      Category VARCHAR2(100),
      YearMade INT,
    
    CONSTRAINT Movies_PK
      PRIMARY KEY(MovieId)
    )';

  MoviesGenres_tbl  VARCHAR(1000) :=
    'CREATE TABLE MoviesGenres
    (
    MovieId INT,
    Genre VARCHAR2(100),
    
    CONSTRAINT MoviesGenres_PK
      PRIMARY KEY(MovieId, Genre),
      
    CONSTRAINT MoviesGenres_FK1
      FOREIGN KEY(MovieId)
        REFERENCES Movies(MovieId)
    )';   

  Age_Groups      VARCHAR2(1000) :=
    'CREATE TABLE Age_Groups
      (
      AgeID INT, 
      Age VARCHAR2(10),
      
      CONSTRAINT Age_Groups_PK
        PRIMARY KEY(AgeID)      
      )';
      
  Occupation_Groups  VARCHAR2(1000) :=
    'CREATE TABLE Occupation_Groups
      (
      OccupationID INT, 
      Occupation VARCHAR2(100),

      CONSTRAINT Occupation_Groups_PK
        PRIMARY KEY(OccupationID)         
      )';       

  Users_tbl         VARCHAR(1000) :=
    'CREATE TABLE Users
    (
    UserID INT, 
    Gender VARCHAR2(1), 
    AgeID INT, 
    OccupationID INT, 
    Zipcode VARCHAR2(15),
    
    CONSTRAINT Users_PK
      PRIMARY KEY(UserID),
      
    CONSTRAINT Users_FK1
      FOREIGN KEY(AgeID)
        REFERENCES Age_Groups(AgeID),     
        
    CONSTRAINT Users_FK2
      FOREIGN KEY(OccupationID)
        REFERENCES Occupation_Groups(OccupationID)        
      )';
      
  Ratings_tbl      VARCHAR2(1000) :=
    'CREATE TABLE Ratings (
      UserID INT,
      MovieId INT,  
      Rating INT, 
      Timestamp BINARY_FLOAT,
      TrueDate DATE,

    CONSTRAINT Ratings
      PRIMARY KEY(MovieId, UserID),
      
    CONSTRAINT Ratings_FK1
      FOREIGN KEY(UserID)
        REFERENCES Users(UserID),
        
    CONSTRAINT Ratings_FK2
      FOREIGN KEY(MovieId)
        REFERENCES Movies(MovieId)         
      )';     
BEGIN
  EXECUTE IMMEDIATE Movies_tbl;
  EXECUTE IMMEDIATE MoviesGenres_tbl;
  EXECUTE IMMEDIATE Occupation_Groups; 
  EXECUTE IMMEDIATE Age_Groups;
  EXECUTE IMMEDIATE Users_tbl;
  EXECUTE IMMEDIATE Ratings_tbl;
END create_tables;
/

BEGIN 
  create_tables;
END;

/* 
TABLES ARE POPULATED USING THE SQL LOADER
*/