package ru.job4j.content;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.util.Random;

@Component
public class ContentProviderImage implements ContentProvider {
    private static final String IMAGES_DIR_FOR_GOOD = "./imagesForGood";
    private static final String IMAGES_DIR_FOR_BAD = "./imagesForBad";

    public static final Random RANDOM = new Random();

    @Override
    public Content byMood(Long chatId, Long moodId, boolean isGood) {
        var content = new Content(chatId);
        String dirForMood = isGood ? IMAGES_DIR_FOR_GOOD : IMAGES_DIR_FOR_BAD;
        File image = getRandomImage(dirForMood);
        if (image != null) {
            content.setPhoto(new InputFile(image));
        }
        return content;
    }

    private File getRandomImage(String directory) {
        File dir = new File(directory);

        if (!dir.exists() || !dir.isDirectory()) {
            return null;
        }

        File[] images = dir.listFiles(file ->
                file.isFile()
                        && (file.getName().endsWith(".png")
                        || file.getName().endsWith(".jpg")
                        || file.getName().endsWith(".jpeg")
                        || file.getName().endsWith(".webp"))
                        );
        if (images == null || images.length == 0) {
            return null;
        }
        File image = images[RANDOM.nextInt(images.length)];
        System.out.println("Папка: " + image.getAbsolutePath());
        return image;
    }
}