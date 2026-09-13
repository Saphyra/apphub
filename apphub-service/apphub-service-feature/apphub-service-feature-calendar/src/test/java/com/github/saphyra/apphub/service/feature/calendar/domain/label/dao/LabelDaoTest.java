package com.github.saphyra.apphub.service.feature.calendar.domain.label.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @Mock
    private LabelCache labelCache;

    @InjectMocks
    private LabelDao underTest;

    @Mock
    private LabelEntity entity;

    @Mock
    private Label domain;

    @Test
    void getByLabelIds() {
        given(labelCache.get(eq(USER_ID), any())).willReturn(Map.of(LABEL_ID, domain));

        List<Label> result = underTest.getByLabelIds(USER_ID, List.of(LABEL_ID));

        assertThat(result).containsExactly(domain);
    }

    @Test
    void save() {
        given(converter.convertDomain(domain)).willReturn(entity);
        given(domain.getUserId()).willReturn(USER_ID);

        underTest.save(domain);

        then(repository).should().save(entity);
        then(labelCache).should().invalidate(USER_ID);
    }

    @Test
    void findByIdValidated_notFound() {
        given(labelCache.get(eq(USER_ID), any())).willReturn(Map.of());

        ExceptionValidator.validateNotFoundException(() -> underTest.findByIdValidated(USER_ID, LABEL_ID));
    }

    @Test
    void findByIdValidated() {
        given(labelCache.get(eq(USER_ID), any())).willReturn(Map.of(LABEL_ID, domain));

        assertThat(underTest.findByIdValidated(USER_ID, LABEL_ID)).isEqualTo(domain);
    }

    @Test
    void getByUserId() {
        given(labelCache.get(eq(USER_ID), any())).willReturn(Map.of(LABEL_ID, domain));
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));
        given(domain.getLabelId()).willReturn(LABEL_ID);

        assertThat(underTest.getByUserId(USER_ID)).containsEntry(LABEL_ID, domain);

        ArgumentCaptor<Supplier<Map<UUID, Label>>> argumentCaptor = ArgumentCaptor.forClass(Supplier.class);
        then(labelCache).should().get(eq(USER_ID), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().get()).containsEntry(LABEL_ID, domain);
    }

    @Test
    void delete() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(LABEL_ID)).willReturn(LABEL_ID_STRING);

        underTest.delete(USER_ID, LABEL_ID);

        then(repository).should().delete(USER_ID_STRING, LABEL_ID_STRING);
        then(labelCache).should().invalidate(USER_ID);
    }

    @Test
    void getByIds() {
        given(labelCache.get(eq(USER_ID), any())).willReturn(Map.of(LABEL_ID, domain));

        assertThat(underTest.getByIds(List.of(new BiWrapper<>(USER_ID, LABEL_ID)))).containsExactly(domain);
    }
}

