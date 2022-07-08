# Demo Organisation Translator

## Parameter explanation
Parameters should be supplied as environmental arguments.
Application is expected to run as a docker container or kubernetes job.
Running als commandline application will require code changes.

### Database
- `spring.datasource.url` - The JDBC url of the databse
- `spring.datasource.username` - Username of the databse user with suffecient rights
- `spring.datasource.password` - Password for the user

### Application properties
- `application.filename` Filename of the csv file with the organisations (default is `organisations.csv` located in the resources folder)

## Installation instructions
### IDE
Pull the code from Github and open in your IDE.
Fill in the `application.properties` with the parameters described above.
Run the application.

### Docker
Ensure that parameters are either available as environmental variables are added in the `application.properties`.
Build the Dockerfile with `docker build . -t demo-organisations-translator`
Run the container with `docker run demo-organisations-translator`
