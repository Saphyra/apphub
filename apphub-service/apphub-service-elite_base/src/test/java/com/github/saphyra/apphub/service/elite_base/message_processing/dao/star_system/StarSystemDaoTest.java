package com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StarSystemDaoTest {
    private static final Long STAR_ID = 324L;
    private static final String STAR_NAME = "star-name";

    @Mock
    private StarSystemConverter converter;

    @Mock
    private StarSystemRepository repository;

    @InjectMocks
    private StarSystemDao underTest;

    @Mock
    private StarSystem domain;

    @Mock
    private StarSystemEntity entity;

    @Test
    void findByStarId() {
        given(repository.findByStarId(STAR_ID)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByStarId(STAR_ID)).contains(domain);
    }

    @Test
    void findByStarName() {
        given(repository.findByStarName(STAR_NAME)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByStarName(STAR_NAME)).contains(domain);
    }
}