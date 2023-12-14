package com.vaulterix.telegram_bot.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileDataResponse {
    private int code;
    private String id;
    private String title;
    private String type;
    private String userId;
    private String parentId;
    private String path;
    private String pathId;
    private String versionId;
    private String versionCount;
    private boolean shared;
    private int fileCount;
    private int folderCount;
    private long fileSize;
    private String contentType;
    private String createdBy;
    private String createdAt;
    private String updatedBy;
    private String updatedAt;
}