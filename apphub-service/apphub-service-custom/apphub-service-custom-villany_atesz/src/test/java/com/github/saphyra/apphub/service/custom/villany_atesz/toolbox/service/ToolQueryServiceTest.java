package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StorageBoxModel;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolStatus;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolTypeModel;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.storage_box.StorageBox;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.storage_box.StorageBoxDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool.Tool;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool.ToolDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool_type.ToolType;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool_type.ToolTypeDao;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ToolQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID TOOL_ID = UUID.randomUUID();
    private static final String BRAND = "brand";
    private static final String NAME = "name";
    private static final Integer COST = 24;
    private static final LocalDate ACQUIRED_AT = LocalDate.now();
    private static final LocalDate WARRANTY_EXPIRES_AT = ACQUIRED_AT.minusDays(1);
    private static final LocalDate SCRAPPED_AT = WARRANTY_EXPIRES_AT.minusDays(1);
    private static final UUID TOOL_TYPE_ID = UUID.randomUUID();
    private static final UUID STORAGE_BOX_ID = UUID.randomUUID();
    private static final String TOOL_TYPE_NAME = "tool-type-name";
    private static final String STORAGE_BOX_NAME = "storage-box-name";

    @Mock
    private ToolTypeDao toolTypeDao;

    @Mock
    private StorageBoxDao storageBoxDao;

    @Mock
    private ToolDao toolDao;

    @InjectMocks
    private ToolQueryService underTest;

    @Mock
    private Tool tool;

    @Mock
    private ToolType toolType;

    @Mock
    private StorageBox storageBox;

    @Test
    void getTools() {
        given(toolDao.getByUserId(USER_ID)).willReturn(List.of(tool));

        given(tool.getToolId()).willReturn(TOOL_ID);
        given(tool.getBrand()).willReturn(BRAND);
        given(tool.getName()).willReturn(NAME);
        given(tool.getCost()).willReturn(COST);
        given(tool.getAcquiredAt()).willReturn(ACQUIRED_AT);
        given(tool.getWarrantyExpiresAt()).willReturn(WARRANTY_EXPIRES_AT);
        given(tool.getStatus()).willReturn(ToolStatus.DEFAULT);
        given(tool.getScrappedAt()).willReturn(SCRAPPED_AT);
        given(tool.getToolTypeId()).willReturn(TOOL_TYPE_ID);
        given(tool.getStorageBoxId()).willReturn(STORAGE_BOX_ID);
        given(tool.isInventoried()).willReturn(true);

        given(toolTypeDao.findById(TOOL_TYPE_ID)).willReturn(Optional.of(toolType));
        given(toolType.getToolTypeId()).willReturn(TOOL_TYPE_ID);
        given(toolType.getName()).willReturn(TOOL_TYPE_NAME);

        given(storageBoxDao.findById(STORAGE_BOX_ID)).willReturn(Optional.of(storageBox));
        given(storageBox.getStorageBoxId()).willReturn(STORAGE_BOX_ID);
        given(storageBox.getName()).willReturn(STORAGE_BOX_NAME);

        CustomAssertions.singleListAssertThat(underTest.getTools(USER_ID))
            .returns(TOOL_ID, ToolResponse::getToolId)
            .returns(ToolTypeModel.builder().toolTypeId(TOOL_TYPE_ID).name(TOOL_TYPE_NAME).build(), ToolResponse::getToolType)
            .returns(StorageBoxModel.builder().storageBoxId(STORAGE_BOX_ID).name(STORAGE_BOX_NAME).build(), ToolResponse::getStorageBox)
            .returns(BRAND, ToolResponse::getBrand)
            .returns(NAME, ToolResponse::getName)
            .returns(COST, ToolResponse::getCost)
            .returns(ACQUIRED_AT, ToolResponse::getAcquiredAt)
            .returns(WARRANTY_EXPIRES_AT, ToolResponse::getWarrantyExpiresAt)
            .returns(ToolStatus.DEFAULT, ToolResponse::getStatus)
            .returns(SCRAPPED_AT, ToolResponse::getScrappedAt)
            .returns(true, ToolResponse::getInventoried);
    }

    @Test
    void getTools_noToolTypeAndStorageBox() {
        given(toolDao.getByUserId(USER_ID)).willReturn(List.of(tool));

        given(tool.getToolId()).willReturn(TOOL_ID);
        given(tool.getBrand()).willReturn(BRAND);
        given(tool.getName()).willReturn(NAME);
        given(tool.getCost()).willReturn(COST);
        given(tool.getAcquiredAt()).willReturn(ACQUIRED_AT);
        given(tool.getWarrantyExpiresAt()).willReturn(WARRANTY_EXPIRES_AT);
        given(tool.getStatus()).willReturn(ToolStatus.DEFAULT);
        given(tool.getScrappedAt()).willReturn(SCRAPPED_AT);
        given(tool.getToolTypeId()).willReturn(TOOL_TYPE_ID);
        given(tool.getStorageBoxId()).willReturn(STORAGE_BOX_ID);
        given(tool.isInventoried()).willReturn(true);

        given(toolTypeDao.findById(TOOL_TYPE_ID)).willReturn(Optional.empty());

        given(storageBoxDao.findById(STORAGE_BOX_ID)).willReturn(Optional.empty());

        CustomAssertions.singleListAssertThat(underTest.getTools(USER_ID))
            .returns(TOOL_ID, ToolResponse::getToolId)
            .returns(null, ToolResponse::getToolType)
            .returns(null, ToolResponse::getStorageBox)
            .returns(BRAND, ToolResponse::getBrand)
            .returns(NAME, ToolResponse::getName)
            .returns(COST, ToolResponse::getCost)
            .returns(ACQUIRED_AT, ToolResponse::getAcquiredAt)
            .returns(WARRANTY_EXPIRES_AT, ToolResponse::getWarrantyExpiresAt)
            .returns(ToolStatus.DEFAULT, ToolResponse::getStatus)
            .returns(SCRAPPED_AT, ToolResponse::getScrappedAt)
            .returns(true, ToolResponse::getInventoried);
    }

    @Test
    void getTotalValue() {
        given(toolDao.getByUserId(USER_ID)).willReturn(List.of(tool, tool));
        given(tool.getCost()).willReturn(COST);

        assertThat(underTest.getTotalValue(USER_ID)).isEqualTo(2 * COST);
    }

    @Test
    void getToolTypes() {
        given(toolTypeDao.getByUserId(USER_ID)).willReturn(List.of(toolType));
        given(toolType.getToolTypeId()).willReturn(TOOL_TYPE_ID);
        given(toolType.getName()).willReturn(TOOL_TYPE_NAME);

        CustomAssertions.singleListAssertThat(underTest.getToolTypes(USER_ID))
            .returns(TOOL_TYPE_ID, ToolTypeModel::getToolTypeId)
            .returns(TOOL_TYPE_NAME, ToolTypeModel::getName);
    }

    @Test
    void getStorageBoxes() {
        given(storageBoxDao.getByUserId(USER_ID)).willReturn(List.of(storageBox));
        given(storageBox.getStorageBoxId()).willReturn(STORAGE_BOX_ID);
        given(storageBox.getName()).willReturn(STORAGE_BOX_NAME);

        CustomAssertions.singleListAssertThat(underTest.getStorageBoxes(USER_ID))
            .returns(STORAGE_BOX_ID, StorageBoxModel::getStorageBoxId)
            .returns(STORAGE_BOX_NAME, StorageBoxModel::getName);
    }
}