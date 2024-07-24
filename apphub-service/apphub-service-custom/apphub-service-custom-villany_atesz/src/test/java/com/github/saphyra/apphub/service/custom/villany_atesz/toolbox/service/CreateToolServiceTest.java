package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CreateToolRequest;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.Tool;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.ToolDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CreateToolServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private CreateToolRequestValidator createToolRequestValidator;

    @Mock
    private ToolFactory toolFactory;

    @Mock
    private ToolDao toolDao;

    @InjectMocks
    private CreateToolService underTest;

    @Mock
    private CreateToolRequest request;

    @Mock
    private Tool tool;

    @Test
    void create() {
        given(toolFactory.create(USER_ID, request)).willReturn(tool);

        underTest.create(USER_ID, request);

        then(createToolRequestValidator).should().validate(request);
        then(toolDao).should().save(tool);
    }
}