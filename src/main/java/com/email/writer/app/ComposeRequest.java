package com.email.writer.app;

public class ComposeRequest {
    private String prompt;    
    private String recipient; 
    private String goal;     
    private String tone;
    private String formality;
    public String getPrompt() {
        return prompt;
    }
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    public String getRecipient() {
        return recipient;
    }
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    public String getGoal() {
        return goal;
    }
    public void setGoal(String goal) {
        this.goal = goal;
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
    private String length;
    private String language;
    private UserProfile profile;
}
