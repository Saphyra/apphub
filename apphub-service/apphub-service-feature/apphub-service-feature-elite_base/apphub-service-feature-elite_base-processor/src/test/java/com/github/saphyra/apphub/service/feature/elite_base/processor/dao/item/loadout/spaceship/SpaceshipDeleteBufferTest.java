package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.loadout.spaceship;

import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.ItemDomainId;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.ItemLocationType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SpaceshipDeleteBufferTest {
    private static final UUID EXTERNAL_REFERENCE_1 = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE_2 = UUID.randomUUID();
    private static final String ITEM_NAME_1 = "item-name-1";
    private static final String ITEM_NAME_2 = "item-name-2";

    @Autowired
    private SpaceshipRepository repository;

    @Autowired
    private SpaceshipDeleteBuffer underTest;

    @AfterEach
    void clear() {
        repository.deleteAll();
    }

    @Test
    void synchronize() {
        SpaceshipEntity toDelete = SpaceshipEntity.builder()
            .id(UUID.randomUUID().toString())
            .itemName(ITEM_NAME_1)
            .stationId(EXTERNAL_REFERENCE_1.toString())
            .build();
        repository.save(toDelete);
        SpaceshipEntity externalReferenceMismatch = SpaceshipEntity.builder()
            .id(UUID.randomUUID().toString())
            .itemName(ITEM_NAME_1)
            .stationId(EXTERNAL_REFERENCE_2.toString())
            .build();
        repository.save(externalReferenceMismatch);
        SpaceshipEntity itemNameMismatch = SpaceshipEntity.builder()
            .id(UUID.randomUUID().toString())
            .itemName(ITEM_NAME_2)
            .stationId(EXTERNAL_REFERENCE_1.toString())
            .build();
        repository.save(itemNameMismatch);
        SpaceshipEntity otherLocation = SpaceshipEntity.builder()
            .id(UUID.randomUUID().toString())
            .itemName(ITEM_NAME_1)
            .fleetCarrierId(EXTERNAL_REFERENCE_1.toString())
            .build();
        repository.save(otherLocation);

        ItemDomainId domainId = ItemDomainId.builder()
            .externalReference(EXTERNAL_REFERENCE_1)
            .itemName(ITEM_NAME_1)
            .locationType(ItemLocationType.STATION)
            .build();

        underTest.doSynchronize(List.of(domainId));

        assertThat(repository.findAll()).containsExactlyInAnyOrder(externalReferenceMismatch, itemNameMismatch, otherLocation);
    }
}