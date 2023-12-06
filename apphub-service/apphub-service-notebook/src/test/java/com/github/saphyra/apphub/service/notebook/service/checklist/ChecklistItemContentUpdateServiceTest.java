package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ChecklistItemContentUpdateServiceTest {
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final String CONTENT = "content";

    @Mock
    private ContentDao contentDao;

    @InjectMocks
    private ChecklistItemContentUpdateService underTest;

    @Mock
    private Content content;

    @Test
    void nullContent() {
        Throwable ex = catchThrowable(() -> underTest.updateContent(CHECKLIST_ITEM_ID, null));

        ExceptionValidator.validateInvalidParam(ex, "content", "must not be null");
    }

    @Test
    void updateContent() {
        given(contentDao.findByParentValidated(CHECKLIST_ITEM_ID)).willReturn(content);

        underTest.updateContent(CHECKLIST_ITEM_ID, CONTENT);

        then(content).should().setContent(CONTENT);
        then(contentDao).should().save(content);
    }
}