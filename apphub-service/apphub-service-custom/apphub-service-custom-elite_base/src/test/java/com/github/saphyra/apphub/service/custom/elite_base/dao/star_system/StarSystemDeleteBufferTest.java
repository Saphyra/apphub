package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system;

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
class StarSystemDeleteBufferTest {
    private static final UUID STAR_SYSTEM_ID_1 = UUID.randomUUID();
    private static final UUID STAR_SYSTEM_ID_2 = UUID.randomUUID();

    @Autowired
    private StarSystemRepository repository;

    @Autowired
    private StarSystemDeleteBuffer underTest;

    @AfterEach
    void clear() {
        repository.deleteAll();
    }

    @Test
    void synchronize() {
        underTest.add(STAR_SYSTEM_ID_1);

        StarSystemEntity entity1 = StarSystemEntity.builder()
            .id(STAR_SYSTEM_ID_1.toString())
            .build();
        repository.save(entity1);
        StarSystemEntity entity2 = StarSystemEntity.builder()
            .id(STAR_SYSTEM_ID_2.toString())
            .build();
        repository.save(entity2);

        underTest.synchronize();

        assertThat(repository.findAll()).containsExactly(entity2);
    }
}