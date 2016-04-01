TwitterHeatMap
==============

This application collects tweets from the twitter hose in real time and displays them on the map in the form of a heat map. I used the location(latitude and longitude) information from the tweet metadata to locate it on the map. 

TwittMap
--------
Following are the details of the application:
- Fetches tweets from twitter and store it in DynamoDB
- Uses Google Maps API to render tweets on the screen using markers
- Provides a dropdown of keywords to display only those tweets which contain the keyword
- Displays the tweets on the map in real-time without refreshing the page using WebSockets
- Show charts with statistics on each keyword in the  dropdown using HighCharts

Technologies Used
-----------------
- Spring for RESTful API
- Java Websockets
- Java 8
- Twitter4j
- Amazon AWS SDK
- DynamoDB
- Highcharts

How it works
------------
Please ensure that you add your AwsCredentials.properties to com.twitter package. Also, update the consumer key, consumer secret, access token and access token secret in the TwitterWebSocket.java file. Launch the application in Tomcat server.

This application was later deployed on AWS Elastic Beanstalk in a Java 8 and Tomcat server environment.
