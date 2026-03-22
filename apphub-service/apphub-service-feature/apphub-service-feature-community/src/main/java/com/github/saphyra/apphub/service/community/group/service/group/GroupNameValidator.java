package com.github.saphyra.apphub.service.community.group.service.group;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class GroupNameValidator {
    void validate(String groupName) {
        ValidationUtil.minLength(groupName, 3, "groupName");
        ValidationUtil.maxLength(groupName, 30, "groupName");
    }
}
