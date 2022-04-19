package com.github.saphyra.apphub.service.community.blacklist.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
