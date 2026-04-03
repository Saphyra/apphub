package com.github.saphyra.apphub.service.community.friendship.dao.request;

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
@Table(schema = "community", name = "friend_request")
public class FriendRequestEntity {
    @Id
    private String friendRequestId;
    private String senderId;
    private String receiverId;
}
