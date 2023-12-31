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

# Technology stack
* Java 17
* Spring Boot 3.1
* Spring Security 6.1
* Hibernate ORM 6.2
* JUnit 5

# Getting Started
* Clone the repository `git clone https://github.com/ttomtsis/newspaper-app/tree/master`
* Build with maven, or the maven wrapper `./mvnw clean package`
* Go to the target directory `cd ./target`
* Run using java `java -jar NewspaperApp.0.0.1-SNAPSHOT.jar`
* 
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
