package com.vaulterix.telegram_bot.response;

import com.vaulterix.telegram_bot.dto.EntityDTO;
import com.vaulterix.telegram_bot.dto.UserInfoDTO;
import com.vaulterix.telegram_bot.model.shares.UserPermission;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ShareInfoResponse {
    private int code;
    private String id;
    private EntityDTO entity;
    private String link;
    private UserInfoDTO shareCreator;
    private UserPermission userPermissionInfo;
    private String createdAt;
    private Boolean blocked;

}
