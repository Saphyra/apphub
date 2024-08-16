package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolTypeModel;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool_type.ToolTypeDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class ToolTypeValidator {
    private final ToolTypeDao toolTypeDao;

    void validate(ToolTypeModel toolType) {
        if (isNull(toolType.getToolTypeId())) {
            ValidationUtil.notNull(toolType.getName(), "toolType.name");
        } else if (!toolTypeDao.exists(toolType.getToolTypeId())) {
            throw ExceptionFactory.invalidParam("toolTypeId", "not found");
        }
    }
}
