package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.pin.PinGroupResponse;
import com.github.saphyra.apphub.api.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.notebook.service.pin.PinService;
import com.github.saphyra.apphub.service.notebook.service.pin.group.PinGroupCreationService;
import com.github.saphyra.apphub.service.notebook.service.pin.group.PinGroupDeletionService;
import com.github.saphyra.apphub.service.notebook.service.pin.group.PinGroupItemService;
import com.github.saphyra.apphub.service.notebook.service.pin.group.PinGroupQueryService;
import com.github.saphyra.apphub.service.notebook.service.pin.group.PinGroupRenameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PinControllerImplTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PIN_GROUP_ID = UUID.randomUUID();
    private static final String PIN_GROUP_NAME = "pin-group-name";

    @Mock
    private PinService pinService;

    @Mock
    private PinGroupQueryService pinGroupQueryService;

    @Mock
    private PinGroupCreationService pinGroupCreationService;

    @Mock
    private PinGroupRenameService pinGroupRenameService;

    @Mock
    private PinGroupDeletionService pinGroupDeletionService;

    @Mock
    private PinGroupItemService pinGroupItemService;

    @InjectMocks
    private PinControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private NotebookView notebookView;

    @Mock
    private PinGroupResponse pinGroupResponse;

    @BeforeEach
    void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void pinListItem() {
        underTest.pinListItem(LIST_ITEM_ID, new OneParamRequest<>(true), accessTokenHeader);

        verify(pinService).pinListItem(LIST_ITEM_ID, true);
    }

    @Test
    public void getPinnedItems() {
        given(pinService.getPinnedItems(USER_ID, PIN_GROUP_ID)).willReturn(Arrays.asList(notebookView));

        List<NotebookView> result = underTest.getPinnedItems(PIN_GROUP_ID, accessTokenHeader);

        assertThat(result).containsExactly(notebookView);
    }

    @Test
    void createPinGroup() {
        given(pinGroupQueryService.getPinGroups(USER_ID)).willReturn(List.of(pinGroupResponse));

        assertThat(underTest.createPinGroup(new OneParamRequest<>(PIN_GROUP_NAME), accessTokenHeader)).containsExactly(pinGroupResponse);

        then(pinGroupCreationService).should().create(USER_ID, PIN_GROUP_NAME);
    }

    @Test
    void getPinGroups() {
        given(pinGroupQueryService.getPinGroups(USER_ID)).willReturn(List.of(pinGroupResponse));

        assertThat(underTest.getPinGroups(accessTokenHeader)).containsExactly(pinGroupResponse);
    }

    @Test
    void renamePinGroup() {
        given(pinGroupQueryService.getPinGroups(USER_ID)).willReturn(List.of(pinGroupResponse));

        assertThat(underTest.renamePinGroup(new OneParamRequest<>(PIN_GROUP_NAME), PIN_GROUP_ID, accessTokenHeader)).containsExactly(pinGroupResponse);

        then(pinGroupRenameService).should().rename(PIN_GROUP_ID, PIN_GROUP_NAME);
    }

    @Test
    void deletePinGroup() {
        given(pinGroupQueryService.getPinGroups(USER_ID)).willReturn(List.of(pinGroupResponse));

        assertThat(underTest.deletePinGroup(PIN_GROUP_ID, accessTokenHeader)).containsExactly(pinGroupResponse);

        then(pinGroupDeletionService).should().delete(PIN_GROUP_ID);
    }

    @Test
    void addItemToPinGroup() {
        given(pinService.getPinnedItems(USER_ID, PIN_GROUP_ID)).willReturn(Arrays.asList(notebookView));

        assertThat(underTest.addItemToPinGroup(PIN_GROUP_ID, LIST_ITEM_ID, accessTokenHeader)).containsExactly(notebookView);

        then(pinGroupItemService).should().addItem(USER_ID, PIN_GROUP_ID, LIST_ITEM_ID);
    }

    @Test
    void removeItemFromPinGroup() {
        given(pinService.getPinnedItems(USER_ID, PIN_GROUP_ID)).willReturn(Arrays.asList(notebookView));

        assertThat(underTest.removeItemFromPinGroup(PIN_GROUP_ID, LIST_ITEM_ID, accessTokenHeader)).containsExactly(notebookView);

        then(pinGroupItemService).should().removeItem(USER_ID, PIN_GROUP_ID, LIST_ITEM_ID);
    }
}