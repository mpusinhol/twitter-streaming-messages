package org.interview;

import org.interview.configs.PropertiesLoader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {

    private static final String TWITTER_CONSUMER_KEY_NAME = "TWITTER_CONSUMER_KEY";
    private static final String TWIITER_CONSUMER_SECRET_NAME = "TWITTER_CONSUMER_SECRET";

    public static void main(String[] args) {

        List<String> variablesToLoad = Arrays.asList(TWITTER_CONSUMER_KEY_NAME, TWIITER_CONSUMER_SECRET_NAME);
        Map<String, String> loadedVariablesMap = PropertiesLoader.loadVariables(variablesToLoad);

        loadedVariablesMap.forEach((key, value) -> System.out.println("Key: " + key + " | Value: " + value));
    }
}
