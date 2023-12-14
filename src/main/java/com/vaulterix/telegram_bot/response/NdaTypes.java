package com.vaulterix.telegram_bot.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum NdaTypes {

    ONCE("ONCE"),
    ALWAYS("ALWAYS");

    private final String value;
}
