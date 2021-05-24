package org.interview.configs;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertiesLoader {

    private static final String PROPERTIES_FILE = "application.properties";

    /**
     * Loads all properties inside a specific resource
     *
     * @param resourceName The resource containing the properties
     * @return Properties with the loaded content
     * @throws IllegalArgumentException If resourceName is null or empty
     * @throws IOException If resource cannot be loaded or doesn't exist
     */
    public static Properties loadProperties(String resourceName) throws IOException {

        if (StringUtils.isEmpty(resourceName)) {
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

    /**
     * Loads specific variables from properties resource or environment
     *
     * @param variablesToLoad List of variable names to be loaded
     * @return Map<String, String> Containing all variables and their values
     * @throws IllegalArgumentException If variablesToLoad is null or empty
     */
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
