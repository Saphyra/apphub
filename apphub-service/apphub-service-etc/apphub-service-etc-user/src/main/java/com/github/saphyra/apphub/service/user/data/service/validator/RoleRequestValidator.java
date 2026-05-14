package com.github.saphyra.apphub.service.user.data.service.validator;

import com.github.saphyra.apphub.api.etc.user.model.role.RoleRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleRequestValidator {
    public void validate(RoleRequest roleRequest) {
        ValidationUtil.notNull(roleRequest.getUserId(), "userId");
        ValidationUtil.notNull(roleRequest.getPassword(), "password");
        ValidationUtil.notNull(roleRequest.getRole(), "role");
    }
}
