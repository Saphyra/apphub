package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
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
public class AllocatedResourcesTest {
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final UUID ALLOCATED_RESOURCE_ID = UUID.randomUUID();

    private final AllocatedResources underTest = new AllocatedResources();

    @Mock
    private AllocatedResource allocatedResource;

    @BeforeEach
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

    @Test
    public void getByExternalReference() {
        given(allocatedResource.getExternalReference()).willReturn(EXTERNAL_REFERENCE);

        List<AllocatedResource> result = underTest.getByExternalReference(EXTERNAL_REFERENCE);

        assertThat(result).containsExactly(allocatedResource);
    }

    @Test
    public void findByIdValidated_notFound() {
        given(allocatedResource.getAllocatedResourceId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.findByIdValidated(ALLOCATED_RESOURCE_ID));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void findByIdValidated() {
        given(allocatedResource.getAllocatedResourceId()).willReturn(ALLOCATED_RESOURCE_ID);

        AllocatedResource result = underTest.findByIdValidated(ALLOCATED_RESOURCE_ID);

        assertThat(result).isEqualTo(allocatedResource);
    }
}