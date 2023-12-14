package com.vaulterix.telegram_bot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserDTO {
    private String id;
    private List<String> rolesId;
    private List<String> groupsId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String language;
    private Long quota;
    private String timeZone;
    private String phone;
    private String userType;
    private Boolean adUser;
    private String department;
    private String position;
    private Boolean compromised;
    private Boolean blocked;
    private Boolean softDeletion;
    private String createdAt;
    private Integer licenseAgreement;
}
