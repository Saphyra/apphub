package com.github.saphyra.apphub.service.user.authentication.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.OffsetDateTime;

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
    private OffsetDateTime lastAccess;
}
