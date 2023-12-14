package com.vaulterix.telegram_bot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EntityDTO {

    private String id;
    private String title;
    private String type;
    private String userId;
    private String createdAt;
    private String updatedAt;

}
