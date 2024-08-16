package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolStatus;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool.Tool;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool.ToolDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SetToolStatusService {
    private final ToolDao toolDao;
    private final DateTimeUtil dateTimeUtil;

    public void setStatus(UUID toolId, ToolStatus toolStatus) {
        ValidationUtil.notNull(toolStatus, "status");

        Tool tool = toolDao.findByIdValidated(toolId);

        tool.setStatus(toolStatus);

        if (ToolStatus.SCRAPPED == toolStatus) {
            tool.setScrappedAt(dateTimeUtil.getCurrentDate());
        }

        toolDao.save(tool);
    }
}
