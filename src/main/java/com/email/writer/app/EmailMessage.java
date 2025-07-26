package com.email.writer.app;

import java.time.Instant;

import lombok.Data;

@Data
public class EmailMessage {
    private String sender;
    private String body;
    private Instant timestamp; 

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
