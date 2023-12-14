package com.vaulterix.telegram_bot.model.shares;


import com.vaulterix.telegram_bot.request.UserPermissionInfoRequest;
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
public class CreateSharedLinkRequest {
    private StorageEntityModel entity;
    private UserPermissionInfoRequest userPermissionInfo;
}