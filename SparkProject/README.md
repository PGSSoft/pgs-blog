# Spark project
## Installation instructions
### Cloning repository
1. Go to directory https://bitbucket.pgs-soft.com.
2. Clone repository using `git clone sparkproject.git`
3. Go to module `analyzer` and execute from command prompt `npm install` and `bower install`
4. Import project with all submodules to IDE of your choose, it should be recognized as maven project.
### Apache Cassandra
1. Download Apache Cassandra from official page: http://cassandra.apache.org/
2. Install Apache Cassandra.
3. Go to `cassandra_dir/conf`
4. Open cassandra.yaml, find line `enable_user_defined_functions` and set it as `true`
5. Run Apache Cassandra.
6. Go to `cassandra_directory/bin` and open Command Prompt.
7. Type `cassandra`
8. Open another  Comment Prompt in `cassandra_directory/bin` and type `cqlsh` (WARNING: If you are using Cassandra version < 3.9
you have to install Python version < 2.7.11, on any higher version cqlsh does not work).
9. Paste here `create-schema.cql` content and execute.

##### Optional:
10. Download and install workbench for cassandra (I am using Dbeaver - Enterprise Edition supports Cassandra):
    http://dbeaver.jkiss.org/download/
11. Add connection to Cassandra in Dbeaver.
12. Go to keyspace `analyzer`

### Apache ActiveMQ
1. Download Apache ActiveMQ from official page: http://activemq.apache.org/download.html
2. Install Apache ActiveMQ.
3. Go to `activeMQ_directory/bin`, open command prompt and type activeMQ start
4. Open your web browser and go to http://localhost:8161/
5. Choose `Manage ActiveMQ broker`
6. Next choose `Queues`
7. Create two queues: `processingRequests` and `processingResults`

### MySQL
1. Download  and install MySQL (Database + Workbench optionaly):  https://www.mysql.com/downloads/
2. Add connection to database in MySQL workbench.
3. Create user with privileages to create databases, tables, sequences.
4. Create empty schema using this user.
5. In your cloned repository open `analyzer/pom.xml` find plugin `liquibase-maven-plugin`
    and provide your MySQL database credentials there (database address, username, password etc)
6. Now in the same module, find file `application-dev.yml` (`analyzer/src/main/resources/config/application-dev.yml`). In this file also provide credentials for your sql database.


### Importing example training data
1. Start all modules (algorithmComparator, analyzer, crawler, processor) using your IDE or from command prompt (`mvn spring-boot:run` for each module)
2. Open `localhost:8080`
2. Login as default admin (username: `admin`, password: `admin`).
3. Go to `Provide training data` and choose import training data from file (you can find example training data in `crawler/src/main/resources/data/amazon_cells_labeled.txt`)
4. Import validation data from file using similar method (now choose import validation data - you can find example validation data in `crawler/src/main/resources/data/amazon_cells_labeled_validation.txt`)
5. Now application should be ready to use - create search profile from entities -> search profile.
6. Create search criteria from entities -> search criteria, and choose previously created search profile for this criteria.
7. Now you can perform filtering and learning after filtering from jobs menu.