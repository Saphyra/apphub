package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool_type;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ToolTypeDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID TOOL_TYPE_ID = UUID.randomUUID();
    private static final String TOOL_TYPE_ID_STRING = "tool-type-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ToolTypeConverter converter;

    @Mock
    private ToolTypeRepository repository;

    @InjectMocks
    private ToolTypeDao underTest;

    @Mock
    private ToolType domain;

    @Mock
    private ToolTypeEntity entity;

    @Test
    void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        then(repository).should().deleteByUserId(USER_ID_STRING);
    }

    @Test
    void findByIdValidated_notFound() {
        ExceptionValidator.validateNotLoggedException(() -> underTest.findByIdValidated(TOOL_TYPE_ID), HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void findByIdValidated() {
        given(uuidConverter.convertDomain(TOOL_TYPE_ID)).willReturn(TOOL_TYPE_ID_STRING);
        given(repository.findById(TOOL_TYPE_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByIdValidated(TOOL_TYPE_ID)).isEqualTo(domain);
    }

    @Test
    void exists() {
        given(uuidConverter.convertDomain(TOOL_TYPE_ID)).willReturn(TOOL_TYPE_ID_STRING);
        given(repository.existsById(TOOL_TYPE_ID_STRING)).willReturn(true);

        assertThat(underTest.exists(TOOL_TYPE_ID)).isTrue();
    }

    @Test
    void getByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByUserId(USER_ID)).containsExactly(domain);
    }
}