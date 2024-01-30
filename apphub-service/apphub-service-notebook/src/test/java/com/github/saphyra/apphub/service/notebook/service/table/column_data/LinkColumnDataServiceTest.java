package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.service.table.dto.Link;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LinkColumnDataServiceTest {
    private static final String URL = "url";
    private static final String LABEL = "label";
    private static final String DATA = "data";
    private static final String STRINGIFIED_DATA = "stringified-data";
    private static final UUID COLUMN_ID = UUID.randomUUID();

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private ContentDao contentDao;

    @InjectMocks
    private LinkColumnDataService underTest;

    @Mock
    private Content content;

    @Test
    void stringifyContent() {
        given(objectMapperWrapper.writeValueAsString(DATA)).willReturn(STRINGIFIED_DATA);

        assertThat(underTest.stringifyContent(DATA)).isEqualTo(STRINGIFIED_DATA);
    }

    @Test
    void validateData_null() {
        Throwable ex = catchThrowable(() -> underTest.validateData(null));

        ExceptionValidator.validateInvalidParam(ex, "link", "must not be null");
    }

    @Test
    void getData() {
        given(contentDao.findByParentValidated(COLUMN_ID)).willReturn(content);
        given(content.getContent()).willReturn(DATA);
        Link link = new Link();
        given(objectMapperWrapper.readValue(DATA, Link.class)).willReturn(link);

        assertThat(underTest.getData(COLUMN_ID)).isEqualTo(link);
    }

    @Test
    void validateData_blankLabel() {
        Link link = Link.builder()
            .url(URL)
            .label(" ")
            .build();
        given(objectMapperWrapper.convertValue(DATA, Link.class)).willReturn(link);

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "link.label", "must not be null or blank");
    }

    @Test
    void validateData_nullUrl() {
        Link link = Link.builder()
            .url(null)
            .label(LABEL)
            .build();
        given(objectMapperWrapper.convertValue(DATA, Link.class)).willReturn(link);

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "link.url", "must not be null");
    }

    @Test
    void validateData() {
        Link link = Link.builder()
            .url(URL)
            .label(LABEL)
            .build();
        given(objectMapperWrapper.convertValue(DATA, Link.class)).willReturn(link);

        underTest.validateData(DATA);
    }
}