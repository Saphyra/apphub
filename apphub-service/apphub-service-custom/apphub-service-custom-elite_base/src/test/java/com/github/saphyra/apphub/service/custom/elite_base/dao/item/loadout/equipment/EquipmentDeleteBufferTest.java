package com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.equipment;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemDomainId;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemEntityId;
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
            .id(ItemEntityId.builder()
                .externalReference(EXTERNAL_REFERENCE_1.toString())
                .itemName(ITEM_NAME_1)
                .build())
            .build();
        equipmentRepository.save(toDelete);
        EquipmentEntity externalReferenceMismatch = EquipmentEntity.builder()
            .id(ItemEntityId.builder()
                .externalReference(EXTERNAL_REFERENCE_2.toString())
                .itemName(ITEM_NAME_1)
                .build())
            .build();
        equipmentRepository.save(externalReferenceMismatch);
        EquipmentEntity itemNameMismatch = EquipmentEntity.builder()
            .id(ItemEntityId.builder()
                .externalReference(EXTERNAL_REFERENCE_1.toString())
                .itemName(ITEM_NAME_2)
                .build())
            .build();
        equipmentRepository.save(itemNameMismatch);

        ItemDomainId domainId = ItemDomainId.builder()
            .externalReference(EXTERNAL_REFERENCE_1)
            .itemName(ITEM_NAME_1)
            .build();

        underTest.doSynchronize(List.of(domainId));

        assertThat(equipmentRepository.findAll()).containsExactlyInAnyOrder(externalReferenceMismatch, itemNameMismatch);
    }
}