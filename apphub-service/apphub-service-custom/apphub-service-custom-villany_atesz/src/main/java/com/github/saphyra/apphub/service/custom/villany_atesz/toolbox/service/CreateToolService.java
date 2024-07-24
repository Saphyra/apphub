package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CreateToolRequest;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.Tool;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.ToolDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateToolService {
    private final CreateToolRequestValidator createToolRequestValidator;
    private final ToolFactory toolFactory;
    private final ToolDao toolDao;

    public void create(UUID userId, CreateToolRequest request) {
        createToolRequestValidator.validate(request);

        Tool tool = toolFactory.create(userId, request);
        toolDao.save(tool);
    }
}
