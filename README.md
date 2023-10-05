Spring application for self-service beer tap dispenser.

The application allows you to: 
- add beer dispensers 
- interact with them 
- get statistics on their use

For storage date used h2 in memory database

Api requirements [link](https://rviewer.io/jobs/java-developer-1696316516063)

Api specification - [link](https://rviewer.stoplight.io/docs/beer-tap-dispenser/juus8uwnzzal5-beer-tap-dispenser)

In database stored calculated data, to be able to update price and dispenser data without modifying existing data

###### Steps to start application:
1) mvn clean install
2) docker build --build-arg JAR_FILE=target/*.jar -t dispenserApp . 
3) docker run -p 8080:8080 dispenserapp
