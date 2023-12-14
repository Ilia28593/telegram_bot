package com.vaulterix.telegram_bot.request;

import com.vaulterix.telegram_bot.model.shares.UserProtectionRequest;
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
public class UserPermissionInfoRequest {
    private boolean allowPrinting;
    private boolean allowView;
    private boolean dateTimeOnWaterMark;
    private boolean downloadPlainFile;
    private boolean downloadWatermarkFile;
    private String expirationDate;
    private int numberOfAllowedDownloads;
    private int numberOfDownloads;
    private String password;
    private String permissionLevel;
    private boolean restrictNumberOfDownloads;
    private boolean secureView;
    private boolean uploadFileAllowed;
    private boolean waterMarkReceiverEmail;
    private boolean waterMarkSenderEmail;
    private String waterMarkText;
    private boolean allowViewXlsx;
    private UserProtectionRequest userProtection;
    private boolean ipAddressOnWaterMark;
    private boolean allowEditFile;
    private boolean showNda;
    private String ndaId;
    private boolean jointAccessFile;
    private boolean allowCopy;
    private boolean secureViewEdit;
    private boolean addMetadataFile;
    private boolean addMetadataPdfFile;
    private UserInfoRequest principal;
}
