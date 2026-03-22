package com.github.saphyra.apphub.service.community.blacklist.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = "community", name = "blacklist")
class BlacklistEntity {
    @Id
    private String blacklistId;
    private String userId;
    private String blockedUserId;
}
