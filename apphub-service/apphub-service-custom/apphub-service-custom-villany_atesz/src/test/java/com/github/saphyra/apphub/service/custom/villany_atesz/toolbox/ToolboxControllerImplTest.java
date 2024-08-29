package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CreateToolRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StorageBoxModel;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolStatus;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolTypeModel;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service.CreateToolService;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service.DeleteToolService;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service.SetToolStatusService;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service.StorageBoxService;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service.ToolQueryService;
import com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.service.ToolTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ToolboxControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID TOOL_ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final UUID TOOL_TYPE_ID = UUID.randomUUID();
    private static final UUID STORAGE_BOX_ID = UUID.randomUUID();

    @Mock
    private ToolQueryService toolQueryService;

    @Mock
    private CreateToolService createToolService;

    @Mock
    private SetToolStatusService setToolStatusService;

    @Mock
    private DeleteToolService deleteToolService;

    @Mock
    private ToolTypeService toolTypeService;

    @Mock
    private StorageBoxService storageBoxService;

    @InjectMocks
    private ToolboxControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private ToolResponse toolResponse;

    @Mock
    private CreateToolRequest createToolRequest;

    @Mock
    private ToolTypeModel toolTypeModel;

    @Mock
    private StorageBoxModel storageBoxModel;

    @BeforeEach
    void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    void getTools() {
        given(toolQueryService.getTools(USER_ID)).willReturn(List.of(toolResponse));

        assertThat(underTest.getTools(accessTokenHeader)).containsExactly(toolResponse);
    }

    @Test
    void createTool() {
        underTest.createTool(createToolRequest, accessTokenHeader);

        then(createToolService).should().create(USER_ID, createToolRequest);
    }

    @Test
    void setToolStatus() {
        given(toolQueryService.getTools(USER_ID)).willReturn(List.of(toolResponse));

        assertThat(underTest.setToolStatus(new OneParamRequest<>(ToolStatus.DAMAGED), TOOL_ID, accessTokenHeader)).containsExactly(toolResponse);

        then(setToolStatusService).should().setStatus(TOOL_ID, ToolStatus.DAMAGED);
    }

    @Test
    void deleteTool() {
        given(toolQueryService.getTools(USER_ID)).willReturn(List.of(toolResponse));

        assertThat(underTest.deleteTool(TOOL_ID, accessTokenHeader)).containsExactly(toolResponse);

        then(deleteToolService).should().deleteTool(USER_ID, TOOL_ID);
    }

    @Test
    void getToolTypes() {
        given(toolQueryService.getToolTypes(USER_ID)).willReturn(List.of(toolTypeModel));

        assertThat(underTest.getToolTypes(accessTokenHeader))
            .containsExactly(toolTypeModel);
    }

    @Test
    void getStorageBoxes() {
        given(toolQueryService.getStorageBoxes(USER_ID)).willReturn(List.of(storageBoxModel));

        assertThat(underTest.getStorageBoxes(accessTokenHeader))
            .containsExactly(storageBoxModel);
    }

    @Test
    void editToolType() {
        underTest.editToolType(new OneParamRequest<>(NAME), TOOL_TYPE_ID, accessTokenHeader);

        then(toolTypeService).should().edit(TOOL_TYPE_ID, NAME);
    }

    @Test
    void deleteToolType() {
        underTest.deleteToolType(TOOL_TYPE_ID, accessTokenHeader);

        then(toolTypeService).should().delete(USER_ID, TOOL_TYPE_ID);
    }

    @Test
    void editStorageBox() {
        underTest.editStorageBox(new OneParamRequest<>(NAME), STORAGE_BOX_ID, accessTokenHeader);

        then(storageBoxService).should().edit(STORAGE_BOX_ID, NAME);
    }

    @Test
    void deleteStorageBox() {
        underTest.deleteStorageBox(STORAGE_BOX_ID, accessTokenHeader);

        then(storageBoxService).should().delete(USER_ID, STORAGE_BOX_ID);
    }
}