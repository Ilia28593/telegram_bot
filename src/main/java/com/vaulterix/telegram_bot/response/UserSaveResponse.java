package com.vaulterix.telegram_bot.response;

import com.vaulterix.telegram_bot.model.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserSaveResponse {
    private int code;
    private boolean isUserConvertedFromExternal;
    private UserDTO user;
}
