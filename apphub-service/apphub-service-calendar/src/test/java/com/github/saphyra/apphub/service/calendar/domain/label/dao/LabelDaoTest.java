package com.github.saphyra.apphub.service.calendar.domain.label.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class LabelDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final String LABEL_ID_STRING = "label-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private LabelConverter converter;

    @Mock
    private LabelRepository repository;

    @InjectMocks
    private LabelDao underTest;

    @Mock
    private Label domain;

    @Mock
    private LabelEntity entity;

    @Test
    void deleteByUserId(){
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        then(repository).should().deleteByUserId(USER_ID_STRING);
    }

    @Test
    void existsById(){
        given(uuidConverter.convertDomain(LABEL_ID)).willReturn(LABEL_ID_STRING);
        given(repository.existsById(LABEL_ID_STRING)).willReturn(true);

        assertThat(underTest.existsById(LABEL_ID)).isTrue();
    }

    @Test
    void getByUserId(){
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(java.util.List.of(entity));
        given(converter.convertEntity(java.util.List.of(entity))).willReturn(java.util.List.of(domain));

        assertThat(underTest.getByUserId(USER_ID)).containsExactly(domain);
    }

    @Test
    void deleteByUserIdAndLabelId(){
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(LABEL_ID)).willReturn(LABEL_ID_STRING);

        underTest.deleteByUserIdAndLabelId(USER_ID, LABEL_ID);

        then(repository).should().deleteByUserIdAndLabelId(USER_ID_STRING, LABEL_ID_STRING);
    }

    @Test
    void findByIdValidated_notFound(){
        given(uuidConverter.convertDomain(LABEL_ID)).willReturn(LABEL_ID_STRING);
        given(repository.findById(LABEL_ID_STRING)).willReturn(Optional.empty());

        ExceptionValidator.validateNotFoundException(() -> underTest.findByIdValidated(LABEL_ID));
    }

    @Test
    void findByIdValidated(){
        given(uuidConverter.convertDomain(LABEL_ID)).willReturn(LABEL_ID_STRING);
        given(repository.findById(LABEL_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByIdValidated(LABEL_ID)).isEqualTo(domain);
    }
}