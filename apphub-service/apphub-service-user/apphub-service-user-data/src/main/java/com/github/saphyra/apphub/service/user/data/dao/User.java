package com.github.saphyra.apphub.service.user.data.dao;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class User {
    private final UUID userId;
    private final String email;
    private final String username;
    private final String password;
}
