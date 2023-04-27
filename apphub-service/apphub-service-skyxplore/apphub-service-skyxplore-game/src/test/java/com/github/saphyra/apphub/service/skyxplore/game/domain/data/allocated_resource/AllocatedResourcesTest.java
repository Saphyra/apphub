package com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource;

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
class AllocatedResourcesTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID ALLOCATED_RESOURCE_ID = UUID.randomUUID();

    private final AllocatedResources underTest = new AllocatedResources();

    @Mock
    private AllocatedResource allocatedResource1;

    @Mock
    private AllocatedResource allocatedResource2;

    @Mock
    private AllocatedResource allocatedResource3;

    @Test
    void getByLocationAndDataId() {
        given(allocatedResource1.getLocation()).willReturn(LOCATION);
        given(allocatedResource1.getDataId()).willReturn(DATA_ID);

        given(allocatedResource2.getLocation()).willReturn(LOCATION);
        given(allocatedResource2.getDataId()).willReturn("asd");

        given(allocatedResource3.getLocation()).willReturn(UUID.randomUUID());

        underTest.addAll(List.of(allocatedResource1, allocatedResource2, allocatedResource3));

        List<AllocatedResource> result = underTest.getByLocationAndDataId(LOCATION, DATA_ID);

        assertThat(result).containsExactly(allocatedResource1);
    }

    @Test
    void findByExternalReferenceAndDataIdValidated_found() {
        given(allocatedResource1.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(allocatedResource1.getDataId()).willReturn(DATA_ID);

        underTest.add(allocatedResource1);

        assertThat(underTest.findByExternalReferenceAndDataIdValidated(EXTERNAL_REFERENCE, DATA_ID)).isEqualTo(allocatedResource1);
    }

    @Test
    void findByExternalReferenceAndDataIdValidated_notFound() {
        given(allocatedResource1.getExternalReference()).willReturn(UUID.randomUUID());

        given(allocatedResource2.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(allocatedResource2.getDataId()).willReturn("asd");

        underTest.addAll(List.of(allocatedResource1, allocatedResource2));

        Throwable ex = catchThrowable(() -> underTest.findByExternalReferenceAndDataIdValidated(EXTERNAL_REFERENCE, DATA_ID));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void getByExternalReference() {
        given(allocatedResource1.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(allocatedResource2.getExternalReference()).willReturn(UUID.randomUUID());

        underTest.addAll(List.of(allocatedResource1, allocatedResource2));

        assertThat(underTest.getByExternalReference(EXTERNAL_REFERENCE)).containsExactly(allocatedResource1);
    }

    @Test
    void findByAllocatedResourceId_found() {
        given(allocatedResource1.getAllocatedResourceId()).willReturn(ALLOCATED_RESOURCE_ID);

        underTest.add(allocatedResource1);

        assertThat(underTest.findByAllocatedResourceId(ALLOCATED_RESOURCE_ID)).contains(allocatedResource1);
    }

    @Test
    void findByAllocatedResourceIdValidated_notFound() {
        given(allocatedResource1.getAllocatedResourceId()).willReturn(UUID.randomUUID());

        underTest.add(allocatedResource1);

        Throwable ex = catchThrowable(() -> underTest.findByAllocatedResourceIdValidated(ALLOCATED_RESOURCE_ID));

        ExceptionValidator.validateLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void getByLocation() {
        given(allocatedResource1.getLocation()).willReturn(LOCATION);
        given(allocatedResource2.getLocation()).willReturn(UUID.randomUUID());

        underTest.addAll(List.of(allocatedResource1, allocatedResource2));

        assertThat(underTest.getByLocation(LOCATION)).containsExactly(allocatedResource1);
    }
}