package com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen;

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
class CitizensTest {
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();

    private final Citizens underTest = new Citizens();

    @Mock
    private Citizen citizen1;

    @Mock
    private Citizen citizen2;

    @Test
    void findByCitizenIdValidated_found() {
        given(citizen1.getCitizenId()).willReturn(CITIZEN_ID);

        underTest.add(citizen1);

        assertThat(underTest.findByCitizenIdValidated(CITIZEN_ID)).isEqualTo(citizen1);
    }

    @Test
    void findByCitizenIdValidated_notFound() {
        given(citizen1.getCitizenId()).willReturn(UUID.randomUUID());

        underTest.add(citizen1);

        Throwable ex = catchThrowable(() -> underTest.findByCitizenIdValidated(CITIZEN_ID));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void getByLocation() {
        given(citizen1.getLocation()).willReturn(LOCATION);
        given(citizen2.getLocation()).willReturn(UUID.randomUUID());

        underTest.addAll(List.of(citizen1, citizen2));

        assertThat(underTest.getByLocation(LOCATION)).containsExactly(citizen1);
    }
}