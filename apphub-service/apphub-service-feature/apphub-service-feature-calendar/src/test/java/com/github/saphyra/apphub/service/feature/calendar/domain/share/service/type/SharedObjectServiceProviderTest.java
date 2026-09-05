package com.github.saphyra.apphub.service.feature.calendar.domain.share.service.type;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SharedObjectServiceProviderTest {
    @Mock
    private SharedObjectService sharedObjectService;

    private SharedObjectServiceProvider underTest;

    @BeforeEach
    void setUp() {
        underTest = new SharedObjectServiceProvider(List.of(sharedObjectService));
    }

    @Test
    void getForType() {
        given(sharedObjectService.getType()).willReturn(SharedObjectType.EVENT);

        assertThat(underTest.getForType(SharedObjectType.EVENT)).isEqualTo(sharedObjectService);
    }

    @Test
    void getForType_notFound() {
        given(sharedObjectService.getType()).willReturn(SharedObjectType.EVENT);

        ExceptionValidator.validateReportedException(catchThrowable(() -> underTest.getForType(SharedObjectType.LABEL)), HttpStatus.NOT_IMPLEMENTED, ErrorCode.GENERAL_ERROR);
    }
}