package com.github.saphyra.apphub.service.user.data.dao.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(schema = "apphub_user", name = "apphub_user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UserEntity {
    @Id
    private String userId;
    private String email;
    private String username;
    private String password;
    private String language;
    @Builder.Default
    private Boolean markedForDeletion = false;
    private LocalDateTime markedForDeletionAt;
    private Integer passwordFailureCount;
    private LocalDateTime lockedUntil;
}
