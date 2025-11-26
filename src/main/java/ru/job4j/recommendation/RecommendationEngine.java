package ru.job4j.recommendation;

import ru.job4j.content.Content;
import ru.job4j.content.ContentProvider;

import java.util.List;
import java.util.Random;

public class RecommendationEngine {
    private final List<ContentProvider> contents;
    private static final Random RND = new Random(System.currentTimeMillis());

    public RecommendationEngine(List<ContentProvider> contents) {
        this.contents = contents;
    }

    public Content recommendFor(Long chatId, Long moodId) {
        var index = RND.nextInt(0, contents.size());
        return contents.get(index).byMood(chatId, moodId);
    }
}