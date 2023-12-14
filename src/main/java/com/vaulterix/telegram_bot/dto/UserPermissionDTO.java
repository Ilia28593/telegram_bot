package com.vaulterix.telegram_bot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserPermissionDTO {
    private Boolean allowPrinting;
    private Boolean allowView;
    private boolean allowEditFile;
    private Boolean dateTimeOnWaterMark;
    private Boolean downloadPlainFile;
    private Boolean downloadWatermarkFile;
    private String expirationDate;
    private Integer numberOfAllowedDownloads;
    private Integer numberOfDownloads;
    private String permissionLevel;
    private Boolean restrictNumberOfDownloads;
    private Boolean secureView;
    private Boolean uploadFileAllowed;
    private Boolean waterMarkReceiverEmail;
    private Boolean waterMarkSenderEmail;
    private String waterMarkText;
    private boolean allowViewXlsx;
    private boolean ipAddressOnWaterMark;
    private UserProtectionDTO userProtection;
    private UserInfoDTO principal;
    private boolean jointAccessFile;
    private boolean allowCopy;
    private boolean secureViewEdit;
    private boolean addMetadataFile;
    private boolean addMetadataPdfFile;

}
