package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class StarSystemWriteBufferTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();

    @Mock
    private StarSystemRepository repository;

    @Mock
    private StarSystemConverter converter;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private StarSystemWriteBuffer underTest;

    @Mock
    private StarSystem domain;

    @Mock
    private StarSystemEntity entity;

    @Test
    void synchronize() {
        underTest.add(STAR_SYSTEM_ID, domain);
        given(converter.convertDomain(any(Collection.class))).willReturn(List.of(entity));

        underTest.synchronize();

        then(repository).should().saveAll(List.of(entity));
    }
}