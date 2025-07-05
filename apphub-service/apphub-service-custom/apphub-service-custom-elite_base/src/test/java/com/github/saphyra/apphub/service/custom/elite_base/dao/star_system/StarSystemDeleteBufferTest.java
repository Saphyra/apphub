package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class StarSystemDeleteBufferTest {
    private static final UUID STAR_SYSTEM_ID = UUID.randomUUID();
    private static final String STAR_SYSTEM_ID_STRING = "star-system-id";

    @Mock
    private StarSystemRepository repository;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private StarSystemDeleteBuffer underTest;

    @Test
    void synchronize() {
        underTest.add(STAR_SYSTEM_ID);

        given(uuidConverter.convertDomain(STAR_SYSTEM_ID)).willReturn(STAR_SYSTEM_ID_STRING);

        underTest.doSynchronize();

        then(repository).should().deleteAllById(List.of(STAR_SYSTEM_ID_STRING));
    }
}