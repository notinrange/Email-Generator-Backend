package com.email.writer.app;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class ReplyResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public ReplyResponse(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    private List<String> suggestions;

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }
}
