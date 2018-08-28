# Database-Programming
This is my final project for a Database Programming class at DePaul University. 
It demonstrates an ability to use Java as well as PL/SQL for creating a normalized database (3NF), parsing files, 
manipulating data, loading tables, and running advanced queries.

## Sample Output
    Loading JDBC driver...

    Connecting to DEF database...

    Connected to database DEF...

    Dropping old tables...

        movies table dropped...

        moviesgenres table dropped...

        agegroups table dropped...

        occupationgroups table dropped...

        users table dropped...

        ratings table dropped...

    Building new tables...

        SUCCESS tablemoviesbuilt

        SUCCESS tablemoviesgenresbuilt

        SUCCESS tableagegroupsbuilt

        SUCCESS tableoccupationgroupsbuilt

        SUCCESS tableusersbuilt

        SUCCESS tableratingsbuilt

    Parsing movies...

        SUCCESS movies and moviesgenres populated

    Parsing age_groups...

        SUCCESS agregroups populated

    Parsing occupation_groups...

        SUCCESS occupationgroups populated

    Parsing users...

        SUCCESS users populated

    Parsing ratings...

        SUCCESS ratings populated

    Final table record counts:
        movies:             3883

        moviesgenres:       6408

        agegroups:          7

        occupationgroups:   21

        users:              6040

        ratings:            105059



    Top 5 genres per occupation group:

    other or not specified:
    Rank   Genre          Avg. Rating
    ----------------------------------
    1      Film-Noir      4.09           
    2      Documentary    3.99           
    3      War            3.97           
    4      Drama          3.95           
    5      Musical        3.84           

    academic/educator:
    Rank   Genre          Avg. Rating
    ----------------------------------
    1      War            4.04           
    2      Film-Noir      3.97           
    3      Documentary    3.96           
    4      Drama          3.9            
    5      Mystery        3.82           

    artist:
    Rank   Genre          Avg. Rating
    ----------------------------------
    1      Film-Noir      4.1            
    2      Documentary    3.98           
    3      War            3.86           
    4      Mystery        3.76           
    5      Animation      3.71           

    clerical/admin:
    Rank   Genre          Avg. Rating
    ----------------------------------
    1      Documentary    4.17           
    2      War            3.97           
    3      Film-Noir      3.9            
    4      Animation      3.9            
    5      Musical        3.87           

    college/grad student:
    Rank   Genre          Avg. Rating
    ----------------------------------
    1      Film-Noir      4.01           
    2      Documentary    4              
    3      War            3.79           
    4      Drama          3.61           
    5      Animation      3.58           

    customer service:
    Rank   Genre          Avg. Rating
    ----------------------------------
    1      Documentary    5              
    2      Western        4.34           
    3      Crime          4.29           
    4      Mystery        4.26           
    5      War            4.21           

    doctor/health care:
    Rank   Genre          Avg. Rating
    ----------------------------------
    1      Documentary    4.16           
    2      Animation      4.09           
    3      Film-Noir      4.05           
    4      War            4.03           
    5      Musical        3.91           

    executive/managerial:
    Rank   Genre          Avg. Rating
    ----------------------------------
    1      Film-Noir      4.08           
    2      War            3.93           
    3      Documentary    3.88           
    4      Drama          3.81           
    5      Animation      3.8            

    farmer:
    Rank   Genre          Avg. Rating
    ----------------------------------
    1      Film-Noir      4.05           
    2      War            3.95           
    3      Documentary    3.89           
    4      Musical        3.79           
    5      Drama          3.75           

    homemaker:
    Rank   Genre          Avg. Rating
    ----------------------------------
    1      Film-Noir      4.03           
    2      Musical        3.9            
    3      War            3.8            
    4      Animation      3.78           
    5      Crime          3.75           

    K-12 student:
    Rank   Genre          Avg. Rating
    ----------------------------------
    1      Romance        3.91           
    2      War            3.84           
    3      Musical        3.82           
    4      Adventure      3.8            
    5      Drama          3.75           

    lawyer:
    Rank   Genre          Avg. Rating
    ----------------------------------
    1      Documentary    4.48           
    2      Film-Noir      4.48           
    3      War            4.16           
    4      Musical        4.04           
    5      Drama          3.93           

    programmer:
    Rank   Genre          Avg. Rating
    ----------------------------------
    1      Film-Noir      4.3            
    2      War            4.02           
    3      Drama          3.96           
    4      Musical        3.84           
    5      Animation      3.83           

    retired:
    Rank   Genre          Avg. Rating
    ----------------------------------
    1      War            4.11           
    2      Film-Noir      4.03           
    3      Drama          3.89           
    4      Mystery        3.86           
    5      Romance        3.85           

    sales/marketing:
    Rank   Genre          Avg. Rating
    ----------------------------------
    1      Film-Noir      4.12           
    2      Documentary    3.75           
    3      Crime          3.68           
    4      Mystery        3.68           
    5      Western        3.66           

    scientist:
    Rank   Genre          Avg. Rating
    ----------------------------------
    1      Film-Noir      4.38           
    2      Western        4.24           
    3      War            4.13           
    4      Musical        4              
    5      Drama          3.98           

    self-employed:
    Rank   Genre          Avg. Rating
    ----------------------------------
    1      Film-Noir      4.16           
    2      Documentary    3.95           
    3      War            3.91           
    4      Animation      3.74           
    5      Drama          3.65           

    technician/engineer:
    Rank   Genre          Avg. Rating
    ----------------------------------
    1      Documentary    4.14           
    2      Film-Noir      3.95           
    3      War            3.92           
    4      Drama          3.87           
    5      Animation      3.82           

    tradesman/craftsman:
    Rank   Genre          Avg. Rating
    ----------------------------------
    1      War            4.1            
    2      Documentary    3.87           
    3      Drama          3.85           
    4      Mystery        3.83           
    5      Animation      3.77           

    unemployed:
    Rank   Genre          Avg. Rating
    ----------------------------------
    1      Film-Noir      4.56           
    2      War            4              
    3      Animation      3.75           
    4      Mystery        3.7            
    5      Crime          3.65           

    writer:
    Rank   Genre          Avg. Rating
    ----------------------------------
    1      Film-Noir      4.04           
    2      Documentary    3.89           
    3      War            3.87           
    4      Crime          3.74           
    5      Animation      3.69           
