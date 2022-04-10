package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ReservedStoragesTest {
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();

    private final ReservedStorages underTest = new ReservedStorages();

    @Mock
    private ReservedStorage reservedStorage;

    @Before
    public void setUp() {
        underTest.add(reservedStorage);
    }

    @Test
    public void findByIdValidated_notFound() {
        given(reservedStorage.getReservedStorageId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.findByIdValidated(RESERVED_STORAGE_ID));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void findByIdValidated() {
        given(reservedStorage.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);

        ReservedStorage result = underTest.findByIdValidated(RESERVED_STORAGE_ID);

        assertThat(result).isEqualTo(reservedStorage);
    }

    @Test
    public void findById() {
        given(reservedStorage.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);

        Optional<ReservedStorage> result = underTest.findById(RESERVED_STORAGE_ID);

        assertThat(result).contains(reservedStorage);
    }

    @Test
    public void getByExternalReference() {
        given(reservedStorage.getExternalReference()).willReturn(EXTERNAL_REFERENCE);

        List<ReservedStorage> result = underTest.getByExternalReference(EXTERNAL_REFERENCE);

        assertThat(result).containsExactly(reservedStorage);
    }
}