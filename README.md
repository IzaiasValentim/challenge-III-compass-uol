# CHALLENGE 3 - WEEK 12:

## The project being delivered consists of a back-end system that allows the creation of improvement ideas to be voted on in a session by the organization's employees. Below I will address some important points of each microservice involved and their functionalities and ultimately the application setup.

### MS-EUREKA:

Eureka, is a component of the Spring Cloud ecosystem that provides service discovery and microservices registration functionalities. Spring Eureka is an implementation of the "Service Discovery" pattern and is used to create distributed systems made up of multiple microservices that need to dynamically locate and communicate with each other.

- Microservices registered in eureka can be viewed at the following URL: http://localhost: 8761

## MS-GATEWAY:

A gateway has a crucial role in routing and load balancing. In this project, the gateway aims to direct client requests to the appropriate microservices based on criteria such as URL.

## MS-EMPLOYEES:

This microservice is the starting point for registering employees and defining their role in the company. Registration is important because during the improvement sessions, only votes from registered users will be accepted.

- Endpoints:

*The endpoints can be viewed in the postan collection present at the root of the current branch.

- GET type: /employees

**Will return the list with all registered employees

- GET type: /employees/{cpf}

**{cpf} must be replaced with the employee's data and the success request will return whether or not the respective employee will be able to vote.

- POST type: /employees

** In this request, an attempt will be made to register an employee. The data must be entered in the body-raw of the request in json format. The CPF will be validated in accordance with generation and minimum size standards. There will only be registration if the CPF is valid.

Format:

{

    "cpf":"Valid-cpf",

    "name":Name-employee",

    "userRole":"Role"

}

- PATCH type: /employees/{cpf}

** An employee's occurrence may be changed. The data must be entered in form-data, with the following parameters:

NewName

NewUserRole

- DELETE type: /employees/{cpf}

**An employee's occurrence will be excluded if it exists in the database.

## MS-IMPROVEMENTS:

The improvement microservice will make possible to create improvement ideas that will be voted by employees. Each improvement will be registered with a session time which will be open for voting. If the voting time is not specified, it will be 1 (one) minute.

Endpoints:

- GET type: /improvements

** It will return a list with all the improvements present in the database and their information such as vote and status.

- GET type: /improvements/{id}

**Will return an occurrence improvement equal to {id}.

- POST type: /improvements

**Register an improvement with an exclusive voting time, if informed. The improvement data must be informed in the body-raw of the request in json format.

Format:

{

    "improvementToSave":{

       "name":"New-Improvement-Name",

       "description":"New-Description"

    },

    "timeSessionOfVotes":3

}

- POST type: /improvements/{id}

**Endpoint for voting. The {id} refers to the occurrence of improvement and the vote must be informed in the form-data, with the following parameters:

voute

*0 for rejected and 1 for approved.

CPF

--> After the voting session closes, the improvement will have a “CLOSED” status and the votes will be counted and the result finalized. This result will be sent to a Queue in RabbitMq: “monitoring-improvements-status" opening doors for future applications and other access.

- DELETE type: /improvements/{id}

**The occurrence of an improvement will be excluded if it exists in the database.

- PATCH type: /improvements/{id}

** The occurrence of an improvement may be changed. The data must be entered in form-data, with the following parameters:

NewName

newDescription

## Setup:

- The Project will be available in two versions, with and without docker configurations, the one without will be in the main(Relative at V1.0) branch, and the one with docker is V.1.0-docker.

### Without docker:

  To use the version without Docker, the project contained in the respective branch will be imported into the Intellij IDE and configured with JDK 17 (“corretto-17”).

  After import, the pom.xml files for each microservice must be added to Maven. After recognition it will be possible to run the application.

  run RabbitMq on docker and create the Queue: 
  - run: run rabbitmq: docker run --name rabbit-container -p 5672:5672 -p 15672:15672 --network arq-ms-net rabbitmq:3.12-management
  - create the Queue: monitoring-improvements-status
  
### With docker:

-To use the comdocker version, the project contained in the respective branch will be imported into the Intellij IDE and configured with JDK 17 (“corretto-17”).

-It is necessary to generate the executable of each application (microservice) through the command carried out in the root folder of each individual project:

  ./mvnw clean package –DskipTests

--The executable will be added to the target folder.

-Having the executable of each application related to a microservice, it will be possible to create the docker image, since the Dockerfile file is already present. So, in each microservice project, generate its image using the command run in the root folder of each individual project:

For ms-eureka:

docker build --tag ms-eureka .

For ms-improvements:

docker build --tag ms-improvements .

For ms-employee:

docker build --tag ms-employee .

For ms-gateway:

docker build --tag ms-gateway .

-Create a network and start containers(including RabbitMq), using terminal commands:

create network, run : docker network create arq-ms-net

run ms-eureka: docker run --name ms-eureka-docker -p 8761:8761 --network arq-ms-net ms-eureka

run rabbitmq: docker run --name rabbit-container -p 5672:5672 -p 15672:15672 --network arq-ms-net rabbitmq:3.12-management

run ms-improvements: docker run --name ms-improvements --network arq-ms-net ms-improvements

run ms-employee: docker run --name ms-employee --network arq-ms-net ms-employee

run gateway: docker run --name ms-gateway -p 8765:8765 --network arq-ms-net ms-gateway
