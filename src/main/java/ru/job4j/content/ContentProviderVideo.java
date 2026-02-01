package ru.job4j.content;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.util.Random;

@Component
public class ContentProviderVideo implements ContentProvider {
    private static final String VIDEO_DIR_FOR_GOOD = "./videoForGood";
    private static final String VIDEO_DIR_FOR_BAD = "./videoForBad";

    public static final Random RANDOM = new Random();

    @Override
    public Content byMood(Long chatId, Long moodId, boolean isGood) {
        var content = new Content(chatId);
        String dirForMood = isGood ? VIDEO_DIR_FOR_GOOD : VIDEO_DIR_FOR_BAD;
        File video = getRandomVideo(dirForMood);
        if (video != null) {
            content.setVideo(new InputFile(video));
        }
        return content;
    }

    private File getRandomVideo(String directory) {
        File dir = new File(directory);

        if (!dir.exists() || !dir.isDirectory()) {
            return null;
        }

        File[] video = dir.listFiles(file ->
                file.isFile()
                        && file.getName().endsWith(".mp4")
        );
        if (video == null || video.length == 0) {
            return null;
        }
        File vid = video[RANDOM.nextInt(video.length)];
        System.out.println("Папка: " + vid.getAbsolutePath());
        return vid;
    }
}