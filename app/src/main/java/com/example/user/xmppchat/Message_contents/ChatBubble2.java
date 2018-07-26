package com.example.user.xmppchat.Message_contents;

public class ChatBubble2 {
    private String content;
    private boolean myMessage;
    String tag;

    public ChatBubble2(String content, boolean myMessage, String tag) {
        this.content = content;
        this.myMessage = myMessage;
        this.tag = tag;
    }

    public String getContent() {
        return content;
    }

    public boolean myMessage() {
        return myMessage;
    }

    public String Tag() {
        return tag;
    }
}
