package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ReservedStoragesTest {
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();

    private final ReservedStorages underTest = new ReservedStorages();

    @Mock
    private ReservedStorage reservedStorage;

    @Before
    public void setUp() {
        underTest.add(reservedStorage);
    }

    @Test
    public void findById() {
        given(reservedStorage.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);

        Optional<ReservedStorage> result = underTest.findById(RESERVED_STORAGE_ID);

        assertThat(result).contains(reservedStorage);
    }
}