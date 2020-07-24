package com.github.saphyra.apphub.service.notebook.service.text;

import com.github.saphyra.apphub.api.notebook.model.response.TextResponse;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.text.Text;
import com.github.saphyra.apphub.service.notebook.dao.text.TextDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class TextQueryServiceTest {
    private static final UUID TEXT_ID = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final String CONTENT = "content";

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private TextDao textDao;

    @InjectMocks
    private TextQueryService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private Text text;

    @Test
    public void getTextResponse() {
        given(listItemDao.findByIdValidated(TEXT_ID)).willReturn(listItem);
        given(textDao.findByParentValidated(TEXT_ID)).willReturn(text);
        given(listItem.getTitle()).willReturn(TITLE);
        given(text.getContent()).willReturn(CONTENT);

        TextResponse result = underTest.getTextResponse(TEXT_ID);

        assertThat(result.getTextId()).isEqualTo(TEXT_ID);
        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getContent()).isEqualTo(CONTENT);
    }
}