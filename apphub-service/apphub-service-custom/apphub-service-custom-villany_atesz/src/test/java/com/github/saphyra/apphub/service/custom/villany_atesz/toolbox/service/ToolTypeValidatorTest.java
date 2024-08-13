package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolTypeModel;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool_type.ToolTypeDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ToolTypeValidatorTest {
    private static final UUID TOOL_TYPE_ID = UUID.randomUUID();

    @Mock
    private ToolTypeDao toolTypeDao;

    @InjectMocks
    private ToolTypeValidator underTest;

    @Test
    void nullStorageBoxIdAndName() {
        ExceptionValidator.validateInvalidParam(() -> underTest.validate(ToolTypeModel.builder().build()), "toolType.name", "must not be null");
    }

    @Test
    void storageBoxDoesNotExist() {
        given(toolTypeDao.exists(TOOL_TYPE_ID)).willReturn(false);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(ToolTypeModel.builder().toolTypeId(TOOL_TYPE_ID).build()), "toolTypeId", "not found");
    }

    @Test
    void onlyNameFilled() {
        underTest.validate(ToolTypeModel.builder().name("asd").build());
    }

    @Test
    void storageBoxFound() {
        given(toolTypeDao.exists(TOOL_TYPE_ID)).willReturn(true);

        underTest.validate(ToolTypeModel.builder().toolTypeId(TOOL_TYPE_ID).build());
    }
}