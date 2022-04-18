package com.github.saphyra.apphub.service.community.friendship.dao.request;

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
@Table(schema = "community", name = "friend_request")
public class FriendRequestEntity {
    @Id
    private String friendRequestId;
    private String senderId;
    private String receiverId;
}
