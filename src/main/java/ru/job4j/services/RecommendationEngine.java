package ru.job4j.services;

import org.springframework.stereotype.Service;
import ru.job4j.content.Content;
import ru.job4j.content.ContentProvider;
import ru.job4j.exception.InvalidRecommendationRequestException;
import ru.job4j.exception.NoContentProviderException;
import ru.job4j.exception.RecommendationFailedException;

import java.util.List;
import java.util.Random;

@Service
public class RecommendationEngine {
    private final List<ContentProvider> contents;
    private static final Random RND = new Random(System.currentTimeMillis());

    public RecommendationEngine(List<ContentProvider> contents) {
        this.contents = contents;
    }

    public Content recommendFor(Long chatId, Long moodId) {
        if (chatId == null || moodId == null) {
            throw new InvalidRecommendationRequestException("chatId and moodId must not be null");
        }

        if (contents == null || contents.isEmpty()) {
            throw new NoContentProviderException();
        }

        try {
            var index = RND.nextInt(0, contents.size());
            return contents.get(index).byMood(chatId, moodId);
        } catch (Exception e) {
            throw new RecommendationFailedException(
                    "Failed to generate recommendation",
                    e
            );
        }
    }
}