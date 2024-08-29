package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool_type.ToolType;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool_type.ToolTypeDao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ToolTypeService {
    private final ToolTypeDao toolTypeDao;

    public void edit(UUID toolTypeId, String name) {
        ValidationUtil.notBlank(name, "name");

        ToolType toolType = toolTypeDao.findByIdValidated(toolTypeId);

        toolType.setName(name);

        toolTypeDao.save(toolType);
    }

    @Transactional
    public void delete(UUID userId, UUID toolTypeId) {
        toolTypeDao.deleteByUserIdAndToolTypeId(userId, toolTypeId);
    }
}
