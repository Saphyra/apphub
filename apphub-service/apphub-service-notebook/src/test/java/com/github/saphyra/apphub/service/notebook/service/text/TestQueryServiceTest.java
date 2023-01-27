package com.github.saphyra.apphub.service.notebook.service.text;

import com.github.saphyra.apphub.api.notebook.model.response.TextResponse;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TestQueryServiceTest {
    private static final UUID TEXT_ID = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final String CONTENT = "content";

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private ContentDao contentDao;

    @InjectMocks
    private TextQueryService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private Content content;

    @Test
    public void getTextResponse() {
        given(listItemDao.findByIdValidated(TEXT_ID)).willReturn(listItem);
        given(contentDao.findByParentValidated(TEXT_ID)).willReturn(content);
        given(listItem.getTitle()).willReturn(TITLE);
        given(content.getContent()).willReturn(CONTENT);

        TextResponse result = underTest.getTextResponse(TEXT_ID);

        assertThat(result.getTextId()).isEqualTo(TEXT_ID);
        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getContent()).isEqualTo(CONTENT);
    }
}