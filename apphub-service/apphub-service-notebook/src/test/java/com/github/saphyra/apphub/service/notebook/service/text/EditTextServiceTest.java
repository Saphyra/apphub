package com.github.saphyra.apphub.service.notebook.service.text;

import com.github.saphyra.apphub.api.notebook.model.request.EditTextRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.text.Text;
import com.github.saphyra.apphub.service.notebook.dao.text.TextDao;
import com.github.saphyra.apphub.service.notebook.service.TitleValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EditTextServiceTest {
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final UUID TEXT_ID = UUID.randomUUID();

    @Mock
    private ContentValidator contentValidator;

    @Mock
    private TitleValidator titleValidator;

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private TextDao textDao;

    @InjectMocks
    private EditTextService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private Text text;

    @Test
    public void editText() {
        EditTextRequest request = EditTextRequest.builder()
            .title(TITLE)
            .content(CONTENT)
            .build();

        given(listItemDao.findByIdValidated(TEXT_ID)).willReturn(listItem);
        given(textDao.findByParentValidated(TEXT_ID)).willReturn(text);

        underTest.editText(TEXT_ID, request);

        verify(listItem).setTitle(TITLE);
        verify(text).setContent(CONTENT);
        verify(listItemDao).save(listItem);
        verify(textDao).save(text);
    }
}