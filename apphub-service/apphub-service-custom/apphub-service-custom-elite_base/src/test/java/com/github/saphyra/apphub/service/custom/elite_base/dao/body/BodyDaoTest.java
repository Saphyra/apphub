package com.github.saphyra.apphub.service.custom.elite_base.dao.body;

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
class BodyDaoTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final Long BODY_ID = 234L;
    private static final String STAR_SYSTEM_ID_STRING = "star-system-id";
    private static final String BODY_NAME = "body-name";
    private static final UUID ID = UUID.randomUUID();
    private static final String ID_STRING = "id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private BodyConverter converter;

    @Mock
    private BodyRepository repository;

    @InjectMocks
    private BodyDao underTest;

    @Mock
    private BodyEntity entity;

    @Mock
    private Body domain;

    @Test
    void findByStarSystemIdAndBodyId() {
        given(uuidConverter.convertDomain(STAR_SYSTEM_ID)).willReturn(STAR_SYSTEM_ID_STRING);
        given(repository.findByStarSystemIdAndBodyId(STAR_SYSTEM_ID_STRING, BODY_ID)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByStarSystemIdAndBodyId(STAR_SYSTEM_ID, BODY_ID)).contains(domain);
    }

    @Test
    void findByBodyName() {
        given(repository.findByBodyName(BODY_NAME)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByBodyName(BODY_NAME)).contains(domain);
    }

    @Test
    void findAllById(){
        given(uuidConverter.convertDomain(List.of(ID))).willReturn(List.of(ID_STRING));
        given(repository.findAllById(List.of(ID_STRING))).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.findAllById(List.of(ID))).containsExactly(domain);
    }
}