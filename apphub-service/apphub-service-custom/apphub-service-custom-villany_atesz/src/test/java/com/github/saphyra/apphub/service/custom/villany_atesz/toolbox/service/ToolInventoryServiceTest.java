package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StorageBoxModel;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolStatus;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolTypeModel;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool.Tool;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool.ToolDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ToolInventoryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID TOOL_ID = UUID.randomUUID();
    private static final UUID STORAGE_BOX_ID = UUID.randomUUID();
    private static final UUID TOOL_TYPE_ID = UUID.randomUUID();
    private static final String BRAND = "brand";
    private static final String NAME = "name";
    private static final Integer COST = 42;
    private static final LocalDate ACQUIRED_AT = LocalDate.now();
    private static final LocalDate WARRANTY_EXPIRES_AT = ACQUIRED_AT.plusDays(1);
    private static final LocalDate SCRAPPED_AT = WARRANTY_EXPIRES_AT.plusDays(1);

    @Mock
    private StorageBoxValidator storageBoxValidator;

    @Mock
    private ToolDao toolDao;

    @Mock
    private StorageBoxFactory storageBoxFactory;

    @Mock
    private ToolTypeValidator toolTypeValidator;

    @Mock
    private ToolTypeFactory toolTypeFactory;

    @InjectMocks
    private ToolInventoryService underTest;

    @Mock
    private StorageBoxModel storageBoxModel;

    @Mock
    private ToolTypeModel toolTypeModel;

    @Mock
    private Tool tool;

    @Test
    void editStorageBox() {
        given(toolDao.findByIdValidated(TOOL_ID)).willReturn(tool);
        given(storageBoxFactory.getStorageBoxId(USER_ID, storageBoxModel)).willReturn(STORAGE_BOX_ID);
        given(tool.getStorageBoxId()).willReturn(STORAGE_BOX_ID);

        assertThat(underTest.editStorageBox(USER_ID, TOOL_ID, storageBoxModel)).isEqualTo(STORAGE_BOX_ID);

        then(storageBoxValidator).should().validate(storageBoxModel);
        then(tool).should().setStorageBoxId(STORAGE_BOX_ID);
        then(toolDao).should().save(tool);
    }

    @Test
    void editToolType() {
        given(toolDao.findByIdValidated(TOOL_ID)).willReturn(tool);
        given(toolTypeFactory.getToolTypeId(USER_ID, toolTypeModel)).willReturn(TOOL_TYPE_ID);
        given(tool.getToolTypeId()).willReturn(TOOL_TYPE_ID);

        assertThat(underTest.editToolType(USER_ID, TOOL_ID, toolTypeModel)).isEqualTo(TOOL_TYPE_ID);

        then(toolTypeValidator).should().validate(toolTypeModel);
        then(tool).should().setToolTypeId(TOOL_TYPE_ID);
        then(toolDao).should().save(tool);
    }

    @Test
    void editBrand_null() {
        ExceptionValidator.validateInvalidParam(() -> underTest.editBrand(TOOL_ID, null), "brand", "must not be null");
    }

    @Test
    void editBrand() {
        given(toolDao.findByIdValidated(TOOL_ID)).willReturn(tool);

        underTest.editBrand(TOOL_ID, BRAND);

        then(tool).should().setBrand(BRAND);
        then(toolDao).should().save(tool);
    }

    @Test
    void editName_null() {
        ExceptionValidator.validateInvalidParam(() -> underTest.editName(TOOL_ID, " "), "name", "must not be null or blank");
    }

    @Test
    void editName() {
        given(toolDao.findByIdValidated(TOOL_ID)).willReturn(tool);

        underTest.editName(TOOL_ID, NAME);

        then(tool).should().setName(NAME);
        then(toolDao).should().save(tool);
    }

    @Test
    void editCost_null() {
        ExceptionValidator.validateInvalidParam(() -> underTest.editCost(TOOL_ID, null), "cost", "must not be null");
    }

    @Test
    void editCost() {
        given(toolDao.findByIdValidated(TOOL_ID)).willReturn(tool);

        underTest.editCost(TOOL_ID, COST);

        then(tool).should().setCost(COST);
        then(toolDao).should().save(tool);
    }

    @Test
    void editAcquiredAt_null() {
        ExceptionValidator.validateInvalidParam(() -> underTest.editAcquiredAt(TOOL_ID, null), "acquiredAt", "must not be null");
    }

    @Test
    void editAcquiredAt() {
        given(toolDao.findByIdValidated(TOOL_ID)).willReturn(tool);

        underTest.editAcquiredAt(TOOL_ID, ACQUIRED_AT);

        then(tool).should().setAcquiredAt(ACQUIRED_AT);
        then(toolDao).should().save(tool);
    }

    @Test
    void editWarrantyExpiresAt() {
        given(toolDao.findByIdValidated(TOOL_ID)).willReturn(tool);

        underTest.editWarrantyExpiresAt(TOOL_ID, WARRANTY_EXPIRES_AT);

        then(tool).should().setWarrantyExpiresAt(WARRANTY_EXPIRES_AT);
        then(toolDao).should().save(tool);
    }

    @Test
    void editStatus_null() {
        ExceptionValidator.validateInvalidParam(() -> underTest.editStatus(TOOL_ID, null), "status", "must not be null");
    }

    @Test
    void editStatus() {
        given(toolDao.findByIdValidated(TOOL_ID)).willReturn(tool);

        underTest.editStatus(TOOL_ID, ToolStatus.DEFAULT);

        then(tool).should().setStatus(ToolStatus.DEFAULT);
        then(toolDao).should().save(tool);
    }

    @Test
    void editScrappedAt() {
        given(toolDao.findByIdValidated(TOOL_ID)).willReturn(tool);

        underTest.editScrappedAt(TOOL_ID, SCRAPPED_AT);

        then(tool).should().setScrappedAt(SCRAPPED_AT);
        then(toolDao).should().save(tool);
    }

    @Test
    void editInventoried_null() {
        ExceptionValidator.validateInvalidParam(() -> underTest.editInventoried(TOOL_ID, null), "inventoried", "must not be null");
    }

    @Test
    void editInventoried() {
        given(toolDao.findByIdValidated(TOOL_ID)).willReturn(tool);

        underTest.editInventoried(TOOL_ID, true);

        then(tool).should().setInventoried(true);
        then(toolDao).should().save(tool);
    }

    @Test
    void resetInventoried() {
        given(toolDao.getByUserId(USER_ID)).willReturn(List.of(tool));

        underTest.resetInventoried(USER_ID);

        then(tool).should().setInventoried(false);
        then(toolDao).should().saveAll(List.of(tool));
    }
}