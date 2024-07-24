package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CreateToolRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class CreateToolRequestValidator {
    public void validate(CreateToolRequest request) {
        ValidationUtil.notNull(request.getBrand(), "brand");
        ValidationUtil.notBlank(request.getName(), "name");
        ValidationUtil.notNull(request.getCost(), "cost");
        ValidationUtil.notNull(request.getAcquiredAt(), "acquiredAt");
    }
}
