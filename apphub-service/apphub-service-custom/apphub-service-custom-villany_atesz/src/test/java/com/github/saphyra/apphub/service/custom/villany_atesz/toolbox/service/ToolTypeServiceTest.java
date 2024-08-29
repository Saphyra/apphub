package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool_type.ToolType;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool_type.ToolTypeDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ToolTypeServiceTest {
    private static final UUID TOOL_TYPE_ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private ToolTypeDao toolTypeDao;

    @InjectMocks
    private ToolTypeService underTest;

    @Mock
    private ToolType toolType;

    @Test
    void edit_blank() {
        ExceptionValidator.validateInvalidParam(() -> underTest.edit(TOOL_TYPE_ID, " "), "name", "must not be null or blank");
    }

    @Test
    void edit() {
        given(toolTypeDao.findByIdValidated(TOOL_TYPE_ID)).willReturn(toolType);

        underTest.edit(TOOL_TYPE_ID, NAME);

        then(toolType).should().setName(NAME);
        then(toolTypeDao).should().save(toolType);
    }

    @Test
    void delete() {
        underTest.delete(USER_ID, TOOL_TYPE_ID);

        then(toolTypeDao).should().deleteByUserIdAndToolTypeId(USER_ID, TOOL_TYPE_ID);
    }
}