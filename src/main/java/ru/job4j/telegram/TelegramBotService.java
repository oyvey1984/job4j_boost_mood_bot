package ru.job4j.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.content.Content;
import ru.job4j.content.SentContent;
import ru.job4j.exception.SentContentException;
import ru.job4j.services.AdviceService;

@Service
@Conditional(OnProdCondition.class)
public class TelegramBotService extends TelegramLongPollingBot implements SentContent {
    private final AdviceService adviceService;
    private final BotCommandHandler handler;
    private final String botName;

    public TelegramBotService(@Value("${telegram.bot.name}") String botName,
                              @Value("${telegram.bot.token}") String botToken, AdviceService adviceService,
                              BotCommandHandler handler) {
        super(botToken);
        this.adviceService = adviceService;
        this.handler = handler;
        this.botName = botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handler.handleCallback(update.getCallbackQuery())
                    .ifPresent(this::sent);
        } else if (update.hasMessage() && update.getMessage().getText() != null) {
            handler.commands(update.getMessage())
                    .ifPresent(this::sent);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void sent(Content content) {
        long chatId = content.getChatId();

        try {
            if (content.getVideo() != null) {
                SendVideo sendVideo = new SendVideo();
                sendVideo.setChatId(chatId);
                sendVideo.setVideo(content.getVideo());
                if (content.getMarkup() != null) {
                    sendVideo.setCaption(content.getText());
                }
                execute(sendVideo);
            }

            if (content.getAudio() != null) {
                SendAudio sendAudio = new SendAudio();
                sendAudio.setChatId(chatId);
                sendAudio.setAudio(content.getAudio());
                sendAudio.setCaption(content.getText());
                execute(sendAudio);
                return;
            }

            if (content.getPhoto() != null) {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(chatId);
                sendPhoto.setPhoto(content.getPhoto());
                sendPhoto.setCaption(content.getText());
                execute(sendPhoto);
                return;
            }

            if (content.getText() != null) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText(content.getText());
                if (content.getMarkup() != null) {
                    sendMessage.setReplyMarkup(content.getMarkup());
                }
                execute(sendMessage);
            }

        } catch (TelegramApiException e) {
            throw new SentContentException("Ошибка отправки контента", e);
        }
    }

    @Scheduled(cron = "0 29 15 * * *")
    public void sendDailyAdvice() {
        adviceService.adviceUsers()
                .forEach(this::sent);
    }
}