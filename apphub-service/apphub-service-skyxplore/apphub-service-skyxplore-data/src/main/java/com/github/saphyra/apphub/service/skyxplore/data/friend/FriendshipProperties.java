package com.github.saphyra.apphub.service.skyxplore.data.friend;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class FriendshipProperties {
    @Value("${friendship.activeTimeoutMinutes}")
    private Integer activeTimeoutMinutes;
}
