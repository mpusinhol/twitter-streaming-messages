package org.interview;

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
    private static final String OUTPUT_DIRECTORY = "output";
    private static final String DEFAULT_WORD = "Bieber";
    private static final int DEFAULT_MAX_TWEETS = 100;
    private static final Duration DEFAULT_MAX_TIME = Duration.ofSeconds(30);

    public static void main(String[] args) {

        List<String> variablesToLoad = Arrays.asList(TWITTER_CONSUMER_KEY_NAME, TWITTER_CONSUMER_SECRET_NAME);
        Map<String, String> loadedVariablesMap = PropertiesLoader.loadVariables(variablesToLoad);

        String twitterConsumerKey = loadedVariablesMap.get(TWITTER_CONSUMER_KEY_NAME);
        String twitterConsumerSecret = loadedVariablesMap.get(TWITTER_CONSUMER_SECRET_NAME);

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            SocialMidiaService twitterService = new TwitterServiceImpl(twitterConsumerKey, twitterConsumerSecret);
            twitterService.processRealtimePosts(DEFAULT_WORD, DEFAULT_MAX_TWEETS, DEFAULT_MAX_TIME, output);
            Files.write(Paths.get(OUTPUT_DIRECTORY + "/" + Instant.now() + ".json"), output.toByteArray());

        } catch (NoSuchFileException e) {
            LOGGER.error("Could not create output file. Please verify if directory -> {} <- exists.", OUTPUT_DIRECTORY, e);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Twitter consumer key and secret must not be null or empty."
                    + " Please add them to application.properties or as environment variables.", e);
        } catch (Exception e) {
            LOGGER.error("Error while processing realtime tweets.", e);
        }
    }
}
