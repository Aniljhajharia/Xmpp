package com.example.user.xmppchat.Design_Fragment;

public class ChatBubble {
    private String content;
    private boolean myMessage;
    private String tag;
    public ChatBubble(String content, boolean myMessage,String tag) {
        this.content = content;
        this.myMessage = myMessage;
        this.tag=tag;
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
