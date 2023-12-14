package com.vaulterix.telegram_bot.response;

import com.vaulterix.telegram_bot.dto.EntityDTO;
import com.vaulterix.telegram_bot.dto.UserInfoDTO;
import com.vaulterix.telegram_bot.dto.UserPermissionDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
public class ShareResponse {
    private int code;
    private UUID id;
    public EntityDTO entity;
    public UserInfoDTO shareCreator;
    public UserPermissionDTO userPermissionInfo;
    private String link;
    private String createdAt;
    private Boolean blocked;

}
