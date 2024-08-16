package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StorageBoxModel;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolStatus;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolTypeModel;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service.ToolInventoryService;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service.ToolQueryService;
import org.junit.jupiter.api.BeforeEach;
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
class ToolboxInventoryControllerImplTest {
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
    private ToolInventoryService toolInventoryService;

    @Mock
    private ToolQueryService toolQueryService;

    @InjectMocks
    private ToolboxInventoryControllerImpl underTest;

    @Mock
    private StorageBoxModel storageBoxModel;

    @Mock
    private ToolTypeModel toolTypeModel;

    @Mock
    private ToolResponse toolResponse;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @BeforeEach
    void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    void editStorageBox() {
        given(toolInventoryService.editStorageBox(USER_ID, TOOL_ID, storageBoxModel)).willReturn(STORAGE_BOX_ID);
        given(toolQueryService.getStorageBox(STORAGE_BOX_ID)).willReturn(storageBoxModel);

        assertThat(underTest.editStorageBox(storageBoxModel, TOOL_ID, accessTokenHeader)).isEqualTo(storageBoxModel);
    }

    @Test
    void editToolType() {
        given(toolInventoryService.editToolType(USER_ID, TOOL_ID, toolTypeModel)).willReturn(TOOL_TYPE_ID);
        given(toolQueryService.getToolType(TOOL_TYPE_ID)).willReturn(toolTypeModel);

        assertThat(underTest.editToolType(toolTypeModel, TOOL_ID, accessTokenHeader)).isEqualTo(toolTypeModel);
    }

    @Test
    void editBrand() {
        underTest.editBrand(new OneParamRequest<>(BRAND), TOOL_ID, accessTokenHeader);

        then(toolInventoryService).should().editBrand(TOOL_ID, BRAND);
    }

    @Test
    void editName() {
        underTest.editName(new OneParamRequest<>(NAME), TOOL_ID, accessTokenHeader);

        then(toolInventoryService).should().editName(TOOL_ID, NAME);
    }

    @Test
    void editCost() {
        underTest.editCost(new OneParamRequest<>(COST), TOOL_ID, accessTokenHeader);

        then(toolInventoryService).should().editCost(TOOL_ID, COST);
    }

    @Test
    void editAcquiredAt() {
        underTest.editAcquiredAt(new OneParamRequest<>(ACQUIRED_AT), TOOL_ID, accessTokenHeader);

        then(toolInventoryService).should().editAcquiredAt(TOOL_ID, ACQUIRED_AT);
    }

    @Test
    void editWarrantyExpiresAt() {
        underTest.editWarrantyExpiresAt(new OneParamRequest<>(WARRANTY_EXPIRES_AT), TOOL_ID, accessTokenHeader);

        then(toolInventoryService).should().editWarrantyExpiresAt(TOOL_ID, WARRANTY_EXPIRES_AT);
    }

    @Test
    void editStatus() {
        underTest.editStatus(new OneParamRequest<>(ToolStatus.DAMAGED), TOOL_ID, accessTokenHeader);

        then(toolInventoryService).should().editStatus(TOOL_ID, ToolStatus.DAMAGED);
    }

    @Test
    void editScrappedAt() {
        underTest.editScrappedAt(new OneParamRequest<>(SCRAPPED_AT), TOOL_ID, accessTokenHeader);

        then(toolInventoryService).should().editScrappedAt(TOOL_ID, SCRAPPED_AT);
    }

    @Test
    void editInventoried() {
        underTest.editInventoried(new OneParamRequest<>(true), TOOL_ID, accessTokenHeader);

        then(toolInventoryService).should().editInventoried(TOOL_ID, true);
    }

    @Test
    void resetInventoried() {
        given(toolQueryService.getTools(USER_ID)).willReturn(List.of(toolResponse));

        assertThat(underTest.resetInventoried(accessTokenHeader)).containsExactly(toolResponse);

        then(toolInventoryService).should().resetInventoried(USER_ID);
    }
}