# jira2testrail

## Abstract

This project is an answer to a "challenge" on how a team can easily improve their productivity by automating certain tasks. Here we deal with creating a section and a case in
 TestRail, given a JIRA ticket following some conventions and template.

## Functional

From a JIRA ticket, click on a link to the application. This creates a section in TestRail, and a case with preconditions and steps prefilled from JIRA content.
Cherry on the cake: a link is added in the JIRA ticket to the TestRail case.
 
## Technical

* the code is in Groovy:
    * prototyping is faster and easier with Groovy than with Java and Kotlin (no need to mention Scala), and for a trained developer as fast as with other languages such as Python.
    * Groovy is lenient and so deals easily with XML, JSON, maps etc. as if they were strongly typed objects
    * Groovy is more user-friendly for unit tests
* the project is built SpringBoot. In Java ecosystem, SpringBoot is famous for its ability to release a complete runnable microservice with a minimum of effort.
* warning: this is a prototype, therefore:
    * errors are not handled
    * we assume everything matches the templates as expected     

## TODO

Had I had more time, I would:
* handle errors
* add unit tests
* etc.
* fake server to emulate JIRA and TestRail APIS. Usually I do that with small SpringBoot projects. Here, this fake server would have served the sample responses I started to store in folder `etc/`