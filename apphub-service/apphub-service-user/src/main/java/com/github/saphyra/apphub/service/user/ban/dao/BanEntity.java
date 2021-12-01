package com.github.saphyra.apphub.service.user.ban.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "apphub_user", name = "ban")
class BanEntity {
    @Id
    private String id;
    private String userId;
    private String bannedRole;
    private LocalDateTime expiration;
    private Boolean permanent;
    private String reason;
    private String bannedBy;
}
