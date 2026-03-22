package com.github.saphyra.apphub.service.community.friendship.dao.friend;

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
@Table(schema = "community", name = "friendship")
class FriendshipEntity {
    @Id
    private String friendshipId;
    private String userId;
    private String friendId;
}
