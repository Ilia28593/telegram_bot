package com.vaulterix.telegram_bot.service;


import com.vaulterix.telegram_bot.config.BotConfig;
import com.vaulterix.telegram_bot.exceptions.UploadFileException;
import com.vaulterix.telegram_bot.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final ReplyKeyboardMaker replyKeyboardMaker;
    private final TGService service;
    private final BotService botService;
    private final BotConfig config;

    private String description = "1.\tДля того чтобы воспользоваться ботом вы должны быть зарегистрированы в Vaulterix.\n" +
            "2.\tОтправьте команду «старт».\n" +
            "3.\tВведите ваш почтовый адрес пользователя, зарегистрированного в Vaulterix.\n" +
            "4.\tПо получению ответа «почтовый адрес принят» ведите почтовый адрес получателя.\n" +
            "5.\tПрикрепите файл/файлы и дождитесь сообщения о загрузке файлов.\n" +
            "6.\tОтправьте команду «далее». \n" +
            "7.\tДля возобновления отправьте команду «старт».";

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage()) {
            var chatId = update.getMessage().getChatId();
            if (update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                if (messageText.contains("@")) {
                    StringBuilder badEmail = new StringBuilder();
                    StringBuilder acceptedEmail = new StringBuilder();
                    String regex = "^(?=.{9,210}$)[A-Za-z0-9._-]+(?:\\.[A-Za-z0-9-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";
                    Pattern pattern = Pattern.compile(regex);
                    if (messageText.contains(",")) {
                        checkValidSendFile(chatId);
                        addEmailToSend(messageText, chatId, badEmail, acceptedEmail, pattern);
                    } else {
                        String s = checkEmailAndAddResponse(messageText, chatId, pattern);
                        if (!s.contains("принят")) {
                            sendMassageByAddButton(chatId, s);
                        } else {
                            sendMessage(chatId, s);
                        }
                    }
                } else {
                    switch (messageText.toLowerCase()) {
                        case "/start":
                            service.clean(chatId);
                            sendMessage(chatId, "Добрый день, " + update.getMessage().getChat().getFirstName() + "." +
                                    "\nВас приветствует  бот Vaulterix компании MitraSoft!");
                            keybordRequest(chatId, "Справка", "help", "Для получения справки нажмите справка");
                            sendMessage(chatId, "Введите Ваш е-mail пользователя Vaulterix.");
                            break;
                        case "старт":
                            service.clean(chatId);
                            sendMessage(chatId, update.getMessage().getChat().getFirstName() + "\n" +
                                    "Введите Ваш е-mail пользователя Vaulterix.");
                            break;
                        case "далее":
                            checkPArt(chatId);
                            break;

                        default:
                            keybordRequest(chatId, "Справка", "help", "Данная команда не поддерживается, следуйте указаниям бота. " +
                                    "\nДля получения справки нажмите справка");
                    }
                }
            } else if (update.getMessage().hasDocument()) {
                checkSendFile(chatId);
                sendMessage(chatId, service.shareDoc(update.getMessage(), chatId));
            } else if (update.getMessage().hasPhoto()) {
                checkSendFile(chatId);
                sendMessage(chatId, botService.consumePhotoMessageUpdates(update));
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            if (callbackData.equals("help")) {
                sendMessage(chatId, description);
                keybordRequest(chatId, "старт", "start", "Для возврата в меню нажмите старт");
            }
            if (callbackData.equals("start")) {
                service.clean(chatId);
                sendMessage(chatId, "Введите Ваш е-mail пользователя Vaulterix.");
            }
            if (callbackData.equals("share")) {
                sendMessage(chatId, service.createLinkByShares(chatId));
                keybordRequest(chatId, "старт", "start", "Для возврата в меню нажмите старт");
            }
        }
    }

    private void keybordRequest(long chatId, String nameButton, String requestText, String textMessage) {
        SendMessage message = new SendMessage();
        message.setText(textMessage);
        message.setChatId(String.valueOf(chatId));
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var button = new InlineKeyboardButton();
        button.setText(nameButton);
        button.setCallbackData(requestText);
        rowInLine.add(button);
        rowsInLine.add(rowInLine);
        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);
        sendMessage(message);
    }


    private void checkValidSendFile(Long chatId) {
        Optional<User> user = service.getUser(chatId);
        if (user.isEmpty()) {
            sendMessage(chatId, "Вы не прошли систему подверждения почты, отправьте е-mail технического пользователя, зарегистрированного в Vaulterix");
            keybordRequest(chatId, "старт", "start", "Для возврата в меню нажмите старт");
            throw new UploadFileException("Bad response from telegram service: ");
        }
    }

    private void checkPArt(long chatId) {
        Optional<User> user = service.getUser(chatId);
        if (user.isEmpty()) {
            sendMessage(chatId, "Вы не прошли авторизацию пользователем, начните сначала");
            keybordRequest(chatId, "старт", "start", "Для возврата в меню нажмите старт");
            throw new UploadFileException("Bad response from telegram service: ");
        } else if (user.get().getPart() == 1) {
            sendMessage(chatId, "Введите е-mail получателей и нажмите далее.");
        } else if (user.get().getPart() == 2) {
            sendMassageByAddButton(chatId, "Прикрепите файлы и нажмите далее.");
        } else if (user.get().getPart() == 3) {
            sendMessage(chatId, service.createLinkByShares(chatId));
            keybordRequest(chatId, "старт", "start", "Для возврата в меню нажмите старт");
        }
    }

    private void checkSendFile(Long chatId) {
        Optional<User> user = service.getUser(chatId);
        if (user.isEmpty()) {
            sendMessage(chatId, "Вы не прошли авторизацию пользователем, начните сначала");
            keybordRequest(chatId, "старт", "start", "Для возврата в меню нажмите старт");
            throw new UploadFileException("Bad response from telegram service: ");
        }
        if (user.get().getSharingEmail().isEmpty()) {
            sendMessage(chatId, "Вы не задали почту с которой надо поделиться");
            throw new UploadFileException("Bad response from telegram service: ");
        }
    }

    private void addEmailToSend(String messageText, long chatId, StringBuilder badEmail, StringBuilder acceptedEmail, Pattern pattern) {
        List<String> mailList = List.of(messageText.split(","));
        mailList.forEach(e -> {
            String email = e.replaceAll("\\s", "");
            String addResponseString = checkEmailAndAddResponse(email, chatId, pattern);
            if (addResponseString.contains("принят") || addResponseString.contains("Делимся")) {
                acceptedEmail.append("\n" + email);
            } else {
                badEmail.append(addResponseString);
            }
        });
        if (badEmail.toString().isEmpty()) {
            sendMassageByAddButton(chatId, "е-mail's добавлены для рассылки:" + acceptedEmail + "\n прикрепите файлы");
        } else {
            sendMessage(chatId, badEmail.toString());
        }
    }

    private void sendMassageByAddButton(long chatId, String s) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(s);
        message.setReplyMarkup(replyKeyboardMaker.getMainMenuKeyboard());
        sendMessage(message);
    }

    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправки сообщения");
            throw new RuntimeException(e);
        }
    }

    private String checkEmailAndAddResponse(String messageText, long chatId, Pattern pattern) {
        if (pattern.matcher(messageText).matches()) {
            return service.sveUserResponse(messageText, chatId);
        } else {
            return messageText + ": данный  е-mail не валиден.";
        }
    }

    private void sendMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setReplyMarkup(new ReplyKeyboardRemove());
        sendMessage(message);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        sendMessage(message);
    }

}
