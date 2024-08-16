package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolTypeModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool_type.ToolType;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool_type.ToolTypeDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ToolTypeFactoryTest {
    private static final UUID TOOL_TYPE_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String NAME = "name";

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ToolTypeDao toolTypeDao;

    @InjectMocks
    private ToolTypeFactory underTest;

    @Test
    void existingStorageBoxId() {
        ToolTypeModel model = ToolTypeModel.builder()
            .toolTypeId(TOOL_TYPE_ID)
            .build();

        assertThat(underTest.getToolTypeId(USER_ID, model)).isEqualTo(TOOL_TYPE_ID);
    }

    @Test
    void blankName() {
        ToolTypeModel model = ToolTypeModel.builder()
            .name(" ")
            .build();

        assertThat(underTest.getToolTypeId(USER_ID, model)).isNull();
    }

    @Test
    void createNew() {
        ToolTypeModel model = ToolTypeModel.builder()
            .name(NAME)
            .build();

        given(idGenerator.randomUuid()).willReturn(TOOL_TYPE_ID);

        assertThat(underTest.getToolTypeId(USER_ID, model)).isEqualTo(TOOL_TYPE_ID);

        ArgumentCaptor<ToolType> argumentCaptor = ArgumentCaptor.forClass(ToolType.class);
        then(toolTypeDao).should().save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue())
            .returns(TOOL_TYPE_ID, ToolType::getToolTypeId)
            .returns(USER_ID, ToolType::getUserId)
            .returns(NAME, ToolType::getName);
    }
}