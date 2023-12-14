package com.vaulterix.telegram_bot.model.shares;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class StorageEntityModel {
    private String id;
    private String type;
}