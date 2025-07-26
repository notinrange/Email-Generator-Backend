package com.email.writer.app;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/email")
@CrossOrigin(
  origins = {"http://localhost:3000", "https://mail.google.com"},
  allowCredentials = "true",
  allowedHeaders = "*",
  methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}
)
public class EmailGeneratorController {
    private final EmailGeneratorService service;

    public EmailGeneratorController(EmailGeneratorService service) {
        this.service = service;
    }

    // 1) Reply endpoint (multiple suggestions)
    @Cacheable(value = "emailRepliesCache", key = "#req.hashCode()")
    @PostMapping("/generate")
    public ReplyResponse generateReplies(@RequestBody EmailRequest req) {
        return service.generateReplies(req);
    }

    // 2) Single suggestion (legacy)
    @PostMapping("/generateOne")
    public ResponseEntity<String> generateOne(@RequestBody EmailRequest req) {
        return ResponseEntity.ok(service.generateEmailReply(req));
    }

    // 3) Standalone compose endpoint
    @PostMapping("/compose")
    public ResponseEntity<ReplyResponse> composeEmail(@RequestBody ComposeRequest req) {
        // map ComposeRequest -> EmailRequest style, but no thread
        EmailRequest er = new EmailRequest();
        er.setThread(List.of());  // no context
        er.setTone(req.getTone()); /* ... */
        String prompt = req.getPrompt();
        // possibly wrap prompt into EmailMessage and pass through same pipeline
        return ResponseEntity.ok(service.generateReplies(er));
    }
}
