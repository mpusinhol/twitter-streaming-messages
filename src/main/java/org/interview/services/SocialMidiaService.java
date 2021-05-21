package org.interview.services;

import com.google.api.client.http.HttpRequestFactory;

import java.io.OutputStream;
import java.time.Duration;

public interface SocialMidiaService {

    HttpRequestFactory authenticate() throws Exception;

    void processRealtimePosts(String wordToTrack, Integer maxPosts, Duration maxTime,
                              OutputStream output) throws Exception;
}
