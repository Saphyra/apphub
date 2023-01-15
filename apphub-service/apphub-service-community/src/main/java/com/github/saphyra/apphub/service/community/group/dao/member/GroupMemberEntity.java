package com.github.saphyra.apphub.service.community.group.dao.member;

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
@Table(schema = "community", name = "community_group_member")
class GroupMemberEntity {
    @Id
    private String groupMemberId;
    private String groupId;
    private String userId;
    private Boolean canInvite;
    private Boolean canKick;
    private Boolean canModifyRoles;
}
