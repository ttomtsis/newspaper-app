# newspaper-app
### Introduction
This is part of a university course from University of the Aegean,
aiming to provide students with practical experience on software engineering best practices
and tools. 


### Concept

Java Spring Boot application that exposes a RESTful API servicing the day to day operations of a newspaper.

# Features
* RBAC with multiple users
* Basic Authentication with users persisted in a database
* OAuth2 Authentication with Auth0 as a provider
* Unit tests with JUnit
* HATEOAS design
* Hibernate ORM
* TLS v1.3 support

# Technology stack
* Java 17
* Spring Boot 3.1
* Spring Security 6.1
* Hibernate ORM 6.2
* JUnit 5

# Getting Started
### Installation
* Clone the repository `git clone https://github.com/ttomtsis/newspaper-app/tree/master`
* Build with maven, or the maven wrapper `./mvnw clean package`
* Go to the target directory `cd ./target`
* Run using java `java -jar NewspaperApp.0.0.1-SNAPSHOT.jar`

### Database Configuration
To configure the database that the application uses you must configure the following environment variables:
* **DATASOURCE_URL** - The URL where the database is accessible
* **DB_USERNAME** - The username that will be used to connect to the database
* **DB_PASSWORD** - The password that will be used to connect to the database

### OAuth2 Configuration
The applicatno supports OAuth2 authentication. Any OAuth2 provider can be used, but it is recommended that you use Auth0 as it is the only provider that has been tested so far
To configure the OAuth2 provider that the application uses, you must configure the following environment variables:
* **AUDIENCES** - The audiences that the JWT tokens must 
* **PROVIDER_JWKS** - URL where the provider's public key set is accessible 
* **PROVIDER_URI** - Provider's URI

### TLS Configuration
The application supports TLS v1.3 and provides a 'dummy' certificate by default.
To configure the application's TLS settings and use your own certificates, you must configure the following environment variables:
* **KEY_ALIAS** - The alias used for the provided certificate
* **KEY_LOCATION** - The location in the filesystem where the certificate resides
* **KEY_PASSWORD** - The password ( if a password was used ) used to encrypt the certificate
* **KEY_TYPE** - The type of the certificate used
  
# Endpoints
### Story

* `POST /stories` -- Create story 
* `PUT /stories/{id}` -- Modify story
* `DELETE /stories/{id}` -- Delete story 
* `GET /stories/ name = {name}` -- Search story, 
can take multiple parameters as input: content, minDate, maxDate, state
* `GET /stories` -- Show all stories
* `PATCH /stories/ {id} ? state = {state}` -- Submit, approve, reject, publish story
  also see Issue #10

### Comment

* `POST /comments` -- Create comment 
* `PUT /comments/ {id}` -- Modify comment
* `GET /stories/ {id} / comments` -- Show all comments for a story
* `PATCH /comments/ {id}` -- Approve a comment
* `DELETE /comments/ {id}` -- Reject a comment

### Topic

*  `POST /topics` -- Create topic
*  `GET /topics/ search ? name = {name}` -- Search topic
*  `PUT /topics/ {id}` -- Modify topic
*  `GET /topics/ {id}` -- Show topic
*  `GET /topics` -- Show all topics
*  `GET /topics/ {id} / stories` -- Show a topic's stories
*  `PATCH /topics/ {id}` -- Approve a topic
*  `DELETE /topics/ {id}` -- Reject a topic
