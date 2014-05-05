# ATM Simulation Test


## Build status

[![Build Status](https://travis-ci.org/golimpio/atm-simulation.svg?branch=master)](https://travis-ci.org/golimpio/atm-simulation)


## Exercise

An application that simulates the backend logic of a cash dispensing *Automatic Teller Machine *(ATM).


### Requirements
[CLick here for reading the instructions and requirements](https://github.com/golimpio/atm-simulation/blob/master/INSTRUCTIONS.md)


## Application

This application provides a lightweight RESTful API using the Java API for RESTful Web Services (JAX-RS) on top of an embedded Jetty web application.

A deployed version is running and hosted on: [http://atm-simulation-exercise.herokuapp.com/](http://atm-simulation-exercise.herokuapp.com/)


### Frameworks

Server:

- [Jetty - Servlet Engine and HTTP Server](http://www.eclipse.org/jetty/)
- [Jersey - RESTful Web Services in Java](https://jersey.java.net/)
- [FasterXML - Jackson JAX-RS JSON Provider](https://github.com/FasterXML/jackson-jaxrs-json-provider)
- [Guava Libraries - Set of common libraries for Java](https://code.google.com/p/guava-libraries/)

Test:

- [TestNG](http://testng.org/)
- [AssertJ - Fluent assertions for Java](http://joel-costigliola.github.io/assertj/index.html)
- [Mockito - Mocking framework](https://code.google.com/p/mockito/)


### Design patterns

The design pattern used as a base for solving the cash dispenser problem was the *Chain of Responsibility*.

The typical implementation of a *Chain of Resposibility* for this specific problem (ATM simulation) has a flaw, mainly because it doesn't care if the request will be fully satisfied at the time the last object in the chain handles the request.

For example:

Assuming that the ATM has notes of $20 and $50 only (and it has enough to fulfil the withdraw):

- We want to withdraw $80 
- There are two cash dispensers, one for $50 and other for $20 bills.
- The first object will dispense 1 X $50
- The second and last object will dispense 1 X $20
- There is no object in the chain to handle the $10 balance left and it will make the request fail

If the chain start with the $20 dispenser, it should work, but then another problem is introduced, because the small bills will finish a lot faster, creating an additional cost of constantly feeding the ATM.

The proposed solution, is still using this design pattern, but it will check if the last object in the chain will satisfy the request (the balance must be zero):
[Dispenser.java](https://github.com/golimpio/atm-simulation/blob/master/src/main/java/com/github/golimpio/atm/services/Dispenser.java)


### Target platforms

- Mac OSX (tested on 10.9 Mavericks)
- [Heroku](https://www.heroku.com/)
- Java stand alone application

It should work on Linux, but it wasn't tested with OpenJDK.


### Minimum requirements

- Java 7
- Maven 3


### Running the application locally

Building the application:

    $ mvn clean package

Running it:

    $ mvn exec:java -Dexec.mainClass="com.github.golimpio.atm.Main"
    
Alternatively, there is a *run* script at the project root folder:

	$ ./run

You might need to give execution permission to it:

    $ chmod +x run


## API

The REST API can be accessed from: `http://atm-simulation-exercise.herokuapp.com/services`

Or from your localhost if you're running the application locally: `http://localhost:8080/services`


### Services

Following the available services:

1. **init** (initialise the ATM - *GET*)
2. **init/clear** (remove all de available money from the ATM - *POST*)
3. **init/add** (add money to the ATM - *POST*) 
4. **withdraw** (allow withdraw money and verify minimum and maximum amounts allowed - *GET*)
5. **withdraw/** (withdraw cash from the ATM - *GET*)
6. **withdraw/minimum** (retrieve the minimum withdraw allowed - *GET*)
7. **withdraw/maximum** (retrieve the maximum withdraw allowed - *GET*)
8. **monitor/money** (retrieve the amount of notes available for withdraw - *GET*)


### Command line interface

The simplest way to access the service API from the command line is via CURL (a command line tool for transferring data with URL syntax).

**Note:** replace the url from: `http://localhost:8080/services/...` to: `http://atm-simulation-exercise.herokuapp.com/services/...` for using the heroku deployment.


#### Initialise the ATM removing all the available money from it (2)

**Request:**

	curl -X POST -H "Content-Type: application/json" http://atm-simulation-exercise.herokuapp.com/services/init/clear
	
or

	curl -X POST -H "Content-Type: application/json" http://localhost:8080/services/init/clear


The **response** will be a HTTP status OK if it successed or other status if it failed.


#### Add money to the ATM (3)

**Request:**

	curl -X POST -H "Content-Type: application/json" -d $'[ { "note":"TWENTY", "numberOfNotes":150 }, { "note":"FIFTY", "numberOfNotes":40 } ]' http://atm-simulation-exercise.herokuapp.com/services/init/add

or

	curl -X POST -H "Content-Type: application/json" -d $'[ { "note":"TWENTY", "numberOfNotes":150 }, { "note":"FIFTY", "numberOfNotes":40 } ]' http://localhost:8080/services/init/add

Allowed notes:

- FIVE
- TEN
- TWENTY
- FIFTY
- HUNDRED

The **response** will be a HTTP status OK if it successed or other status if it failed.


#### Withdraw (5)

	curl -X GET -H "Content-Type: text/plain" -d $'90' http://atm-simulation-exercise.herokuapp.com/services/withdraw/

or

	curl -X GET -H "Content-Type: text/plain" -d $'90' http://localhost:8080/services/withdraw/

**Response:**

	  [
    	{
	      "note":"FIFTY",
	      "numberOfNotes":1
	    },
	    {
	      "note":"TWENTY",
	      "numberOfNotes":2
	    }
	  ]


#### Retrieve the minimal allowed withdraw (6)

**Request:**

	curl -X GET -H "Content-Type: application/json" http://atm-simulation-exercise.herokuapp.com/services/withdraw/minimum
	
or

	curl -X GET -H "Content-Type: application/json" http://localhost:8080/services/withdraw/minimum

**Response:**

	{ "value":20 }


#### Retrieve the maximum allowed withdraw (7)

**Request:**

	curl -X GET -H "Content-Type: application/json" http://atm-simulation-exercise.herokuapp.com/services/withdraw/maximum

or

	curl -X GET -H "Content-Type: application/json" http://localhost:8080/services/withdraw/maximum

**Response:**

	{ "value":5000 }


#### Retrieve the amount of notes available for withdraw (8)

**Request:**

	curl -X GET -H "Content-Type: application/json" http://atm-simulation-exercise.herokuapp.com/services/monitor/money

or

	curl -X GET -H "Content-Type: application/json" http://localhost:8080/services/monitor/money

**Response:**

	  [
	    {
	      "note":"TWENTY",
	      "numberOfNotes":150
	    },
	    {
	      "note":"FIFTY",
	      "numberOfNotes":40
	    }
	  ]
  

