package org.interview.services.impl;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class TwitterServiceImplTest {

    private static final String DEFAULT_WORD = "Bieber";
    private static final int DEFAULT_MAX_TWEETS = 10;
    private static final Duration DEFAULT_MAX_TIME = Duration.ofSeconds(5);
    private static final String HAPPY_FLOW_INPUT = "happy-flow-input.json-stream";
    private static final String HAPPY_FLOW_OUTPUT = "happy-flow-output.json";

    private HttpRequestFactory httpRequestFactory;
    private TwitterServiceImpl twitterService;
    private HttpRequest httpRequest;
    private HttpResponse httpResponse;

    static Stream<Arguments> happyFlowInputProvider() {
        return Stream.of(
                Arguments.of(
                        DEFAULT_WORD,
                        DEFAULT_MAX_TWEETS,
                        DEFAULT_MAX_TIME,
                        HAPPY_FLOW_INPUT,
                        HAPPY_FLOW_OUTPUT
                )
        );
    }

    @BeforeAll
    void setUpMocks() throws Exception {
        httpRequestFactory = Mockito.mock(HttpRequestFactory.class);
        httpRequest = Mockito.mock(HttpRequest.class);
        httpResponse = Mockito.mock(HttpResponse.class);

        when(httpRequestFactory.buildGetRequest(any())).thenReturn(httpRequest);
        when(httpRequest.execute()).thenReturn(httpResponse);

        twitterService = spy(new TwitterServiceImpl("dummy", "dummy"));
        doReturn(httpRequestFactory).when(twitterService).authenticate();
    }

    String loadResourceAsString(String resource) {
        InputStream inputStream = this.getClass().getResourceAsStream(resource);
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        return bufferedReader.lines().collect(Collectors.joining("\n"));
    }

    @ParameterizedTest
    @MethodSource("happyFlowInputProvider")
    void testHappyFlowWithSorting(String wordToTrack, int maxTweets, Duration maxTime, String happyFlowInput,
                                  String happyFlowOutput) throws Exception {

        InputStream input = this.getClass().getResourceAsStream(happyFlowInput);
        String expectedOutput = loadResourceAsString(happyFlowOutput);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        when(httpResponse.getContent()).thenReturn(input);

        twitterService.processRealtimePosts(wordToTrack, maxTweets, maxTime, byteArrayOutputStream);

        String output = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);

        JSONAssert.assertEquals(expectedOutput, output, JSONCompareMode.STRICT);
    }

    @ParameterizedTest
    @MethodSource("happyFlowInputProvider")
    void testZeroMaxTweets(String wordToTrack, int maxTweets, Duration maxTime, String happyFlowInput,
                           String happyFlowOutput) throws Exception {

        InputStream input = this.getClass().getResourceAsStream(happyFlowInput);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        when(httpResponse.getContent()).thenReturn(input);

        twitterService.processRealtimePosts(wordToTrack, 0, maxTime, byteArrayOutputStream);

        String output = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);

        JSONAssert.assertEquals("[]", output, JSONCompareMode.STRICT);
    }

    @ParameterizedTest
    @MethodSource("happyFlowInputProvider")
    void testZeroMaxTime(String wordToTrack, int maxTweets, Duration maxTime, String happyFlowInput,
                         String happyFlowOutput) throws Exception {

        InputStream input = this.getClass().getResourceAsStream(happyFlowInput);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        when(httpResponse.getContent()).thenReturn(input);

        twitterService.processRealtimePosts(wordToTrack, maxTweets, Duration.ZERO, byteArrayOutputStream);

        String output = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);

        JSONAssert.assertEquals("[]", output, JSONCompareMode.STRICT);
    }

    @ParameterizedTest
    @MethodSource("happyFlowInputProvider")
    void testNullWordToTrack(String wordToTrack, int maxTweets, Duration maxTime, String happyFlowInput,
                             String happyFlowOutput) throws Exception {

        InputStream input = this.getClass().getResourceAsStream(happyFlowInput);
        String expectedOutput = loadResourceAsString(happyFlowOutput);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        when(httpResponse.getContent()).thenReturn(input);

        twitterService.processRealtimePosts(null, maxTweets, maxTime, byteArrayOutputStream);

        String output = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);

        JSONAssert.assertEquals(expectedOutput, output, JSONCompareMode.STRICT);
    }
}
