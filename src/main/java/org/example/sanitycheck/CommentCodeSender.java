package org.example.sanitycheck;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * This class handles sending a combination of code and comments to a specified API
 * and retrieves the response.
 */
public class CommentCodeSender {

    private final HttpClient client;
    private final ObjectMapper objectMapper;
    private final String apiUrl;
    private final String modelName;

    /**
     * Constructor initializes the HTTP client, JSON mapper, API URL, and model name from settings.
     */
    public CommentCodeSender() {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.apiUrl = SanityCheckSettings.getInstance().apiUrl;
        this.modelName = SanityCheckSettings.getInstance().modelName;
    }

    /**
     * Sends a comment and code to the API and returns the response as a String.
     * Returns "NOTHING" if any error occurs during the sending or processing.
     *
     * @param commentString The comment associated with the code
     * @param codeString The actual code to be sent
     * @return CompletableFuture delivering the API response or "NOTHING" in case of errors
     */
    public CompletableFuture<String> sendCommentAndCode(String commentString, String codeString) {
        if (commentString.isEmpty()) {
            return CompletableFuture.completedFuture("Comment is empty");
        }

        try {
            // Make the json payload
            String json = objectMapper.writeValueAsString(Map.of(
                    "comment", commentString,
                    "code", codeString,
                    "model", modelName
            ));

            // Build the HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            // Send the HTTP request and return the future
            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(this::extractResponse)
                    .exceptionally(ex -> "NOTHING"); // Return "NOTHING" on any exception
        } catch (Exception e) {
            return CompletableFuture.completedFuture("NOTHING"); // Return "NOTHING" if pre-request processing fails
        }
    }

    /**
     * Extracts the API response from the JSON response body.
     *
     * @param responseBody JSON string from the API
     * @return The extracted response or "NOTHING" if the response body cannot be processed
     */
    private String extractResponse(String responseBody) {
        try {
            //Map the values
            Map<String, String> responseMap = objectMapper.readValue(responseBody, new TypeReference<Map<String, String>>() {});
            return responseMap.getOrDefault("response", "NOTHING");
        } catch (Exception e) {
            return "NOTHING"; // Return "NOTHING" if JSON parsing fails
        }
    }
}
