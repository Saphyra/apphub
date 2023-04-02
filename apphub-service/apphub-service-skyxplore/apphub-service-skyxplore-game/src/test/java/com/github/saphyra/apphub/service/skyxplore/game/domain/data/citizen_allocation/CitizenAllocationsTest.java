package com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation;

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
class CitizenAllocationsTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();

    private final CitizenAllocations underTest = new CitizenAllocations();

    @Mock
    private CitizenAllocation citizenAllocation1;

    @Mock
    private CitizenAllocation citizenAllocation2;

    @Test
    void findByProcessId_found() {
        given(citizenAllocation1.getProcessId()).willReturn(PROCESS_ID);

        underTest.add(citizenAllocation1);

        assertThat(underTest.findByProcessId(PROCESS_ID)).contains(citizenAllocation1);
    }

    @Test
    void findByProcessIdValidated_notFound() {
        given(citizenAllocation1.getProcessId()).willReturn(UUID.randomUUID());

        underTest.add(citizenAllocation1);

        Throwable ex = catchThrowable(() -> underTest.findByProcessIdValidated(PROCESS_ID));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void deleteByProcessId() {
        given(citizenAllocation1.getProcessId()).willReturn(PROCESS_ID);
        given(citizenAllocation2.getProcessId()).willReturn(UUID.randomUUID());

        underTest.addAll(List.of(citizenAllocation1, citizenAllocation2));

        underTest.deleteByProcessId(PROCESS_ID);

        assertThat(underTest).containsExactly(citizenAllocation2);
    }

    @Test
    void findByCitizenIdValidated_found() {
        given(citizenAllocation1.getCitizenId()).willReturn(CITIZEN_ID);

        underTest.add(citizenAllocation1);

        assertThat(underTest.findByCitizenIdValidated(CITIZEN_ID)).isEqualTo(citizenAllocation1);
    }

    @Test
    void findByCitizenIdValidated_notFound() {
        given(citizenAllocation1.getCitizenId()).willReturn(UUID.randomUUID());

        underTest.add(citizenAllocation1);

        Throwable ex = catchThrowable(() -> underTest.findByCitizenIdValidated(CITIZEN_ID));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }
}