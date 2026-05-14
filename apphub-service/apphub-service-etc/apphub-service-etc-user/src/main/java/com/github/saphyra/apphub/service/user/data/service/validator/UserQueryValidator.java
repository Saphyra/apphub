package com.github.saphyra.apphub.service.user.data.service.validator;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import org.springframework.stereotype.Component;

@Component
public class UserQueryValidator {
    public void validateQuery(String query) {
        ValidationUtil.minLength(query, 3, "query");
    }
}
