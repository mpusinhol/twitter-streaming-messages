package org.interview.services;

import com.google.api.client.http.HttpRequestFactory;

import javax.annotation.Nullable;
import java.io.OutputStream;
import java.time.Duration;

/**
 * Contract designed to act as a basic service for any social midia
 */
public interface SocialMidiaService {

    /**
     * Method required to be implemented for authentication on any social midia
     *
     * @return HttpRequestFactory already authenticated
     * @throws Exception if authentication fails
     */
    HttpRequestFactory authenticate() throws Exception;

    /**
     * Method designed to fetch realtime posts on social midia. Based on the arguments,
     * gets the posts and writes them to an outputStream
     *
     * @param wordToTrack Optional to look for a specific word or phrase on posts
     * @param maxPosts Maximum number of posts allowed to be retrieved
     * @param maxTime Maximum time allowed to retrieve posts
     * @param output OutputStream where the results will be written
     * @throws Exception If something goes wrong while processing posts
     */
    void processRealtimePosts(@Nullable String wordToTrack, Integer maxPosts, Duration maxTime,
                              OutputStream output) throws Exception;
}
