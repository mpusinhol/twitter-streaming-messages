package org.interview.configs;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertiesLoader {

    private static final String PROPERTIES_FILE = "application.properties";

    public static Properties loadProperties(String resourceName) throws IOException {

        if (resourceName == null || "".equals(resourceName)) {
            throw new IllegalArgumentException("ResourceName must not be null or empty.");
        }

        Properties properties = new Properties();

        InputStream inputStream = PropertiesLoader.class
                .getClassLoader()
                .getResourceAsStream(resourceName);

        if (inputStream != null) {
            properties.load(inputStream);
            inputStream.close();
        }

        return properties;
    }

    public static Map<String, String> loadVariables(List<String> variablesToLoad) {

        if (variablesToLoad == null || variablesToLoad.isEmpty()) {
            throw new IllegalArgumentException("VariablesToLoad must not be null or empty");
        }

        Map<String, String> variables = new HashMap<>();
        Properties properties = null;

        try {
            properties = loadProperties(PROPERTIES_FILE);
        } catch (IOException e) {
        }

        for (String variable : variablesToLoad) {
            String value = System.getenv(variable);

            if (value == null && properties != null) {
                value = properties.getProperty(variable, "");
            }

            variables.put(variable, value);
        }

        return variables;
    }
}