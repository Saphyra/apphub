package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.notebook.service.table.dto.Link;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LinkColumnDataServiceTest {
    private static final Object DATA = "data";
    private static final String STRINGIFIED = "stringified";
    private static final String URL = "https://example.com";
    private static final String LABEL = "Example";

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private LinkColumnDataService underTest;

    @Test
    void canProcess() {
        assertThat(underTest.canProcess(ColumnType.LINK)).isTrue();
    }

    @Test
    void canProcess_notLink() {
        assertThat(underTest.canProcess(ColumnType.TEXT)).isFalse();
    }

    @Test
    void validateData_null() {
        Throwable ex = catchThrowable(() -> underTest.validateData(null));

        ExceptionValidator.validateInvalidParam(ex, "link", "must not be null");
    }

    @Test
    void validateData_parseError() {
        given(objectMapper.convertValue(DATA, Link.class)).willThrow(new RuntimeException("parse error"));

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "link", "failed to parse");
    }

    @Test
    void validateData_blankLabel() {
        Link link = Link.builder().label(" ").url(URL).build();
        given(objectMapper.convertValue(DATA, Link.class)).willReturn(link);

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "link.label", "must not be null or blank");
    }

    @Test
    void validateData_nullUrl() {
        Link link = Link.builder().label(LABEL).url(null).build();
        given(objectMapper.convertValue(DATA, Link.class)).willReturn(link);

        Throwable ex = catchThrowable(() -> underTest.validateData(DATA));

        ExceptionValidator.validateInvalidParam(ex, "link.url", "must not be null");
    }

    @Test
    void validateData() {
        Link link = Link.builder().label(LABEL).url(URL).build();
        given(objectMapper.convertValue(DATA, Link.class)).willReturn(link);

        underTest.validateData(DATA);
    }

    @Test
    void serialize() {
        given(objectMapper.writeValueAsString(DATA)).willReturn(STRINGIFIED);

        Optional<BiWrapper<String, Optional<UUID>>> result = underTest.serialize(DATA);

        assertThat(result).isPresent();
        assertThat(result.get().getEntity1()).isEqualTo(STRINGIFIED);
        assertThat(result.get().getEntity2()).isEmpty();
    }

    @Test
    void deserialize() {
        Link link = Link.builder().label(LABEL).url(URL).build();
        given(objectMapper.readValue(STRINGIFIED, Link.class)).willReturn(link);

        Object result = underTest.deserialize(STRINGIFIED);

        assertThat(result).isEqualTo(link);
    }
}

