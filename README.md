# IFTTT-CLONE

## About
...

## Configuration
Some custom configuration files are required in order to run the application. These files need to be created according to the examples provided.

### src/main/resources/application.properties
This file contains the configuration of **JDBC**, **Hibernate**, and the fixed delay at which the scheduler will run.
This application requires a relational database. The dependencies declared in the `pom.xml` file assume that the MariaDB database is used. When the application is deployed in production the `hibernate.hbm2ddl.auto` field must be set to validate; however, the first time that the application is run, in order to create the schema, it must be set to create.

    jdbc.url=jdbc:mysql://localhost:3306/iftttclone
    jdbc.username=root
    jdbc.password=
    hibernate.hbm2ddl.auto=validate
    hibernate.show_sql=false
    scheduler.fixedDelay=900000

### src/main/resources/client_secret.json
This file is required in order to interact with the APIs of Google for managing Gmail and Google Calendar.

1. Connect to the [Google APIs Console](https://console.developers.google.com)
2. Enable the Gmail and Google Calendar APIs
3. Add a new project and then create an OAuth client ID for a web application
4. Enter as *Authorized JavaScript origins* the domain of your application
5. Enter as *Authorized redirect URIs*:
    - http://[DOMAIN]/[PATH]/api/channels/gmail/authorize
    - http://[DOMAIN]/[PATH]/api/channels/google_calendar/authorize
6. Select *Download JSON*

### src/test/resources/application.properties
This file is required only if you plan to run the tests related to the channels. You must enter the authorization tokens for Gmail and Google Calendar: these tokens can be retrieved from the `channel_connector` table after having connected the channels.

    jdbc.url=jdbc:mysql://localhost:3306/iftttclone
    jdbc.username=root
    jdbc.password=
    hibernate.hbm2ddl.auto=create
    hibernate.show_sql=false
    scheduler.fixedDelay=30000
    gCalendar.token=
    gCalendar.refreshToken=
    gMail.token=
    gMail.refreshToken=
    gMail.selfAddr=

## Test
It is possible to run the tests related to the channels with the command `mvn test`.

This will create a set of recipes using a fake test channel and a regular channel: the result of their execution will be printed to the standard output.

## Compilation
In order to compile the project it sufficient to run the command `mvn package`. This will create the resulting .war file.