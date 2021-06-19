package com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(schema = "skyxplore", name = "friend_request")
class FriendRequestEntity {
    @Id
    private String friendRequestId;

    private String senderId;
    private String friendId;
}
