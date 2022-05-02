package com.github.saphyra.apphub.service.community.group.dao.member;

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
