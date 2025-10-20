package com.github.saphyra.apphub.service.custom.elite_base.dao.loadout;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LoadoutDeleteBufferItTest {
    private static final UUID EXTERNAL_REFERENCE_1 = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE_2 = UUID.randomUUID();
    private static final String LOADOUT_NAME_1 = "loadout-name-1";
    private static final String LOADOUT_NAME_2 = "loadout-name-2";

    @Autowired
    private LoadoutRepository loadoutRepository;

    @Autowired
    private LoadoutDeleteBuffer underTest;

    @AfterEach
    void clear(){
        loadoutRepository.deleteAll();
    }

    @Test
    void synchronize() {
        LoadoutDomainId domainId = LoadoutDomainId.builder()
            .externalReference(EXTERNAL_REFERENCE_1)
            .type(LoadoutType.SHIPYARD)
            .name(LOADOUT_NAME_1)
            .build();
        underTest.add(domainId);

        LoadoutEntity entity1 = LoadoutEntity.builder()
            .externalReference(EXTERNAL_REFERENCE_1.toString())
            .type(LoadoutType.SHIPYARD)
            .name(LOADOUT_NAME_1)
            .build();
        loadoutRepository.save(entity1);
        LoadoutEntity entity2 = LoadoutEntity.builder()
            .externalReference(EXTERNAL_REFERENCE_2.toString())
            .type(LoadoutType.SHIPYARD)
            .name(LOADOUT_NAME_1)
            .build();
        loadoutRepository.save(entity2);
        LoadoutEntity entity3 = LoadoutEntity.builder()
            .externalReference(EXTERNAL_REFERENCE_1.toString())
            .type(LoadoutType.OUTFITTING)
            .name(LOADOUT_NAME_1)
            .build();
        loadoutRepository.save(entity3);
        LoadoutEntity entity4 = LoadoutEntity.builder()
            .externalReference(EXTERNAL_REFERENCE_1.toString())
            .type(LoadoutType.SHIPYARD)
            .name(LOADOUT_NAME_2)
            .build();
        loadoutRepository.save(entity4);

        underTest.synchronize();

        assertThat(loadoutRepository.findAll()).containsExactlyInAnyOrder(entity2, entity3, entity4);
    }
}