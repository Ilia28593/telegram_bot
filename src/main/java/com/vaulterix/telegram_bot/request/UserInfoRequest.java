package com.vaulterix.telegram_bot.request;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserInfoRequest {
    private String email;
}
