package com.github.saphyra.apphub.service.user.data.dao.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class CredentialEntity  {
    private String credential;
    private String userId;
}
