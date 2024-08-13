package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CreateToolRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StorageBoxModel;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolStatus;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolTypeModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.storage_box.StorageBox;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.storage_box.StorageBoxDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool.Tool;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool_type.ToolType;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool_type.ToolTypeDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ToolFactoryTest {
    private static final UUID TOOL_ID = UUID.randomUUID();
    private static final String BRAND = "brand";
    private static final String NAME = "name";
    private static final Integer COST = 234;
    private static final LocalDate ACQUIRED_AT = LocalDate.now();
    private static final LocalDate WARRANTY_EXPIRES_AT = ACQUIRED_AT.minusDays(1);
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID STORAGE_BOX_ID = UUID.randomUUID();
    private static final UUID TOOL_TYPE_ID = UUID.randomUUID();
    private static final String STORAGE_BOX_NAME = "storage-box-name";
    private static final String TOOL_TYPE_NAME = "tool-type-name";

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ToolTypeDao toolTypeDao;

    @Mock
    private StorageBoxDao storageBoxDao;

    @InjectMocks
    private ToolFactory underTest;

    @Test
    void create_existingStorageBoxAndToolType() {
        given(idGenerator.randomUuid()).willReturn(TOOL_ID);

        CreateToolRequest request = CreateToolRequest.builder()
            .brand(BRAND)
            .name(NAME)
            .cost(COST)
            .acquiredAt(ACQUIRED_AT)
            .warrantyExpiresAt(WARRANTY_EXPIRES_AT)
            .storageBox(StorageBoxModel.builder().storageBoxId(STORAGE_BOX_ID).build())
            .toolType(ToolTypeModel.builder().toolTypeId(TOOL_TYPE_ID).build())
            .build();

        assertThat(underTest.create(USER_ID, request))
            .returns(TOOL_ID, Tool::getToolId)
            .returns(USER_ID, Tool::getUserId)
            .returns(BRAND, Tool::getBrand)
            .returns(NAME, Tool::getName)
            .returns(COST, Tool::getCost)
            .returns(ACQUIRED_AT, Tool::getAcquiredAt)
            .returns(ToolStatus.DEFAULT, Tool::getStatus)
            .returns(WARRANTY_EXPIRES_AT, Tool::getWarrantyExpiresAt)
            .returns(null, Tool::getScrappedAt)
            .returns(STORAGE_BOX_ID, Tool::getStorageBoxId)
            .returns(TOOL_TYPE_ID, Tool::getToolTypeId);
    }

    @Test
    void create_blankStorageBoxAndToolType() {
        given(idGenerator.randomUuid()).willReturn(TOOL_ID);

        CreateToolRequest request = CreateToolRequest.builder()
            .brand(BRAND)
            .name(NAME)
            .cost(COST)
            .acquiredAt(ACQUIRED_AT)
            .warrantyExpiresAt(WARRANTY_EXPIRES_AT)
            .storageBox(StorageBoxModel.builder().name("").build())
            .toolType(ToolTypeModel.builder().name("").build())
            .build();

        assertThat(underTest.create(USER_ID, request))
            .returns(TOOL_ID, Tool::getToolId)
            .returns(USER_ID, Tool::getUserId)
            .returns(BRAND, Tool::getBrand)
            .returns(NAME, Tool::getName)
            .returns(COST, Tool::getCost)
            .returns(ACQUIRED_AT, Tool::getAcquiredAt)
            .returns(ToolStatus.DEFAULT, Tool::getStatus)
            .returns(WARRANTY_EXPIRES_AT, Tool::getWarrantyExpiresAt)
            .returns(null, Tool::getScrappedAt)
            .returns(null, Tool::getStorageBoxId)
            .returns(null, Tool::getToolTypeId);
    }

    @Test
    void create_createStorageBoxAndToolType() {
        given(idGenerator.randomUuid())
            .willReturn(TOOL_ID)
            .willReturn(STORAGE_BOX_ID)
            .willReturn(TOOL_TYPE_ID);

        CreateToolRequest request = CreateToolRequest.builder()
            .brand(BRAND)
            .name(NAME)
            .cost(COST)
            .acquiredAt(ACQUIRED_AT)
            .warrantyExpiresAt(WARRANTY_EXPIRES_AT)
            .storageBox(StorageBoxModel.builder().name(STORAGE_BOX_NAME).build())
            .toolType(ToolTypeModel.builder().name(TOOL_TYPE_NAME).build())
            .build();

        assertThat(underTest.create(USER_ID, request))
            .returns(TOOL_ID, Tool::getToolId)
            .returns(USER_ID, Tool::getUserId)
            .returns(BRAND, Tool::getBrand)
            .returns(NAME, Tool::getName)
            .returns(COST, Tool::getCost)
            .returns(ACQUIRED_AT, Tool::getAcquiredAt)
            .returns(ToolStatus.DEFAULT, Tool::getStatus)
            .returns(WARRANTY_EXPIRES_AT, Tool::getWarrantyExpiresAt)
            .returns(null, Tool::getScrappedAt)
            .returns(STORAGE_BOX_ID, Tool::getStorageBoxId)
            .returns(TOOL_TYPE_ID, Tool::getToolTypeId);

        ArgumentCaptor<StorageBox> storageBoxArgumentCaptor = ArgumentCaptor.forClass(StorageBox.class);
        then(storageBoxDao).should().save(storageBoxArgumentCaptor.capture());
        assertThat(storageBoxArgumentCaptor.getValue())
            .returns(STORAGE_BOX_ID, StorageBox::getStorageBoxId)
            .returns(USER_ID, StorageBox::getUserId)
            .returns(STORAGE_BOX_NAME, StorageBox::getName);

        ArgumentCaptor<ToolType> toolTypeArgumentCaptor = ArgumentCaptor.forClass(ToolType.class);
        then(toolTypeDao).should().save(toolTypeArgumentCaptor.capture());
        assertThat(toolTypeArgumentCaptor.getValue())
            .returns(TOOL_TYPE_ID, ToolType::getToolTypeId)
            .returns(USER_ID, ToolType::getUserId)
            .returns(TOOL_TYPE_NAME, ToolType::getName);
    }
}