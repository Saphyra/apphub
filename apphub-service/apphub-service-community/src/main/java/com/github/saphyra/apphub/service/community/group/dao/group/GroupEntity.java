package com.github.saphyra.apphub.service.community.group.dao.group;

import com.github.saphyra.apphub.api.community.model.response.group.GroupInvitationType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(schema = "community", name = "community_group")
class GroupEntity {
    @Id
    private String groupId;
    private String ownerId;
    private String name;
    @Enumerated(EnumType.STRING)
    private GroupInvitationType invitationType;
}
