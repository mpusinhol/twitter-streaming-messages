package org.interview.services.impl;

import com.google.api.client.http.HttpRequestFactory;
import org.interview.services.SocialMidiaService;

import java.io.OutputStream;
import java.time.Duration;

public class TwitterServiceImpl implements SocialMidiaService {

    @Override
    public HttpRequestFactory authenticate() throws Exception {
        return null;
    }

    @Override
    public void processRealtimePosts(String wordToTrack, Integer maxPosts, Duration maxTime,
                                     OutputStream output) throws Exception {

    }
}
