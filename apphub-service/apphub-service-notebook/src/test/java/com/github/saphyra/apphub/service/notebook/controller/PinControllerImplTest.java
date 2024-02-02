package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.notebook.service.pin.PinService;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PinControllerImplTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PIN_GROUP_ID = UUID.randomUUID();

    @Mock
    private PinService pinService;

    @InjectMocks
    private PinControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private NotebookView notebookView;

    @Test
    public void pinListItem() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        underTest.pinListItem(LIST_ITEM_ID, new OneParamRequest<>(true), accessTokenHeader);

        verify(pinService).pinListItem(LIST_ITEM_ID, true);
    }

    @Test
    public void getPinnedItems() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(pinService.getPinnedItems(USER_ID, PIN_GROUP_ID)).willReturn(Arrays.asList(notebookView));

        List<NotebookView> result = underTest.getPinnedItems(PIN_GROUP_ID, accessTokenHeader);

        assertThat(result).containsExactly(notebookView);
    }
}