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
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ToolResponse;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ToolStatus;
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

    @Test(groups = {"be", "villany-atesz"})
    public void toolboxCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);

        create_nullBrand(accessTokenId);
        create_blankName(accessTokenId);
        create_nullCost(accessTokenId);
        create_nullAcquiredAt(accessTokenId);
        UUID toolId = create(accessTokenId);

        setStatus_null(accessTokenId, toolId);
        setStatus(accessTokenId, toolId);

        delete(accessTokenId, toolId);
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

    private UUID create(UUID accessTokenId) {
        CreateToolRequest request = CreateToolRequest.builder()
            .brand(BRAND)
            .name(NAME)
            .cost(COST)
            .acquiredAt(ACQUIRED_AT)
            .warrantyExpiresAt(WARRANTY_EXPIRES_AT)
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
            .returns(null, ToolResponse::getScrappedAt);

        return toolResponse.getToolId();
    }

    private void create_nullAcquiredAt(UUID accessTokenId) {
        CreateToolRequest request = CreateToolRequest.builder()
            .brand(BRAND)
            .name(NAME)
            .cost(COST)
            .acquiredAt(null)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszToolboxActions.getCreateToolResponse(accessTokenId, request), "acquiredAt", "must not be null");
    }

    private void create_nullCost(UUID accessTokenId) {
        CreateToolRequest request = CreateToolRequest.builder()
            .brand(BRAND)
            .name(NAME)
            .cost(null)
            .acquiredAt(ACQUIRED_AT)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszToolboxActions.getCreateToolResponse(accessTokenId, request), "cost", "must not be null");
    }

    private void create_blankName(UUID accessTokenId) {
        CreateToolRequest request = CreateToolRequest.builder()
            .brand(BRAND)
            .name(" ")
            .cost(COST)
            .acquiredAt(ACQUIRED_AT)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszToolboxActions.getCreateToolResponse(accessTokenId, request), "name", "must not be null or blank");
    }

    private void create_nullBrand(UUID accessTokenId) {
        CreateToolRequest request = CreateToolRequest.builder()
            .brand(null)
            .name(NAME)
            .cost(COST)
            .acquiredAt(ACQUIRED_AT)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszToolboxActions.getCreateToolResponse(accessTokenId, request), "brand", "must not be null");
    }
}
