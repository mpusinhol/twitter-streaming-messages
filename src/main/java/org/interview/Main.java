package org.interview;

import org.apache.commons.lang3.StringUtils;
import org.interview.configs.PropertiesLoader;
import org.interview.services.SocialMidiaService;
import org.interview.services.impl.TwitterServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String TWITTER_CONSUMER_KEY_NAME = "TWITTER_CONSUMER_KEY";
    private static final String TWITTER_CONSUMER_SECRET_NAME = "TWITTER_CONSUMER_SECRET";
    private static final String DEFAULT_OUTPUT_DIRECTORY = "output";
    private static final String DEFAULT_WORD = "Bieber";
    private static final int DEFAULT_MAX_TWEETS = 100;
    private static final Duration DEFAULT_MAX_TIME = Duration.ofSeconds(30);

    public static void main(String[] args) {
        String wordToTrack = args.length >= 1 ? args[0] : DEFAULT_WORD;
        Integer maxTweets = args.length >= 2 && StringUtils.isNumeric(args[1]) ?
                Integer.valueOf(args[1]) : DEFAULT_MAX_TWEETS;
        Duration maxTime = args.length >= 3 && StringUtils.isNumeric(args[2]) ?
                Duration.ofSeconds(Integer.valueOf(args[2])) : DEFAULT_MAX_TIME;
        String outputDirectory = args.length >= 4 ? args[3] : DEFAULT_OUTPUT_DIRECTORY;

        List<String> variablesToLoad = Arrays.asList(TWITTER_CONSUMER_KEY_NAME, TWITTER_CONSUMER_SECRET_NAME);
        Map<String, String> loadedVariablesMap = PropertiesLoader.loadVariables(variablesToLoad);

        String twitterConsumerKey = loadedVariablesMap.get(TWITTER_CONSUMER_KEY_NAME);
        String twitterConsumerSecret = loadedVariablesMap.get(TWITTER_CONSUMER_SECRET_NAME);

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            SocialMidiaService twitterService = new TwitterServiceImpl(twitterConsumerKey, twitterConsumerSecret);
            twitterService.processRealtimePosts(wordToTrack, maxTweets, maxTime, output);

            String filename = outputDirectory + "/" + Instant.now() + ".json";
            Files.write(Paths.get(filename), output.toByteArray());

            LOGGER.info("Output written to " + filename);
        } catch (NoSuchFileException e) {
            LOGGER.error("Could not create output file. Please verify if directory -> {} <- exists.", outputDirectory, e);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Twitter consumer key and secret must not be null or empty."
                    + " Please add them to application.properties or as environment variables.", e);
        } catch (Exception e) {
            LOGGER.error("Error while processing realtime tweets.", e);
        }
    }
}
