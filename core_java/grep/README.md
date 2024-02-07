# Grep Application

## Overview
This project is a robust simulation of the 'grep' Command Line Tool in Linux, implemented in Core Java and Maven for seamless build management. Docker is used for the deployment. Users can effortlessly specify a regex pattern and the directory to start the search from, allowing the tool to efficiently store all the matched lines in an output file matching the given criteria for further analysis. This app is cross-platform meaning it is compatible on all Operating Systems.

## Quickstart
### Directly with JAR file
1. After cloning the project from GitHub, open the project in any of your Java IDE.
2. Navigate to the project directory (Same level as `pom.xml` file) from the terminal and run the command `mvn clean install` to build the project.
3. Once the project is built successfully, run the project with `java -jar [name-of-jar-file.jar] [Regex-Pattern] [root-directory-to-start] [output-file]`

### With Docker
NOTE: If you are using Linux, you'll have to download and install [Docker Engine](https://docs.docker.com/engine/install/) first. If you're using Windows or MacOS, you'll need [Docker Desktop](https://www.docker.com/products/docker-desktop/) which will have Docker Engine.

1. Download the Docker Image from [here](https://hub.docker.com/repository/docker/clandoor/grep/general).
2. Once you have the image, you can easily create and run a container instantly.
3. To create and run the container, run the following command below.<br>
`docker run --rm \
   -v pwd/data:/data -v pwd/log:/log \
   ${docker_user}/grep Shakespeare /data /log/grep.out`
   
   Option `-rm` will instantly delete the container once it is done running.<br> 
   Option `-v` will attach volumes inside the container to keep data (Matched lines in this case) in sync.<br>
   The character "\" is just for a new line in terminal for better readability since the command is lengthy.
4. This command creates and runs a container which will search for the lines with pattern "Shakespeare" in all the files inside the directory `data` storing all the matched lines inside `/log/grep.out` file.
   

## Implementation
1. Setting up GitHub
   - Creating a base project structure on the remote repository.
   - Creating appropriate branches as per the GitFlow strategies.
2. Setting up Maven
   - Structuring the Project and Project files as per the standards of Maven.
   - Setting up `pom.xml` file with dependencies required for the project along with other project's metadata.
3. Approaching the Problem
   - Studied the behavior of Grep tool in linux.
   - Brainstormed different approaches to implement in Java.
4. Implementing Code
   - Wrote an Interface `JavaGrep` which would encapsulate all the functions and behavior of the Grep app.
   - Wrote a Class file `JavaGrepImp` which implemented the interface adding behavior to the application.
5. Testing
   - Tested the Grep application with a wide range of file trees and datasets to check its robustness.
   - Tested the Grep application on different platforms to ensure its cross-platform compatibility.
6. Deployment
   - Utilized Docker to create an image of the .jar file which contained JDK, JRE and JVM.
   - Containerized the app and uploaded the image in Docker Hub for public use.

## Code Files
   - `ca.jrvs.apps.grep.Javagrep.java` - Interface
   - `ca.jrvs.apps.grep.JavaGrepImp.java` - Class implementing the interface
   - `ca.jrvs.apps.bettergrep.JavaGrep.java` - Interface consisting the skeleton for an optimized version of the application.
   - `ca.jrvs.apps.bettergrep.JavaGrepImp.java` - Optimized Implementation of the Grep application.


## Deployment
Docker and GitHub were utilized to deploy this application.
Base image `amazoncorretto:17-alpine3.19` was utilized.
Containerization was implemented encapsulating all the necessary dependencies inside a container allowing it to be used efficiently across all platforms.
GitHub along with GitFlow branching strategy was implemented to deliver and write consistent code.

## Optimization
### Issue
The original application used lists to store and move the data along with legacy file I/O utilities.
As a result, all the data from the file was loaded directly into the heap further storing it in concrete data structures.
This was an issue if the files were gigantic as the heap would not be able to allocate substantial amount of memory in one call beyond its capacity.

### Solution
As a result, streams were utilized along with Java's better File I/O utilities to process the file data reliably regardless of the file sizes.

## Future Enhancements
1. Diversify the Exception Handling which will handle various types of different exceptions. As a result, it will be easier to debug, and it will be more user-friendly.
2. Introduce a graphical version of the grep app which will allow the users to dynamically input regex patterns and see instant results.
3. Add a functionality enabling the end users to enable and disable case sensitivity.