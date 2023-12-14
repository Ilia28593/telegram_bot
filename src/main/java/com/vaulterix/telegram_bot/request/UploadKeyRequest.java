package com.vaulterix.telegram_bot.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UploadKeyRequest {
    private String fileName;
    private long fileSize;
    private String fileType;
    private String folderId;
    boolean toRewrite;
}