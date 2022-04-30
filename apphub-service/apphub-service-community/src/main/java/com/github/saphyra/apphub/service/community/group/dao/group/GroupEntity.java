package com.github.saphyra.apphub.service.community.group.dao.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = "community", name = "community_group")
class GroupEntity {
    @Id
    private String groupId;
    private String ownerId;
    private String name;
    @Enumerated(EnumType.STRING)
    private GroupInvitationType invitationType;
}
