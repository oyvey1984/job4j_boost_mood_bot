package ru.job4j.services;

import org.springframework.stereotype.Service;
import ru.job4j.content.Content;
import ru.job4j.content.ContentProvider;
import ru.job4j.exception.InvalidRecommendationRequestException;
import ru.job4j.exception.NoContentProviderException;
import ru.job4j.exception.RecommendationFailedException;
import ru.job4j.model.MoodContent;
import ru.job4j.repository.MoodContentRepository;

import java.util.List;
import java.util.Random;

@Service
public class RecommendationEngine {
    private final List<ContentProvider> contents;
    private static final Random RND = new Random();
    private final MoodContentRepository moodContentRepository;

    public RecommendationEngine(List<ContentProvider> contents, MoodContentRepository repository) {
        this.contents = contents;
        this.moodContentRepository = repository;
    }

    private Content generateMedia(Long chatId, Long moodId, boolean isGood) {
        if (contents == null || contents.isEmpty()) {
            throw new NoContentProviderException();
        }
        var index = RND.nextInt(0, contents.size());
        return contents.get(index).byMood(chatId, moodId, isGood);
    }

    private String buildMoodReactionText(Long moodId) {
        return moodContentRepository.findAll()
                .stream()
                .filter(moodContent -> moodContent.getMood().getId().equals(moodId))
                .findFirst()
                .map(MoodContent::getText)
                .orElse("");
    }

    private void appendText(Content content, String textToAppend) {
        if (textToAppend == null || textToAppend.isBlank()) {
            return;
        }

        if (content.getText() == null || content.getText().isBlank()) {
            content.setText(textToAppend);
        } else {
            content.setText(textToAppend + "\n\n" + content.getText());
        }
    }

    public Content reactionToMood(Long chatId, Long moodId, boolean isGood) {
        if (chatId == null || moodId == null) {
            throw new InvalidRecommendationRequestException("chatId and moodId must not be null");
        }
        try {
            Content content = generateMedia(chatId, moodId, isGood);
            appendText(content, buildMoodReactionText(moodId));
            return content;
        } catch (Exception e) {
            throw new RecommendationFailedException(
                    "Failed to generate recommendation",
                    e
            );
        }
    }

    public Content dailyRecommendation(Long chatId, Long moodId, boolean isGood) {
        try {
            return generateMedia(chatId, moodId, isGood);
        } catch (Exception e) {
            throw new RecommendationFailedException("Failed to generate daily recommendation", e);
        }
    }
}