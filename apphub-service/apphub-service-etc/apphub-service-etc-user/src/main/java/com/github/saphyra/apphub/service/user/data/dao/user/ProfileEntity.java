package com.github.saphyra.apphub.service.user.data.dao.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
class ProfileEntity {
    private String userId;
    private String email;
    private String username;
    private String language;
    private String password;
    private Integer passwordFailureCount;
    private Long lockedUntil;
}
