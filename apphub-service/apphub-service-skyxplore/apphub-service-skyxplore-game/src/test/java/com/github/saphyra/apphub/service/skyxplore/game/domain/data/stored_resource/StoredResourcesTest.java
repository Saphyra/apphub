package com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StoredResourcesTest {
    private static final UUID LOCATION = UUID.randomUUID();

    private final StoredResources underTest = new StoredResources();

    @Mock
    private StoredResource storedResource1;

    @Mock
    private StoredResource storedResource2;

    @Test
    void getByLocation() {
        given(storedResource1.getLocation()).willReturn(LOCATION);
        given(storedResource2.getLocation()).willReturn(UUID.randomUUID());

        underTest.addAll(List.of(storedResource1, storedResource2));

        assertThat(underTest.getByLocation(LOCATION)).containsExactly(storedResource1);
    }
}