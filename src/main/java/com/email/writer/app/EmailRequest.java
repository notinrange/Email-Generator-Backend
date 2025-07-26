package com.email.writer.app;

import java.util.List;

import lombok.Data;

@Data
public class EmailRequest {
    private String emailContent;   
    private List<EmailMessage> thread;      
    private String tone;                    
    private String formality;               
    private String length;                  
    private String language;                
    private UserProfile profile;            
    private boolean skipCache;              

    public List<EmailMessage> getThread() {
        return thread;
    }

    public void setThread(List<EmailMessage> thread) {
        this.thread = thread;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    public String getFormality() {
        return formality;
    }

    public void setFormality(String formality) {
        this.formality = formality;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    public boolean isSkipCache() {
        return skipCache;
    }

    public void setSkipCache(boolean skipCache) {
        this.skipCache = skipCache;
    }

    public String getEmailContent() {
        return emailContent;
    }
}
// using lombok no need to create getter and setters 
