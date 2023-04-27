package com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ReservedStoragesTest {
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();

    private final ReservedStorages underTest = new ReservedStorages();

    @Mock
    private ReservedStorage reservedStorage1;

    @Mock
    private ReservedStorage reservedStorage2;

    @Test
    void findByReservedStorageIdValidated_found() {
        given(reservedStorage1.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);

        underTest.add(reservedStorage1);

        assertThat(underTest.findByReservedStorageIdValidated(RESERVED_STORAGE_ID)).isEqualTo(reservedStorage1);
    }

    @Test
    void findByReservedStorageIdValidated_notFound() {
        given(reservedStorage1.getReservedStorageId()).willReturn(UUID.randomUUID());

        underTest.add(reservedStorage1);

        Throwable ex = catchThrowable(() -> underTest.findByReservedStorageIdValidated(RESERVED_STORAGE_ID));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void getByExternalReference() {
        given(reservedStorage1.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(reservedStorage2.getExternalReference()).willReturn(UUID.randomUUID());

        underTest.addAll(List.of(reservedStorage1, reservedStorage2));

        assertThat(underTest.getByExternalReference(EXTERNAL_REFERENCE)).containsExactly(reservedStorage1);
    }

    @Test
    void getByLocation() {
        given(reservedStorage1.getLocation()).willReturn(LOCATION);
        given(reservedStorage2.getLocation()).willReturn(UUID.randomUUID());

        underTest.addAll(List.of(reservedStorage1, reservedStorage2));

        assertThat(underTest.getByLocation(LOCATION)).containsExactly(reservedStorage1);
    }
}