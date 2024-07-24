package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.ToolDao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteToolService {
    private final ToolDao toolDao;

    @Transactional
    public void deleteTool(UUID userId, UUID toolId) {
        toolDao.deleteByUserIdAndToolId(userId, toolId);
    }
}
