package com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_data;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
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

@ExtendWith(MockitoExtension.class)
class BodyDataDaoTest {
    private static final UUID ID = UUID.randomUUID();
    private static final String ID_STRING = "id-string";

    @Mock
    private BodyDataConverter converter;

    @Mock
    private BodyDataRepository repository;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private BodyDataDao underTest;

    @Mock
    private BodyData domain;

    @Mock
    private BodyDataEntity entity;

    @Test
    void findById(){
        given(uuidConverter.convertDomain(ID)).willReturn(ID_STRING);
        given(repository.findById(ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findById(ID)).contains(domain);
    }

    @Test
    void getByIds(){
        given(uuidConverter.convertDomain(List.of(ID))).willReturn(List.of(ID_STRING));
        given(repository.findAllById(List.of(ID_STRING))).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByIds(List.of(ID))).containsExactly(domain);
    }
}