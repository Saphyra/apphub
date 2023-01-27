package com.github.saphyra.apphub.service.user.authentication.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(schema = "apphub_user", name = "access_token")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class AccessTokenEntity {
    @Id
    private String accessTokenId;
    private String userId;
    private boolean persistent;
    private LocalDateTime lastAccess;
}
