package com.vaulterix.telegram_bot.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotService {
    String consumePhotoMessageUpdates(Update update);
}
