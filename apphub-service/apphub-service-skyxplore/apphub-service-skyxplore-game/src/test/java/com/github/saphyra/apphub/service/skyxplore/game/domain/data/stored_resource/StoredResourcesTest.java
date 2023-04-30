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
    private static final String DATA_ID = "data-id";

    private final StoredResources underTest = new StoredResources();

    @Mock
    private StoredResource storedResource1;

    @Mock
    private StoredResource storedResource2;

    @Test
    void findByLocationAndDataIdOrDefault_found() {
        given(storedResource1.getLocation()).willReturn(LOCATION);
        given(storedResource1.getDataId()).willReturn(DATA_ID);

        underTest.add(storedResource1);

        assertThat(underTest.findByLocationAndDataIdOrDefault(LOCATION, DATA_ID)).isEqualTo(storedResource1);
    }

    @Test
    void findByLocationAndDataIdOrDefault_createDefault() {
        given(storedResource1.getLocation()).willReturn(UUID.randomUUID());

        given(storedResource2.getLocation()).willReturn(LOCATION);
        given(storedResource2.getDataId()).willReturn("asd");

        underTest.add(storedResource1);
        underTest.add(storedResource2);

        StoredResource result = underTest.findByLocationAndDataIdOrDefault(LOCATION, DATA_ID);

        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getAmount()).isEqualTo(0);
    }

    @Test
    void findByLocationAndDataId_found() {
        given(storedResource1.getLocation()).willReturn(LOCATION);
        given(storedResource1.getDataId()).willReturn(DATA_ID);

        underTest.add(storedResource1);

        assertThat(underTest.findByLocationAndDataId(LOCATION, DATA_ID)).contains(storedResource1);
    }

    @Test
    void findByLocationAndDataIdOrDefault_notFound() {
        given(storedResource1.getLocation()).willReturn(UUID.randomUUID());

        given(storedResource2.getLocation()).willReturn(LOCATION);
        given(storedResource2.getDataId()).willReturn("asd");

        underTest.add(storedResource1);
        underTest.add(storedResource2);

        assertThat(underTest.findByLocationAndDataId(LOCATION, DATA_ID)).isEmpty();
    }

    @Test
    void getByLocation() {
        given(storedResource1.getLocation()).willReturn(LOCATION);
        given(storedResource2.getLocation()).willReturn(UUID.randomUUID());

        underTest.addAll(List.of(storedResource1, storedResource2));

        assertThat(underTest.getByLocation(LOCATION)).containsExactly(storedResource1);
    }
}