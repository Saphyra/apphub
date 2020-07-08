package com.github.saphyra.apphub.service.notebook;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.notebook.service.ListItemDeletionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ListItemControllerIImplTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private ListItemDeletionService listItemDeletionService;

    @InjectMocks
    private ListItemControllerIImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Test
    public void deleteListItem() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        underTest.deleteListItem(LIST_ITEM_ID, accessTokenHeader);

        verify(listItemDeletionService).deleteListItem(LIST_ITEM_ID, USER_ID);
    }
}