package com.vaulterix.telegram_bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserProtectionDTO {
    private boolean userPasswordEnable;
    private boolean allowPrintingDownloaded;
    private boolean allowCopy;
    private boolean allowEdit;
}
