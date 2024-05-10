package org.example.sanitycheck;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CommentCodeSender {

    private final HttpClient client;
    private final ObjectMapper objectMapper;
    private final String apiUrl;
    private final String modelName;

    public CommentCodeSender() {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.apiUrl = SanityCheckSettings.getInstance().apiUrl; // Using settings
        this.modelName = SanityCheckSettings.getInstance().modelName;
    }

    public CompletableFuture<String> sendCommentAndCode(String commentString, String codeString) {
        if (commentString.isEmpty()) {
            return CompletableFuture.completedFuture("Comment is empty");
        }

        try {
            String json = objectMapper.writeValueAsString(Map.of("comment", commentString,
                    "code", codeString, "model", modelName));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(responseBody -> {
                        try {
                            Map<String, String> responseMap = objectMapper.readValue(responseBody, new TypeReference<Map<String, String>>() {});
                            return responseMap.getOrDefault("response", "No response key in JSON");
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .exceptionally(e -> "Error processing the request: " + e.getMessage());
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}
