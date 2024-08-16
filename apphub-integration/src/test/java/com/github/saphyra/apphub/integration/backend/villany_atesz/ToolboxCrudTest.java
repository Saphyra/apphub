package com.github.saphyra.apphub.integration.backend.villany_atesz;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszToolboxActions;
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

public class ToolboxCrudTest extends BackEndTest {
    private static final String NAME = "name";
    private static final Integer COST = 35;
    private static final LocalDate ACQUIRED_AT = LocalDate.now().minusDays(3);
    private static final String BRAND = "brand";
    private static final LocalDate WARRANTY_EXPIRES_AT = LocalDate.now().plusDays(4);
    private static final String STORAGE_BOX_NAME = "storage-box-name";
    private static final String TOOL_TYPE_NAME = "tool-type-name";

    @Test(groups = {"be", "villany-atesz"})
    public void toolboxCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);

        create_nullBrand(accessTokenId);
        create_blankName(accessTokenId);
        create_nullCost(accessTokenId);
        create_nullAcquiredAt(accessTokenId);
        create_nullStorageBox(accessTokenId);
        create_nullStorageBoxIdAndStorageBoxName(accessTokenId);
        create_storageBoxNotFound(accessTokenId);
        create_nullToolType(accessTokenId);
        create_nullToolTypeIdAndToolTypeName(accessTokenId);
        create_toolTypeNotFound(accessTokenId);
        ToolResponse toolResponse = create(accessTokenId);

        setStatus_null(accessTokenId, toolResponse.getToolId());
        setStatus(accessTokenId, toolResponse.getToolId());

        delete(accessTokenId, toolResponse.getToolId());

        create_existingStorageBoxAndToolType(accessTokenId, toolResponse.getStorageBox().getStorageBoxId(), toolResponse.getToolType().getToolTypeId());
    }

    private void create_existingStorageBoxAndToolType(UUID accessTokenId, UUID storageBoxId, UUID toolTypeId) {
        CreateToolRequest request = CreateToolRequest.builder()
            .brand(BRAND)
            .name(NAME)
            .cost(COST)
            .acquiredAt(ACQUIRED_AT)
            .warrantyExpiresAt(WARRANTY_EXPIRES_AT)
            .storageBox(StorageBoxModel.builder().storageBoxId(storageBoxId).build())
            .toolType(ToolTypeModel.builder().toolTypeId(toolTypeId).build())
            .build();
        VillanyAteszToolboxActions.createTool(accessTokenId, request);

        CustomAssertions.singleListAssertThat(VillanyAteszToolboxActions.getTools(accessTokenId))
            .returns(BRAND, ToolResponse::getBrand)
            .returns(NAME, ToolResponse::getName)
            .returns(COST, ToolResponse::getCost)
            .returns(ACQUIRED_AT, ToolResponse::getAcquiredAt)
            .returns(WARRANTY_EXPIRES_AT, ToolResponse::getWarrantyExpiresAt)
            .returns(ToolStatus.DEFAULT, ToolResponse::getStatus)
            .returns(null, ToolResponse::getScrappedAt)
            .returns(STORAGE_BOX_NAME, tr -> tr.getStorageBox().getName())
            .returns(TOOL_TYPE_NAME, tr -> tr.getToolType().getName());
    }

    private void create_toolTypeNotFound(UUID accessTokenId) {
        CreateToolRequest request = CreateToolRequest.builder()
            .brand(BRAND)
            .name(NAME)
            .cost(COST)
            .acquiredAt(ACQUIRED_AT)
            .warrantyExpiresAt(WARRANTY_EXPIRES_AT)
            .storageBox(StorageBoxModel.builder().name(STORAGE_BOX_NAME).build())
            .toolType(ToolTypeModel.builder().toolTypeId(UUID.randomUUID()).build())
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszToolboxActions.getCreateToolResponse(accessTokenId, request), "toolTypeId", "not found");
    }

    private void create_nullToolTypeIdAndToolTypeName(UUID accessTokenId) {
        CreateToolRequest request = CreateToolRequest.builder()
            .brand(BRAND)
            .name(NAME)
            .cost(COST)
            .acquiredAt(ACQUIRED_AT)
            .warrantyExpiresAt(WARRANTY_EXPIRES_AT)
            .storageBox(StorageBoxModel.builder().name(STORAGE_BOX_NAME).build())
            .toolType(ToolTypeModel.builder().build())
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszToolboxActions.getCreateToolResponse(accessTokenId, request), "toolType.name", "must not be null");
    }

    private void create_nullToolType(UUID accessTokenId) {
        CreateToolRequest request = CreateToolRequest.builder()
            .brand(BRAND)
            .name(NAME)
            .cost(COST)
            .acquiredAt(ACQUIRED_AT)
            .warrantyExpiresAt(WARRANTY_EXPIRES_AT)
            .storageBox(StorageBoxModel.builder().name(STORAGE_BOX_NAME).build())
            .toolType(null)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszToolboxActions.getCreateToolResponse(accessTokenId, request), "toolType", "must not be null");
    }

    private void create_storageBoxNotFound(UUID accessTokenId) {
        CreateToolRequest request = CreateToolRequest.builder()
            .brand(BRAND)
            .name(NAME)
            .cost(COST)
            .acquiredAt(ACQUIRED_AT)
            .warrantyExpiresAt(WARRANTY_EXPIRES_AT)
            .storageBox(StorageBoxModel.builder().storageBoxId(UUID.randomUUID()).build())
            .toolType(ToolTypeModel.builder().name(TOOL_TYPE_NAME).build())
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszToolboxActions.getCreateToolResponse(accessTokenId, request), "storageBoxId", "not found");
    }

    private void create_nullStorageBox(UUID accessTokenId) {
        CreateToolRequest request = CreateToolRequest.builder()
            .brand(BRAND)
            .name(NAME)
            .cost(COST)
            .acquiredAt(ACQUIRED_AT)
            .warrantyExpiresAt(WARRANTY_EXPIRES_AT)
            .storageBox(null)
            .toolType(ToolTypeModel.builder().name(TOOL_TYPE_NAME).build())
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszToolboxActions.getCreateToolResponse(accessTokenId, request), "storageBox", "must not be null");
    }

    private void create_nullStorageBoxIdAndStorageBoxName(UUID accessTokenId) {
        CreateToolRequest request = CreateToolRequest.builder()
            .brand(BRAND)
            .name(NAME)
            .cost(COST)
            .acquiredAt(ACQUIRED_AT)
            .warrantyExpiresAt(WARRANTY_EXPIRES_AT)
            .storageBox(StorageBoxModel.builder().build())
            .toolType(ToolTypeModel.builder().name(TOOL_TYPE_NAME).build())
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszToolboxActions.getCreateToolResponse(accessTokenId, request), "storageBox.name", "must not be null");
    }

    private void delete(UUID accessTokenId, UUID toolId) {
        assertThat(VillanyAteszToolboxActions.delete(accessTokenId, toolId)).isEmpty();
    }

    private void setStatus(UUID accessTokenId, UUID toolId) {
        CustomAssertions.singleListAssertThat(VillanyAteszToolboxActions.setToolStatus(accessTokenId, toolId, ToolStatus.DAMAGED))
            .returns(ToolStatus.DAMAGED, ToolResponse::getStatus)
            .returns(null, ToolResponse::getScrappedAt);

        CustomAssertions.singleListAssertThat(VillanyAteszToolboxActions.setToolStatus(accessTokenId, toolId, ToolStatus.SCRAPPED))
            .returns(ToolStatus.SCRAPPED, ToolResponse::getStatus)
            .returns(LocalDate.now(), ToolResponse::getScrappedAt);
    }

    private void setStatus_null(UUID accessTokenId, UUID toolId) {
        ResponseValidator.verifyInvalidParam(VillanyAteszToolboxActions.getSetToolStatusResponse(accessTokenId, toolId, null), "status", "must not be null");
    }

    private ToolResponse create(UUID accessTokenId) {
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

        ToolResponse toolResponse = VillanyAteszToolboxActions.getTools(accessTokenId)
            .get(0);

        assertThat(toolResponse)
            .returns(BRAND, ToolResponse::getBrand)
            .returns(NAME, ToolResponse::getName)
            .returns(COST, ToolResponse::getCost)
            .returns(ACQUIRED_AT, ToolResponse::getAcquiredAt)
            .returns(WARRANTY_EXPIRES_AT, ToolResponse::getWarrantyExpiresAt)
            .returns(ToolStatus.DEFAULT, ToolResponse::getStatus)
            .returns(null, ToolResponse::getScrappedAt)
            .returns(STORAGE_BOX_NAME, tr -> tr.getStorageBox().getName())
            .returns(TOOL_TYPE_NAME, tr -> tr.getToolType().getName());

        return toolResponse;
    }

    private void create_nullAcquiredAt(UUID accessTokenId) {
        CreateToolRequest request = CreateToolRequest.builder()
            .brand(BRAND)
            .name(NAME)
            .cost(COST)
            .acquiredAt(null)
            .storageBox(StorageBoxModel.builder().name(STORAGE_BOX_NAME).build())
            .toolType(ToolTypeModel.builder().name(TOOL_TYPE_NAME).build())
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszToolboxActions.getCreateToolResponse(accessTokenId, request), "acquiredAt", "must not be null");
    }

    private void create_nullCost(UUID accessTokenId) {
        CreateToolRequest request = CreateToolRequest.builder()
            .brand(BRAND)
            .name(NAME)
            .cost(null)
            .acquiredAt(ACQUIRED_AT)
            .storageBox(StorageBoxModel.builder().name(STORAGE_BOX_NAME).build())
            .toolType(ToolTypeModel.builder().name(TOOL_TYPE_NAME).build())
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszToolboxActions.getCreateToolResponse(accessTokenId, request), "cost", "must not be null");
    }

    private void create_blankName(UUID accessTokenId) {
        CreateToolRequest request = CreateToolRequest.builder()
            .brand(BRAND)
            .name(" ")
            .cost(COST)
            .acquiredAt(ACQUIRED_AT)
            .storageBox(StorageBoxModel.builder().name(STORAGE_BOX_NAME).build())
            .toolType(ToolTypeModel.builder().name(TOOL_TYPE_NAME).build())
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszToolboxActions.getCreateToolResponse(accessTokenId, request), "name", "must not be null or blank");
    }

    private void create_nullBrand(UUID accessTokenId) {
        CreateToolRequest request = CreateToolRequest.builder()
            .brand(null)
            .name(NAME)
            .cost(COST)
            .acquiredAt(ACQUIRED_AT)
            .storageBox(StorageBoxModel.builder().name(STORAGE_BOX_NAME).build())
            .toolType(ToolTypeModel.builder().name(TOOL_TYPE_NAME).build())
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszToolboxActions.getCreateToolResponse(accessTokenId, request), "brand", "must not be null");
    }
}
