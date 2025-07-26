package com.email.writer.app;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public class EmailGeneratorService {
    private final WebClient webClient;
    private final CacheManager cacheManager;


    @Value("${model}")
    private String model;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public EmailGeneratorService(WebClient.Builder builder,
                    @Value("${gemini.api.url}") String apiUrl,
                                 CacheManager cacheManager) {
                                    this.webClient = builder
                                    .baseUrl(apiUrl)                
                                    .build();
        this.cacheManager = cacheManager;
    }

    @Cacheable(value = "replyCache",
               key = "#request.thread.hashCode() + #request.tone + #request.formality + #request.language",
               condition = "!#request.skipCache")
    public ReplyResponse generateReplies(EmailRequest request) {
        String prompt = buildFullPrompt(request);
        Map<String, Object> body = Map.of(
            "contents", List.of(
                Map.of(
                    "role", "USER", 
                    "parts", List.of(Map.of("text", prompt))
                    )
            ),
            "generationConfig", Map.of(                        // ← wrap params here
                "candidateCount", 3,
                "temperature", 0.7
            )
            // "n", 3,
            // "temperature", 0.7
        );

        String uri = UriComponentsBuilder
            .fromUriString(geminiApiUrl)
            .path("/v1beta/models/"+ model+ ":generateContent")
            .queryParam("key", geminiApiKey)
            .toUriString();

            List<String> rawCandidates = webClient.post()
            .uri(uri)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(JsonNode.class)
            .map(this::extractAllContents)
            .block();


        List<String> signedReplies = rawCandidates.stream()
            .map(reply -> applySignature(reply, request.getProfile()))
            .collect(Collectors.toList());

        return new ReplyResponse(signedReplies);
    }

    public String generateEmailReply(EmailRequest req) {
        return generateReplies(req).getSuggestions().get(0);
    }

    public String summarize(List<EmailMessage> oldMsgs) {
        String combinedText = oldMsgs.stream()
            .map(EmailMessage::getBody)
            .collect(Collectors.joining("\n\n"));

        String prompt = "Summarize the following email thread in a concise paragraph:\n\n" + combinedText;
        String uri = UriComponentsBuilder
            .fromUriString(geminiApiUrl)
            .queryParam("key", geminiApiKey)
            .toUriString();

        return webClient.post()
            .uri(uri)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of(
                "contents", List.of(
                    Map.of("parts", List.of(Map.of("text", prompt)))
                )
            ))
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }

    public String detectLanguage(EmailRequest r) {
        String prompt = "Identify the language of the following text and provide the ISO 639-1 language code:\n\n" + r.getEmailContent();
        String uri = UriComponentsBuilder
            .fromUriString(geminiApiUrl)
            .queryParam("key", geminiApiKey)
            .toUriString();

        return webClient.post()
            .uri(uri)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of(
                "contents", List.of(
                    Map.of("parts", List.of(Map.of("text", prompt)))
                )
            ))
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }

    

    private String buildFullPrompt(EmailRequest r) {
        StringBuilder p = new StringBuilder("You are an AI email assistant.");

        if (r.getProfile() != null && r.getProfile().getName() != null) {
            p.append(" Reply as ").append(r.getProfile().getName()).append(".");
        }

        String lang = (r.getLanguage() == null || r.getLanguage().equals("AUTO"))
            ? detectLanguage(r)
            : r.getLanguage();
        if (!"English".equalsIgnoreCase(lang)) {
            p.append(" The reply should be in ").append(lang).append(".");
        }

        if (r.getTone() != null)      p.append(" Use a ").append(r.getTone()).append(" tone.");
        if (r.getFormality() != null) p.append(" Write in a ").append(r.getFormality()).append(" style.");
        if (r.getLength() != null)    p.append(" Keep it ").append(r.getLength().toLowerCase()).append(".");

        List<EmailMessage> thread = r.getThread();
        if (thread.size() > 3) {
            String summary = summarize(thread.subList(0, thread.size() - 1));
            p.append(" Conversation summary: ").append(summary);
        } else {
            p.append(" Thread:");
            for (EmailMessage m : thread.subList(0, thread.size() - 1)) {
                p.append("\n").append(m.getSender()).append(": ").append(m.getBody());
            }
        }
        EmailMessage last = thread.get(thread.size() - 1);
        p.append("\n\nLatest email from ")
         .append(last.getSender()).append(": \"")
         .append(last.getBody()).append("\"");

        return p.toString();
    }

    private List<String> extractAllContents(JsonNode root) {
        return StreamSupport.stream(root.path("candidates").spliterator(), false)
            .map(c -> c.path("content").path("parts").get(0).path("text").asText())
            .collect(Collectors.toList());
    }

    private String applySignature(String reply, UserProfile p) {
        if (p == null || p.getSignature() == null) return reply;
        return reply.trim() + "\n\n" + p.getSignature().replace("[NAME]", p.getName());
    }
}
