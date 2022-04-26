package com.github.saphyra.apphub.service.community.friendship.dao.friend;

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
@Table(schema = "community", name = "friendship")
class FriendshipEntity {
    @Id
    private String friendshipId;
    private String userId;
    private String friendId;
}
