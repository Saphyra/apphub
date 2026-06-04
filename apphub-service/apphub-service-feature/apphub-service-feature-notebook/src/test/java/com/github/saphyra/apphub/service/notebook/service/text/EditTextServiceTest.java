package com.github.saphyra.apphub.service.notebook.service.text;

import com.github.saphyra.apphub.api.feature.notebook.model.request.EditTextRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.validator.TextValidator;
import com.github.saphyra.apphub.service.notebook.service.validator.TitleValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EditTextServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final String CONTENT = "content";

    @Mock
    private TextValidator textValidator;

    @Mock
    private TitleValidator titleValidator;

    @Mock
    private ListItemDao listItemDao;

    @InjectMocks
    private EditTextService underTest;

    @Mock
    private ListItem listItem;

    @Test
    void editText() {
        EditTextRequest request = EditTextRequest.builder()
            .title(TITLE)
            .content(CONTENT)
            .build();

        given(listItemDao.findByIdValidated(USER_ID, LIST_ITEM_ID)).willReturn(listItem);

        underTest.editText(USER_ID, LIST_ITEM_ID, request);

        then(titleValidator).should().validate(TITLE);
        then(textValidator).should().validate(CONTENT, "content");
        then(listItem).should().setTitle(TITLE);
        then(listItem).should().setData(CONTENT);
        then(listItemDao).should().save(listItem);
    }
}

