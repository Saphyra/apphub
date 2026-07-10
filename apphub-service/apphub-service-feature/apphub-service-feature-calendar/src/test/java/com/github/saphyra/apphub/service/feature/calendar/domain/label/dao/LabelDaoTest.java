package com.github.saphyra.apphub.service.feature.calendar.domain.label.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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
    private LabelEntity entity;

    @Mock
    private Label domain;

    @Test
    void getByLabelIds() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(List.of(LABEL_ID))).willReturn(List.of(LABEL_ID_STRING));
        given(repository.getByLabelIds(USER_ID_STRING, List.of(LABEL_ID_STRING))).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        List<Label> result = underTest.getByLabelIds(USER_ID, List.of(LABEL_ID));

        assertThat(result).containsExactly(domain);
    }

    @Test
    void save() {
        given(converter.convertDomain(domain)).willReturn(entity);

        underTest.save(domain);

        then(repository).should().save(entity);
    }

    @Test
    void findByIdValidated_notFound() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(LABEL_ID)).willReturn(LABEL_ID_STRING);
        given(repository.findById(USER_ID_STRING, LABEL_ID_STRING)).willReturn(Optional.empty());
        given(converter.convertEntity(Optional.empty())).willReturn(Optional.empty());

        ExceptionValidator.validateNotFoundException(() -> underTest.findByIdValidated(USER_ID, LABEL_ID));
    }

    @Test
    void findByIdValidated() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(LABEL_ID)).willReturn(LABEL_ID_STRING);
        given(repository.findById(USER_ID_STRING, LABEL_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByIdValidated(USER_ID, LABEL_ID)).isEqualTo(domain);
    }

    @Test
    void getByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByUserId(USER_ID)).containsExactly(domain);
    }

    @Test
    void delete() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(LABEL_ID)).willReturn(LABEL_ID_STRING);

        underTest.delete(USER_ID, LABEL_ID);

        then(repository).should().delete(USER_ID_STRING, LABEL_ID_STRING);
    }
}

