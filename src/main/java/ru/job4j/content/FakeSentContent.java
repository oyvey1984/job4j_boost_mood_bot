package ru.job4j.content;

import java.util.ArrayList;
import java.util.List;

public class FakeSentContent implements SentContent {
    private final List<Content> sentMessages = new ArrayList<>();

    @Override
    public void sent(Content content) {
        sentMessages.add(content);
    }

    public List<Content> getSentMessages() {
        return sentMessages;
    }
}
