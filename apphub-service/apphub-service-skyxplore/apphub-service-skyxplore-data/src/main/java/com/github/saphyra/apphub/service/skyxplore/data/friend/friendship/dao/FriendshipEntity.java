package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
