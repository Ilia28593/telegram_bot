package com.vaulterix.telegram_bot.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadKeyResponse {
    private int code;
    private String uploadKey;
    private int packageSize;
    private int packageCount;
    private int uploadAttempts;
    private int completedChunks;
    private int delay;

}