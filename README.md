# ATM Simulation Test

[![Build Status](https://travis-ci.org/golimpio/atm-simulation.png?branch=master)](https://travis-ci.org/golimpio/atm-simulation)


## Exercise

We need an application that simulates the Backend logic of a cash dispensing Automatic Teller Machine (ATM).

### Requirements
[Instructions and Requirements](https://github.com/golimpio/atm-simulation/blob/master/INSTRUCTIONS.md)


## Application

This application provides a lightweight RESTful API using the Java API for RESTful Web Services (JAX-RS) on top of an embedded Jetty.

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

### Target platforms

- Mac OSX (tested on 10.9 Mavericks)
- [Heroku](https://www.heroku.com/)
- Java stand alone application

### Minimum requirements

- Java 7
- Maven 3

### Running the application locally

First build it:

    $ mvn clean package

Then run it:

    $ mvn exec:java -Dexec.mainClass="com.github.golimpio.atm.Main"
    
Alternatively, there is a *run* script at the project root folder:

	$ ./run

You might need to give execution permission to it:

    $ chmod +x run
    
## API

The REST API can be accessed from: `http://atm-simulation-exercise.herokuapp.com/services`

Or from your localhost if you're running the application loccaly: `http://localhost:8080/services`

### Services

Following the available services:

1. **init** (initialise the ATM - *GET*)
2. **init/clear** (remove all de available money from the ATM - *POST*)
3. **init/add** (add money to the ATM - *POST*)
4. **withdraw** (allow to retrieve money and verify minimum and maximum withdraw allowed - *POST*)
5. **withdraw/** (withdraw cash from the ATM - *POST*)
5. **withdraw/minimum** (retrieve the minimum withdraw allowed - *GET*)
5. **withdraw/maximum** (retrieve the maximum withdraw allowed - *GET*)

If the request succeeds, the response’s content type will be *JSON*.


### Command line interface

The simplest way to access the service API from the command line is via CURL (a command line tool for transferring data with URL syntax).

#### Initialise the ATM removing all the available money from it (2)

**Request:**

curl -X POST -H "Content-Type: text/plain" -d $'bread,10,slices,25/12/2014\ncheese,5,slices,3/2/2015' http://recipe-finder.herokuapp.com/services/fridge/add

The **response** will be a JSON with a suggestion for dinner:

	{
	    "message": "Suggestion for dinner",
	    "recipe": {
	        "name": "grilled cheese on toast",
	        "ingredients": [
	            {
	                "item": "bread",
	                "amount": 2,
	                "unit": "slices"
	            },
	            {
	                "item": "cheese",
	                "amount": 2,
	                "unit": "slices"
	            }
	        ]
	    }
	} 
