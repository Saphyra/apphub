package com.github.saphyra.apphub.service.custom.elite_base.dao.loadout;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class LoadoutWriteBufferTest {
    @Mock
    private LoadoutRepository repository;

    @Mock
    private LoadoutConverter converter;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private LoadoutWriteBuffer underTest;

    @Mock
    private LoadoutDomainId domainId;

    @Mock
    private Loadout domain;

    @Mock
    private LoadoutEntity entity;

    @Test
    void synchronize() {
        underTest.add(domainId, domain);
        given(converter.convertDomain(any(Collection.class))).willReturn(List.of(entity));

        underTest.synchronize();

        then(repository).should().saveAll(List.of(entity));
    }
}