package com.github.saphyra.apphub.service.calendar.service.event.service;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventTitleValidator {
    public void validate(String title) {
        ValidationUtil.notBlank(title, "title");
    }
}
