package com.github.saphyra.apphub.service.notebook.service.text;

import com.github.saphyra.apphub.api.feature.notebook.model.response.TextResponse;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TextQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final String DATA = "text-data";

    @Mock
    private ListItemDao listItemDao;

    @InjectMocks
    private TextQueryService underTest;

    @Mock
    private ListItem listItem;

    @Test
    void getTextResponse() {
        given(listItemDao.findByIdValidated(USER_ID, LIST_ITEM_ID)).willReturn(listItem);
        given(listItem.getParent()).willReturn(PARENT);
        given(listItem.getTitle()).willReturn(TITLE);
        given(listItem.getData()).willReturn(DATA);

        TextResponse result = underTest.getTextResponse(USER_ID, LIST_ITEM_ID);

        assertThat(result.getTextId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getContent()).isEqualTo(DATA);
    }
}

