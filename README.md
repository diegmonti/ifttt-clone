# IFTTT-CLONE

## About
This project is a clone of the [IFTTT](https://ifttt.com) service realized using [Spring](https://spring.io) and [AngulasJS](https://angularjs.org) as final assignment for the course Applicazioni Internet of the Politecnico di Torino.

### Terminology
The user can create **recipes** that connect a **trigger** with an **action**: for example, a possible recipe is "if the temperature in Turin is above 30 °C, send me an email".

A **channel** is a set of triggers and actions that interact with a particular external service. For example, the Gmail channel interacts with Gmail and it is composed of a trigger (if I receive an email) and an action (send an email).

The trigger and the action have a set of **fields**: the values of the fields are specified by the user and they are linked to a particular recipe. The fields of the trigger in the example recipe are the name of the location and the value of the temperature, while the fields of the action are the email address, the subject and the text of the email.

A trigger may export a set of **ingredients**: the value of the ingredients can be inserted in the fields of the action. For example, the trigger "if the temperature is above" exports as ingredient the value of the temperature. If in a field of the action the user writes `{{CurrTempCelsius}}`, it will be substituted with the actual value.

### Architecture
The channels are classes inside the package *iftttclone.channels* that extend the abstract class *AbstractChannel*: each trigger or action correspond to a specific method and the fields are their parameters. A trigger returns a list of maps if the corresponding action needs to be invoked (eventually multiple times): the map represent the association among the name of an ingredient and the actual value. If there is no need to invoke the action, the trigger returns *null*.

The channels are annotated in order to describe how their methods can be used in a recipe. Each channel, trigger and action is associated with a name and a description. Each trigger is optionally associated with a set of ingredients: for each ingredient a name, a description and an example must be provided. Each field has a name, a description and a type. The type is used by the *iftttclone.core.Validator* class to check its syntactical correctness when a recipe is created or modified. A field is always serialized in the database as a string.

When the application is loaded the *iftttclone.core.DatabasePopulator* class reads the available annotations and stores them in the database. Every 15 minutes the *iftttclone.core.Scheduler* class processes the active recipes: for each recipe it invokes the trigger and if it is necessary also the action(s) parsing the fields in order to add the value of the ingredients. The *AbstractChannel* class is populated with information about the current user and the last time the recipe was run.

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
    - https://[DOMAIN]/[PATH]/api/channels/gmail/authorize
    - https://[DOMAIN]/[PATH]/api/channels/google_calendar/authorize
6. Select *Download JSON*

### src/main/resources/twitter4j.properties
This file is required in order to interact with the APIs of Twitter.

1. Go to [Twitter Apps](https://apps.twitter.com)
2. Create a new app
3. On *Website* put the domain of your application
4. On *Callback URL* put: https://[DOMAIN]/[PATH]/api/channels/twitter/authorize
5. Under the *Keys and Access Tokens* tab you will find your *Consumer Key* and *Consumer Secret*; put them in the file.

It is a good idea to check *Enable Callback Locking* and to encrypt the contents of the file.

    oauth.consumerKey=
    oauth.consumerSecret=

### src/test/resources/application.properties
This file is required only if you plan to run the tests related to the channels. You must enter the authorization tokens for Gmail, Google Calendar and Twitter: these tokens can be retrieved from the `channel_connector` table after having connected the channels.

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
    twitter.token=
    twitter.secret=

## Test
It is possible to run the tests related to the channels with the command `mvn test`.

This will create a set of recipes using a fake test channel and a regular channel: the result of their execution will be printed to the standard output.

## Compilation
In order to compile the project it is sufficient to run the command `mvn package`. This will create the resulting .war file.