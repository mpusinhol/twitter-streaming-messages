package org.interview.services.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.interview.dto.twitter.Author;
import org.interview.dto.twitter.Message;
import org.interview.dto.twitter.Tweet;
import org.interview.dto.twitter.User;
import org.interview.oauth.twitter.TwitterAuthenticator;
import org.interview.services.SocialMidiaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.Duration;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TwitterServiceImpl implements SocialMidiaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterServiceImpl.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String TWITTER_API_URL = "https://stream.twitter.com/1.1/statuses/filter.json";

    private final String consumerKey;
    private final String consumerSecret;

    public TwitterServiceImpl(String consumerKey, String consumerSecret) {

        if (consumerKey == null || "".equals(consumerSecret)) {
            throw new IllegalArgumentException("Consumer key and consumer secret must not be null.");
        }

        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;

        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public HttpRequestFactory authenticate() throws Exception {
        return authenticate(new PrintStream(System.out));
    }

    public HttpRequestFactory authenticate(PrintStream output) throws Exception {
        TwitterAuthenticator twitterAuthenticator = new TwitterAuthenticator(output, consumerKey, consumerSecret);
        return twitterAuthenticator.getAuthorizedHttpRequestFactory();
    }

    @Override
    public void processRealtimePosts(String wordToTrack, Integer maxPosts, Duration maxTime,
                                     OutputStream output) throws Exception {
        LOGGER.info("Fetching realtime tweets containing: " + wordToTrack);

        HttpRequestFactory httpRequestFactory = authenticate();

        String url = TWITTER_API_URL;

        if (wordToTrack != null && wordToTrack != "") {
            url += "?track=" + wordToTrack;
        }

        GenericUrl genericUrl = new GenericUrl(url);
        InputStream source = httpRequestFactory.buildGetRequest(genericUrl).execute().getContent();

        AtomicInteger eventCounter = new AtomicInteger();

        TreeSet<Author> processedTweets = makeFlowableFromInputStream(source)
                .observeOn(Schedulers.io())
                .takeUntil(Flowable.interval(maxTime.getSeconds(), TimeUnit.SECONDS))
                .flatMap(streamLine ->
                        Flowable.just(OBJECT_MAPPER.readValue(streamLine, Tweet.class))
                                .subscribeOn(Schedulers.computation()))
                .takeWhile(event -> eventCounter.getAndIncrement() < maxPosts)
                .doOnNext(tweet -> LOGGER.info("Processing tweet number {}: {}.", eventCounter.get(), tweet.toString()))
                .collect(TreeSet<Author>::new, this::collectTweet)
                .doOnError(e -> LOGGER.error("Error while processing tweets.", e))
                .doOnSuccess(authors -> LOGGER.info("Finished successfully processing {} tweets.", eventCounter.get()))
                .blockingGet();

        OBJECT_MAPPER.writeValue(output, processedTweets);
    }

    private Flowable<String> makeFlowableFromInputStream(InputStream source) {
        return Flowable.generate(
                () -> new BufferedReader(new InputStreamReader(source)),
                (reader, emitter) -> {
                    String line = reader.readLine();

                    if (line == null) {
                        emitter.onComplete();
                    }

                    emitter.onNext(line);
                },
                reader -> reader.close()
        );
    }

    private void collectTweet(TreeSet<Author> outputSet, Tweet tweet) {
        LOGGER.info("Collecting tweet: {}.", tweet.toString());

        User user = tweet.getUser();

        Author author = outputSet.stream()
                .filter(innerAuthor -> innerAuthor.getId().equals(user.getId()))
                .findAny()
                .orElse(new Author(user.getId(), user.getName(), user.getScreenName(), user.getCreatedAt()));

        Message message = new Message(tweet.getId(), tweet.getText(), tweet.getCreatedAt());

        author.getMessages().add(message);
        outputSet.add(author);
    }
}
