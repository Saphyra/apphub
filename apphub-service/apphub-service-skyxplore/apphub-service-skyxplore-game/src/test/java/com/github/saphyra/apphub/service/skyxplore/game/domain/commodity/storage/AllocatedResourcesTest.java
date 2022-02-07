package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AllocatedResourcesTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";

    private final AllocatedResources underTest = new AllocatedResources();

    @Mock
    private AllocatedResource allocatedResource;

    @Before
    public void setUp() {
        underTest.add(allocatedResource);
    }

    @Test
    public void findByExternalReferenceAndDataIdValidated_notFound() {
        Throwable ex = catchThrowable(() -> underTest.findByExternalReferenceAndDataIdValidated(EXTERNAL_REFERENCE, DATA_ID));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void findByExternalReferenceAndDataIdValidated() {
        given(allocatedResource.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(allocatedResource.getDataId()).willReturn(DATA_ID);

        assertThat(underTest.findByExternalReferenceAndDataIdValidated(EXTERNAL_REFERENCE, DATA_ID)).isEqualTo(allocatedResource);
    }
}