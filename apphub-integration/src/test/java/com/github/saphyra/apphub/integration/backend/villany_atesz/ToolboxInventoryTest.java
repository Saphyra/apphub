package com.github.saphyra.apphub.integration.backend.villany_atesz;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszToolboxActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszToolboxInventoryActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.CustomAssertions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CreateToolRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.StorageBoxModel;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ToolResponse;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ToolStatus;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ToolTypeModel;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ToolboxInventoryTest extends BackEndTest {
    private static final String BRAND = "brand";
    private static final String NAME = "name";
    private static final Integer COST = 24;
    private static final LocalDate ACQUIRED_AT = LocalDate.now();
    private static final LocalDate WARRANTY_EXPIRES_AT = ACQUIRED_AT.plusDays(1);
    private static final String STORAGE_BOX_NAME = "storage-box-name";
    private static final String TOOL_TYPE_NAME = "tool-type-name";
    private static final String NEW_STORAGE_BOX_NAME = "new-storage-box-name";
    private static final String NEW_TOOL_TYPE_NAME = "new-tool-type-name";
    private static final String NEW_BRAND = "new-brand";
    private static final String NEW_NAME = "new-name";
    private static final Integer NEW_COST = 35;
    private static final LocalDate NEW_ACQUIRED_AT = WARRANTY_EXPIRES_AT.plusDays(1);
    private static final LocalDate NEW_WARRANTY_EXPIRES_AT = NEW_ACQUIRED_AT.plusDays(1);
    private static final LocalDate SCRAPPED_AT = NEW_WARRANTY_EXPIRES_AT.plusDays(1);

    @Test(groups = {"villany-atesz", "be"})
    public void toolboxInventory() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);

        UUID toolId = createTool(accessTokenId);

        UUID storageBoxId = VillanyAteszToolboxActions.getStorageBoxes(accessTokenId)
            .get(0)
            .getStorageBoxId();

        UUID toolTypeId = VillanyAteszToolboxActions.getToolTypes(accessTokenId)
            .get(0)
            .getToolTypeId();

        editStorageBox_nullName(accessTokenId, toolId);
        editStorageBox_storageBoxNotFound(accessTokenId, toolId);
        editStorageBox_newStorageBox(accessTokenId, toolId);
        editStorageBox_noStorageBox(accessTokenId, toolId);
        editStorageBox_existingStorageBox(accessTokenId, toolId, storageBoxId);

        editToolType_nullName(accessTokenId, toolId);
        editToolType_toolTypeNotFound(accessTokenId, toolId);
        editToolType_newToolType(accessTokenId, toolId);
        editToolType_noToolType(accessTokenId, toolId);
        editToolType_existingToolType(accessTokenId, toolId, toolTypeId);

        editBrand_null(accessTokenId, toolId);
        editBrand(accessTokenId, toolId);

        editName_blank(accessTokenId, toolId);
        editName(accessTokenId, toolId);

        editCost_null(accessTokenId, toolId);
        editCost(accessTokenId, toolId);

        editAcquiredAt_null(accessTokenId, toolId);
        editAcquiredAt(accessTokenId, toolId);

        editWarrantyExpiresAt(accessTokenId, toolId);

        editStatus_null(accessTokenId, toolId);
        editStatus(accessTokenId, toolId);

        editScrappedAt(accessTokenId, toolId);

        editInventoried_null(accessTokenId, toolId);
        editInventoried(accessTokenId, toolId);

        resetInventoried(accessTokenId);
    }

    private void resetInventoried(UUID accessTokenId) {
        CustomAssertions.singleListAssertThat(VillanyAteszToolboxInventoryActions.resetInventoried(accessTokenId))
            .returns(false, ToolResponse::getInventoried);
    }

    private void editInventoried(UUID accessTokenId, UUID toolId) {
        VillanyAteszToolboxInventoryActions.editInventoried(accessTokenId, toolId, true);

        CustomAssertions.singleListAssertThat(VillanyAteszToolboxActions.getTools(accessTokenId))
            .returns(true, ToolResponse::getInventoried);
    }

    private void editInventoried_null(UUID accessTokenId, UUID toolId) {
        ResponseValidator.verifyInvalidParam(VillanyAteszToolboxInventoryActions.getEditInventoriedResponse(accessTokenId, toolId, null), "inventoried", "must not be null");
    }

    private void editScrappedAt(UUID accessTokenId, UUID toolId) {
        VillanyAteszToolboxInventoryActions.editScrappedAt(accessTokenId, toolId, SCRAPPED_AT);

        CustomAssertions.singleListAssertThat(VillanyAteszToolboxActions.getTools(accessTokenId))
            .returns(SCRAPPED_AT, ToolResponse::getScrappedAt);
    }

    private void editStatus(UUID accessTokenId, UUID toolId) {
        VillanyAteszToolboxInventoryActions.editStatus(accessTokenId, toolId, ToolStatus.SCRAPPED);

        CustomAssertions.singleListAssertThat(VillanyAteszToolboxActions.getTools(accessTokenId))
            .returns(ToolStatus.SCRAPPED, ToolResponse::getStatus);
    }

    private void editStatus_null(UUID accessTokenId, UUID toolId) {
        ResponseValidator.verifyInvalidParam(VillanyAteszToolboxInventoryActions.getEditStatusResponse(accessTokenId, toolId, null), "status", "must not be null");
    }

    private void editWarrantyExpiresAt(UUID accessTokenId, UUID toolId) {
        VillanyAteszToolboxInventoryActions.editWarrantyExpiresAt(accessTokenId, toolId, NEW_WARRANTY_EXPIRES_AT);

        CustomAssertions.singleListAssertThat(VillanyAteszToolboxActions.getTools(accessTokenId))
            .returns(NEW_WARRANTY_EXPIRES_AT, ToolResponse::getWarrantyExpiresAt);
    }

    private void editAcquiredAt(UUID accessTokenId, UUID toolId) {
        VillanyAteszToolboxInventoryActions.editAcquiredAt(accessTokenId, toolId, NEW_ACQUIRED_AT);

        CustomAssertions.singleListAssertThat(VillanyAteszToolboxActions.getTools(accessTokenId))
            .returns(NEW_ACQUIRED_AT, ToolResponse::getAcquiredAt);
    }

    private void editAcquiredAt_null(UUID accessTokenId, UUID toolId) {
        ResponseValidator.verifyInvalidParam(VillanyAteszToolboxInventoryActions.getEditAcquiredAtResponse(accessTokenId, toolId, null), "acquiredAt", "must not be null");
    }

    private void editCost(UUID accessTokenId, UUID toolId) {
        VillanyAteszToolboxInventoryActions.editCost(accessTokenId, toolId, NEW_COST);

        CustomAssertions.singleListAssertThat(VillanyAteszToolboxActions.getTools(accessTokenId))
            .returns(NEW_COST, ToolResponse::getCost);
    }

    private void editCost_null(UUID accessTokenId, UUID toolId) {
        ResponseValidator.verifyInvalidParam(VillanyAteszToolboxInventoryActions.getEditCostResponse(accessTokenId, toolId, null), "cost", "must not be null");
    }

    private void editName(UUID accessTokenId, UUID toolId) {
        VillanyAteszToolboxInventoryActions.editName(accessTokenId, toolId, NEW_NAME);

        CustomAssertions.singleListAssertThat(VillanyAteszToolboxActions.getTools(accessTokenId))
            .returns(NEW_NAME, ToolResponse::getName);
    }

    private void editName_blank(UUID accessTokenId, UUID toolId) {
        ResponseValidator.verifyInvalidParam(VillanyAteszToolboxInventoryActions.getEditNameResponse(accessTokenId, toolId, " "), "name", "must not be null or blank");
    }

    private void editBrand(UUID accessTokenId, UUID toolId) {
        VillanyAteszToolboxInventoryActions.editBrand(accessTokenId, toolId, NEW_BRAND);

        CustomAssertions.singleListAssertThat(VillanyAteszToolboxActions.getTools(accessTokenId))
            .returns(NEW_BRAND, ToolResponse::getBrand);
    }

    private void editBrand_null(UUID accessTokenId, UUID toolId) {
        ResponseValidator.verifyInvalidParam(VillanyAteszToolboxInventoryActions.getEditBrandResponse(accessTokenId, toolId, null), "brand", "must not be null");
    }

    private void editToolType_existingToolType(UUID accessTokenId, UUID toolId, UUID toolTypeId) {
        ToolTypeModel toolTypeModel = ToolTypeModel.builder()
            .toolTypeId(toolTypeId)
            .build();

        ToolTypeModel existingToolType = VillanyAteszToolboxInventoryActions.editToolType(accessTokenId, toolId, toolTypeModel);

        assertThat(existingToolType.getName()).isEqualTo(TOOL_TYPE_NAME);

        CustomAssertions.singleListAssertThat(VillanyAteszToolboxActions.getTools(accessTokenId))
            .returns(existingToolType, ToolResponse::getToolType);
    }

    private void editToolType_noToolType(UUID accessTokenId, UUID toolId) {
        ToolTypeModel toolTypeModel = ToolTypeModel.builder()
            .name("")
            .build();

        assertThat(VillanyAteszToolboxInventoryActions.editToolType(accessTokenId, toolId, toolTypeModel)).isNull();

        CustomAssertions.singleListAssertThat(VillanyAteszToolboxActions.getTools(accessTokenId))
            .returns(null, ToolResponse::getToolType);
    }

    private void editToolType_newToolType(UUID accessTokenId, UUID toolId) {
        ToolTypeModel toolTypeModel = ToolTypeModel.builder()
            .name(NEW_TOOL_TYPE_NAME)
            .build();

        ToolTypeModel newToolType = VillanyAteszToolboxInventoryActions.editToolType(accessTokenId, toolId, toolTypeModel);

        assertThat(newToolType.getName()).isEqualTo(NEW_TOOL_TYPE_NAME);

        CustomAssertions.singleListAssertThat(VillanyAteszToolboxActions.getTools(accessTokenId))
            .returns(newToolType, ToolResponse::getToolType);
    }

    private void editToolType_toolTypeNotFound(UUID accessTokenId, UUID toolId) {
        ToolTypeModel toolTypeModel = ToolTypeModel.builder()
            .toolTypeId(UUID.randomUUID())
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszToolboxInventoryActions.getEditToolTypeResponse(accessTokenId, toolId, toolTypeModel), "toolTypeId", "not found");
    }

    private void editToolType_nullName(UUID accessTokenId, UUID toolId) {
        ToolTypeModel toolTypeModel = ToolTypeModel.builder()
            .name(null)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszToolboxInventoryActions.getEditToolTypeResponse(accessTokenId, toolId, toolTypeModel), "toolType.name", "must not be null");
    }

    private void editStorageBox_existingStorageBox(UUID accessTokenId, UUID toolId, UUID storageBoxId) {
        StorageBoxModel storageBoxModel = StorageBoxModel.builder()
            .storageBoxId(storageBoxId)
            .build();

        StorageBoxModel existingStorageBox = VillanyAteszToolboxInventoryActions.editStorageBox(accessTokenId, toolId, storageBoxModel);

        assertThat(existingStorageBox.getName()).isEqualTo(STORAGE_BOX_NAME);

        CustomAssertions.singleListAssertThat(VillanyAteszToolboxActions.getTools(accessTokenId))
            .returns(existingStorageBox, ToolResponse::getStorageBox);
    }

    private void editStorageBox_noStorageBox(UUID accessTokenId, UUID toolId) {
        StorageBoxModel storageBoxModel = StorageBoxModel.builder()
            .name("")
            .build();

        assertThat(VillanyAteszToolboxInventoryActions.editStorageBox(accessTokenId, toolId, storageBoxModel)).isNull();

        CustomAssertions.singleListAssertThat(VillanyAteszToolboxActions.getTools(accessTokenId))
            .returns(null, ToolResponse::getStorageBox);
    }

    private void editStorageBox_newStorageBox(UUID accessTokenId, UUID toolId) {
        StorageBoxModel storageBoxModel = StorageBoxModel.builder()
            .name(NEW_STORAGE_BOX_NAME)
            .build();

        StorageBoxModel newStorageBox = VillanyAteszToolboxInventoryActions.editStorageBox(accessTokenId, toolId, storageBoxModel);
        assertThat(newStorageBox.getName()).isEqualTo(NEW_STORAGE_BOX_NAME);

        CustomAssertions.singleListAssertThat(VillanyAteszToolboxActions.getTools(accessTokenId))
            .returns(newStorageBox, ToolResponse::getStorageBox);
    }

    private void editStorageBox_storageBoxNotFound(UUID accessTokenId, UUID toolId) {
        StorageBoxModel storageBoxModel = StorageBoxModel.builder()
            .storageBoxId(UUID.randomUUID())
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszToolboxInventoryActions.getEditStorageBoxResponse(accessTokenId, toolId, storageBoxModel), "storageBoxId", "not found");
    }

    private void editStorageBox_nullName(UUID accessTokenId, UUID toolId) {
        StorageBoxModel storageBoxModel = StorageBoxModel.builder()
            .name(null)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszToolboxInventoryActions.getEditStorageBoxResponse(accessTokenId, toolId, storageBoxModel), "storageBox.name", "must not be null");
    }

    private UUID createTool(UUID accessTokenId) {
        CreateToolRequest request = CreateToolRequest.builder()
            .brand(BRAND)
            .name(NAME)
            .cost(COST)
            .acquiredAt(ACQUIRED_AT)
            .warrantyExpiresAt(WARRANTY_EXPIRES_AT)
            .storageBox(StorageBoxModel.builder().name(STORAGE_BOX_NAME).build())
            .toolType(ToolTypeModel.builder().name(TOOL_TYPE_NAME).build())
            .build();
        VillanyAteszToolboxActions.createTool(accessTokenId, request);

        return VillanyAteszToolboxActions.getTools(accessTokenId)
            .get(0)
            .getToolId();
    }
}
