package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolTypeModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool_type.ToolType;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool_type.ToolTypeDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static io.micrometer.common.util.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
@Slf4j
class ToolTypeFactory {
    private final IdGenerator idGenerator;
    private final ToolTypeDao toolTypeDao;

    UUID getToolTypeId(UUID userId, ToolTypeModel toolType) {
        return Optional.ofNullable(toolType.getToolTypeId())
            .orElseGet(() -> createToolType(userId, toolType.getName()));
    }

    private UUID createToolType(UUID userId, String name) {
        if (isBlank(name)) {
            return null;
        }

        log.info("Creating new toolType for userId {}", userId);

        ToolType toolType = ToolType.builder()
            .toolTypeId(idGenerator.randomUuid())
            .userId(userId)
            .name(name)
            .build();

        toolTypeDao.save(toolType);

        return toolType.getToolTypeId();
    }
}
