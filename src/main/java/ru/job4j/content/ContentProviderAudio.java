package ru.job4j.content;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.util.Random;

@Component
public class ContentProviderAudio implements ContentProvider {
    private static final String AUDIO_DIR_FOR_GOOD = "./audioForGood";
    private static final String AUDIO_DIR_FOR_BAD = "./audioForBad";

    public static final Random RANDOM = new Random();

    @Override
    public Content byMood(Long chatId, Long moodId, boolean isGood) {
        var content = new Content(chatId);
        String dirForMood = isGood ? AUDIO_DIR_FOR_GOOD : AUDIO_DIR_FOR_BAD;
        File audio = getRandomAudio(dirForMood);
        if (audio != null) {
            content.setAudio(new InputFile(audio));
        }
        return content;
    }

    private File getRandomAudio(String directory) {
        File dir = new File(directory);

        if (!dir.exists() || !dir.isDirectory()) {
            return null;
        }

        File[] audio = dir.listFiles(file ->
                file.isFile()
                        && (file.getName().endsWith(".mp3"))
        );
        if (audio == null || audio.length == 0) {
            return null;
        }
        File aud = audio[RANDOM.nextInt(audio.length)];
        System.out.println("Папка: " + aud.getAbsolutePath());
        return aud;
    }
}