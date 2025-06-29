package com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource;

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
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StoredResourcesTest {
    private static final UUID LOCATION_1 = UUID.randomUUID();
    private static final UUID LOCATION_2 = UUID.randomUUID();
    private static final String DATA_ID_1 = "data-id-1";
    private static final String DATA_ID_2 = "data-id-2";
    private static final UUID CONTAINER_ID_1 = UUID.randomUUID();
    private static final UUID CONTAINER_ID_2 = UUID.randomUUID();
    private static final UUID ALLOCATED_BY_1 = UUID.randomUUID();
    private static final UUID ALLOCATED_BY_2 = UUID.randomUUID();

    private final StoredResources underTest = new StoredResources();

    @Mock
    private StoredResource storedResource1;

    @Mock
    private StoredResource storedResource2;

    @Mock
    private StoredResource storedResource3;

    @Test
    void getByLocation() {
        given(storedResource1.getLocation()).willReturn(LOCATION_1);
        given(storedResource2.getLocation()).willReturn(UUID.randomUUID());

        underTest.addAll(List.of(storedResource1, storedResource2));

        assertThat(underTest.getByLocation(LOCATION_1)).containsExactly(storedResource1);
    }

    @Test
    void getByLocationAndDataId() {
        underTest.addAll(List.of(storedResource1, storedResource2, storedResource3));

        given(storedResource1.getLocation()).willReturn(LOCATION_1);
        given(storedResource1.getDataId()).willReturn(DATA_ID_1);

        given(storedResource2.getLocation()).willReturn(LOCATION_2);

        given(storedResource3.getLocation()).willReturn(LOCATION_1);
        given(storedResource3.getDataId()).willReturn(DATA_ID_2);

        assertThat(underTest.getByLocationAndDataId(LOCATION_1, DATA_ID_1)).containsExactly(storedResource1);
    }

    @Test
    void getByContainerId() {
        underTest.addAll(List.of(storedResource1, storedResource2));

        given(storedResource1.getContainerId()).willReturn(CONTAINER_ID_1);
        given(storedResource2.getContainerId()).willReturn(CONTAINER_ID_2);

        assertThat(underTest.getByContainerId(CONTAINER_ID_1)).containsExactly(storedResource1);
    }

    @Test
    void getByAllocatedBy() {
        underTest.addAll(List.of(storedResource1, storedResource2, storedResource3));

        given(storedResource1.getAllocatedBy()).willReturn(ALLOCATED_BY_1);
        given(storedResource2.getAllocatedBy()).willReturn(ALLOCATED_BY_2);
        given(storedResource3.getAllocatedBy()).willReturn(null);

        assertThat(underTest.getByAllocatedBy(ALLOCATED_BY_1)).containsExactly(storedResource1);
    }

    @Test
    void findByAllocatedByValidated() {
        underTest.addAll(List.of(storedResource1, storedResource2));

        given(storedResource1.getAllocatedBy()).willReturn(null);
        given(storedResource2.getAllocatedBy()).willReturn(ALLOCATED_BY_1);

        assertThat(underTest.findByAllocatedByValidated(ALLOCATED_BY_1)).isEqualTo(storedResource2);
    }

    @Test
    void findByAllocatedByValidated_notFound() {
        ExceptionValidator.validateNotLoggedException(() -> underTest.findByAllocatedByValidated(ALLOCATED_BY_2), HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void findByContainerIdValidated() {
        underTest.add(storedResource1);
        given(storedResource1.getContainerId()).willReturn(CONTAINER_ID_1);

        assertThat(underTest.findByContainerIdValidated(CONTAINER_ID_1)).isEqualTo(storedResource1);
    }

    @Test
    void findByContainerIdValidated_notFound() {
        ExceptionValidator.validateNotLoggedException(() -> underTest.findByContainerIdValidated(CONTAINER_ID_1), HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }
}