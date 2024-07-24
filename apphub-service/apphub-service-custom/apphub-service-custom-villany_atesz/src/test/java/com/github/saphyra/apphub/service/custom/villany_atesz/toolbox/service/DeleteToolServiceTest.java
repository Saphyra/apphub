package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.ToolDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DeleteToolServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID TOOL_ID = UUID.randomUUID();

    @Mock
    private ToolDao toolDao;

    @InjectMocks
    private DeleteToolService underTest;

    @Test
    void deleteTool() {
        underTest.deleteTool(USER_ID, TOOL_ID);

        then(toolDao).should().deleteByUserIdAndToolId(USER_ID, TOOL_ID);
    }
}