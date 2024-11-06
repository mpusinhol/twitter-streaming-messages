# Exercise #

You are requested to develop a simple application that covers all the requirements listed below. To have an indication of the criteria that will be used to judge your submission, all the following are considered as metrics of good development:

+ Correctness of the implementation
+ Decent test coverage
+ Code cleanliness
+ Efficiency of the solution
+ Careful choice of tools and data formats
+ Use of production-ready approaches

While no specific time limit is mandated to complete the exercise, you will be asked to provide your code within a given deadline from Sytac HR. You are free to choose any library as long as it can run on the JVM.

## Task ##

We would like you to write code that will cover the functionality explained below and provide us with the source, instructions to build and run the application, as well as a sample output of an execution:

+ Connect to [Twitter Streaming API 1.1](https://developer.twitter.com/en/docs/twitter-api/v1/tweets/filter-realtime/overview)
    * Use the following values:
        + Consumer Key: `RLSrphihyR4G2UxvA0XBkLAdl`
        + Consumer Secret: `FTz2KcP1y3pcLw0XXMX5Jy3GTobqUweITIFy4QefullmpPnKm4`
    * The app name will be `java-exercise`
    * You will need to login with Twitter
+ Filter messages that track on "bieber"
+ Retrieve the incoming messages for 30 seconds or up to 100 messages, whichever comes first
+ Your application should return the messages grouped by user (users sorted chronologically, ascending)
+ The messages per user should also be sorted chronologically, ascending
+ For each message, we will need the following:
    * The message ID
    * The creation date of the message as epoch value
    * The text of the message
    * The author of the message
+ For each author, we will need the following:
    * The user ID
    * The creation date of the user as epoch value
    * The name of the user
    * The screen name of the user
+ All the above information is provided in either Standard output, or a log file
+ You are free to choose the output format, provided that it makes it easy to parse and process by a machine

### __Bonus points for:__ ###

+ Keep track of messages per second statistics across multiple runs of the application
+ The application can run as a Docker container

## Provided functionality ##

The present project in itself is a [Maven project](http://maven.apache.org/) that contains one class that provides you with a `com.google.api.client.http.HttpRequestFactory` that is authorised to execute calls to the Twitter API in the scope of a specific user.
You will need to provide your _Consumer Key_ and _Consumer Secret_ and follow through the OAuth process (get temporary token, retrieve access URL, authorise application, enter PIN for authenticated token).
With the resulting factory you are able to generate and execute all necessary requests.
If you want to, you can also disregard the provided classes or Maven configuration and create your own application from scratch.

## Delivery ##

You are assigned to you own private repository. Please use your own branch and do not commit on master.
When the assignment is finished, please create a pull request on the master of this repository, and your contact person will be notified automatically. 

## Running the app

The app accepts four arguments (in order):
1. Word to track in tweets
2. Maximum number of tweets to fetch
3. Maximum time to fetch tweets (in seconds)
4. Output folder

If no arguments are passed, the application will use default values:
1. Bieber
2. 100
3. 30s
4. /output

### Pre requisites

1. On src/main/resources/application.properties, add the values for twitter consumer key and secret.
2. Make sure you have created `/output` directory on the root of your project - or pass another output directory as argument to the application.

### With Docker

From the root folder, run the following command:

```docker build -t ${YOUR_IMAGE_NAME} .```

Then, using default arguments:

```docker run -v ${PWD}/output:/app/output -v ${PWD}/log:/app/log -it ${YOUR_IMAGE_NAME}```

Or:

```docker run -v ${PWD}/output:/app/output -v ${PWD}/log:/app/log -it ${YOUR_IMAGE_NAME} ${wordToTrack} ${maxTweets} ${maxTime} ${outputDir}```

Example:

```docker build -t mpusinhol/bieber-tweets .```
``````docker run -v ${PWD}/output:/app/output -v ${PWD}/log:/app/log -it mpusinhol/bieber-tweets chess 10``````

### With Maven

Make sure you have JDK 11 installed.
Then, from the root folder, run the following command:

```mvn clean package```

Then, to use default arguments:

```java -jar target/bieber-tweets-1.0.0-SNAPSHOT.jar```

Or:

```java -jar target/bieber-tweets-1.0.0-SNAPSHOT.jar ${wordToTrack} ${maxTweets} ${maxTime} ${outputDir}```

Example:

```java -jar target/bieber-tweets-1.0.0-SNAPSHOT.jar "justin bieber" 10 15```
