package com.vaulterix.telegram_bot.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotService {
    void consumeTextMessageUpdates(Update update);

    void consumeDocMessageUpdates(Update update);

    String consumePhotoMessageUpdates(Update update);
}
