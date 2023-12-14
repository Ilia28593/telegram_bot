package com.vaulterix.telegram_bot.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vaulterix.telegram_bot.model.shares.StorageEntityModel;
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
public class ShareRequest {
    private String id;
    private StorageEntityModel entity;
    private UserPermissionInfoRequest userPermissionInfo;
}
