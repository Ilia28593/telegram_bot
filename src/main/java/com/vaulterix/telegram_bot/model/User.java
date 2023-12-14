package com.vaulterix.telegram_bot.model;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Accessors(chain = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatId;
    private String userEmail;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> sharingEmail;
    private LocalDateTime dateCreateRequest;
    private String folderId;
    private Integer part;
    private boolean status;
}
