package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.loadout.equipment;

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
class EquipmentDeleteBufferTest {
    private static final UUID EXTERNAL_REFERENCE_1 = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE_2 = UUID.randomUUID();
    private static final String ITEM_NAME_1 = "item-name-1";
    private static final String ITEM_NAME_2 = "item-name-2";

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private EquipmentDeleteBuffer underTest;

    @AfterEach
    void clear() {
        equipmentRepository.deleteAll();
    }

    @Test
    void synchronize() {
        EquipmentEntity toDelete = EquipmentEntity.builder()
            .id(UUID.randomUUID().toString())
            .itemName(ITEM_NAME_1)
            .stationId(EXTERNAL_REFERENCE_1.toString())
            .build();
        equipmentRepository.save(toDelete);
        EquipmentEntity externalReferenceMismatch = EquipmentEntity.builder()
            .id(UUID.randomUUID().toString())
            .itemName(ITEM_NAME_1)
            .stationId(EXTERNAL_REFERENCE_2.toString())
            .build();
        equipmentRepository.save(externalReferenceMismatch);
        EquipmentEntity itemNameMismatch = EquipmentEntity.builder()
            .id(UUID.randomUUID().toString())
            .itemName(ITEM_NAME_2)
            .stationId(EXTERNAL_REFERENCE_1.toString())
            .build();
        equipmentRepository.save(itemNameMismatch);
        EquipmentEntity otherLocation = EquipmentEntity.builder()
            .id(UUID.randomUUID().toString())
            .itemName(ITEM_NAME_1)
            .fleetCarrierId(EXTERNAL_REFERENCE_1.toString())
            .build();
        equipmentRepository.save(otherLocation);

        ItemDomainId domainId = ItemDomainId.builder()
            .externalReference(EXTERNAL_REFERENCE_1)
            .itemName(ITEM_NAME_1)
            .locationType(ItemLocationType.STATION)
            .build();

        underTest.doSynchronize(List.of(domainId));

        assertThat(equipmentRepository.findAll()).containsExactlyInAnyOrder(externalReferenceMismatch, itemNameMismatch, otherLocation);
    }
}