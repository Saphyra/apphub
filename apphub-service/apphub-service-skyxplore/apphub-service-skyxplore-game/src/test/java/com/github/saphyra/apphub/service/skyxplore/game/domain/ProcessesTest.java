package com.github.saphyra.apphub.service.skyxplore.game.domain;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ProcessesTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();

    @InjectMocks
    private Processes underTest;

    @Mock
    private Process process;

    @Test
    public void findByIdValidated_notFound() {
        Throwable ex = catchThrowable(() -> underTest.findByIdValidated(PROCESS_ID));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void findByIdValidated() {
        underTest.add(process);
        given(process.getProcessId()).willReturn(PROCESS_ID);

        Process result = underTest.findByIdValidated(PROCESS_ID);

        assertThat(result).isEqualTo(process);
    }

    @Test
    public void findByExternalReferenceAndTypeValidated_notFound() {
        Throwable ex = catchThrowable(() -> underTest.findByExternalReferenceAndTypeValidated(EXTERNAL_REFERENCE, ProcessType.REQUEST_WORK));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }


    @Test
    public void findByExternalReferenceAndTypeValidated() {
        underTest.add(process);
        given(process.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(process.getType()).willReturn(ProcessType.PRODUCTION_ORDER);

        Process result = underTest.findByExternalReferenceAndTypeValidated(EXTERNAL_REFERENCE, ProcessType.PRODUCTION_ORDER);

        assertThat(result).isEqualTo(process);
    }

    @Test
    public void getByExternalReferenceAndType() {
        underTest.add(process);
        given(process.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(process.getType()).willReturn(ProcessType.PRODUCTION_ORDER);

        List<Process> result = underTest.getByExternalReferenceAndType(EXTERNAL_REFERENCE, ProcessType.PRODUCTION_ORDER);

        assertThat(result).containsExactly(process);
    }

    @Test
    public void getByExternalReference() {
        underTest.add(process);
        given(process.getExternalReference()).willReturn(EXTERNAL_REFERENCE);

        List<Process> result = underTest.getByExternalReference(EXTERNAL_REFERENCE);

        assertThat(result).containsExactly(process);
    }
}