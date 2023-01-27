package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(schema = "skyxplore", name = "friendship")
class FriendshipEntity {
    @Id
    private String friendshipId;

    @Column(name = "friend_1")
    private String friend1;

    @Column(name = "friend_2")
    private String friend2;
}
