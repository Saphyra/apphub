package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TextAndLinkCloneServiceTest {
    private static final UUID ORIGINAL_PARENT = UUID.randomUUID();
    private static final String CONTENT = "content";

    @Mock
    private ContentDao contentDao;

    @Mock
    private ContentFactory contentFactory;

    @InjectMocks
    private TextAndLinkCloneService underTest;

    @Mock
    private Content originalContent;

    @Mock
    private Content clonedContent;

    @Mock
    private ListItem listItem;

    @Test
    public void cloneTest() {
        given(contentDao.findByParentValidated(ORIGINAL_PARENT)).willReturn(originalContent);
        given(originalContent.getContent()).willReturn(CONTENT);
        given(contentFactory.create(listItem, CONTENT)).willReturn(clonedContent);

        underTest.clone(ORIGINAL_PARENT, listItem);

        verify(contentDao).save(clonedContent);
    }

    ;
}