package com.github.saphyra.apphub.service.notebook.service.pin.group;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class PinGroupNameValidator {
    void validate(String pinGroupName) {
        ValidationUtil.notBlank(pinGroupName, "pinGroupName");
        ValidationUtil.maxLength(pinGroupName, 30, "pinGroupName");
    }
}
