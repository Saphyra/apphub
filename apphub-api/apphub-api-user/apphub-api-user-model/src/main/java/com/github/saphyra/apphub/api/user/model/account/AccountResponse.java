package com.github.saphyra.apphub.api.user.model.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AccountResponse {
    private UUID userId;
    private String email;
    private String username;
}
