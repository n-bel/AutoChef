# AutoChef : Projet de génie logiciel et gestion de projet (INFO-F-307) - Groupe 04


## Table of Contents
* [Libraries](#libraries)
* [Project structure](#project-structure)
* [Gradle Usage](#gradle-usage)
* [Compilation](#compilation)
* [Running the app](#run)
* [Configuration](#configuration)
* [Tests](#tests)
* [Misc](#misc)
* [JSON](#json-structure)
* [Contributions](#contributions)

---
## Libraries 

* Java 17.0.2
* JavaFX 17.0.2
* Gradle 7.4
* Sqlite 3.36
* Arcgis 100.13.0
* Org.Json 2022.03.20
* IText 7.2.2
* JavaMail 1.6.2

### Tools

* SpotBugs 4.6.0
* Junit 5.8.1
* Test-logger 3.2.0

---
## Project structure

```
├── dist/
│   └── gxx-iteration-i.jar
├── lib/
├── doc/
├── src/
│   ├── main
│   │   ├── java
│   │   │   ├── com
│   │   │   │   └── g04autochef
│   │   └── resources
├── test
│   └── java
├── team/
├── build.gradle
├── settings.gradle
├── README.md
```

* The main source package is g04autochef. 
* Source code should be put in packages under main/java/com.g04autochef
* Resources should be placed under main/resources and follow the same package structure as where they calling code is placed.
* Tests must be placed into test/

---
## Gradle usage
For compilation and running please see the sections below.

To only run tests run:
``` 
./gradlew test
```
To use spotbugs run either an `assemble` or `build` task and use the `spotBugs` project property.
For example:
``` 
./gradlew assemble -P spotBugs
``` 

For more details on available tasks please use gradle's built-in `tasks` feature:
``` 
./gradlew tasks
``` 

To build javadoc run:
``` 
./gradlew javadoc
``` 
Javadoc will be generated and placed into `build/docs`

---
## Compilation

This project uses Gradle.

To build the project jar run:
``` 
./gradlew build
```

To build the project jar run:
``` 
./gradlew build -P makeJar
```

## Run 
Needed before running for the map:
Run :
``` 
./gradlew copyNatives 
```
To download and setup the arcgis runtime library for the map.

Then to simply run jar run from `dist/`:
``` 
java -jar nameOfJar.jar
```
The dist folders contains the jar built (will include "all" in name) and the jars of each iteration version.
The jar file can be double-clicked using a desktop environment. (Hint: execute permission will be needed on GNU/Linux)

Or use gradle to run project:
``` 
./gradlew run
```

# Configuration 

# Tests
To run tests use:
``` 
./gradlew test
``` 

---
---
# Misc
## JSON structure
It is possible to import recipe from json file. The json must have the following values :
* TypePlat : string
* StylePlat : string
* Nom : string
* NombrePersonne : integer
* Preparation : array of strings
* Ingredients : array of objects with 
  * unite : string
  * nom : string
  * quantite : integer

If some ingredients haven't been created in the application, they will not be added

# Contributions

Write daily hour contributions for task in the `ITX_Hour_Log` sheet on the burndown.


## License
