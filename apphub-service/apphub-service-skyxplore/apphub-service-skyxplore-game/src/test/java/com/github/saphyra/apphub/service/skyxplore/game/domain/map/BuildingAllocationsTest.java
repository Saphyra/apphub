package com.github.saphyra.apphub.service.skyxplore.game.domain.map;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;

import static org.assertj.core.api.Assertions.assertThat;

public class BuildingAllocationsTest {
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();

    private final BuildingAllocations underTest = new BuildingAllocations();

    @Test
    public void get_noItems() {
        assertThat(underTest.get(BUILDING_ID)).isInstanceOf(Vector.class);
    }

    @Test
    public void get_hasItem() {
        underTest.put(BUILDING_ID, List.of(PROCESS_ID));

        List<UUID> result = underTest.get(BUILDING_ID);

        assertThat(result).containsExactly(PROCESS_ID);
    }

    @Test
    public void add() {
        underTest.add(BUILDING_ID, PROCESS_ID);

        assertThat(underTest.values()).containsExactly(List.of(PROCESS_ID));
    }

    @Test
    public void findByProcessId() {
        underTest.add(BUILDING_ID, PROCESS_ID);

        Optional<UUID> result = underTest.findByProcessId(PROCESS_ID);

        assertThat(result).contains(BUILDING_ID);
    }

    @Test
    public void releaseByProcessId() {
        underTest.put(BUILDING_ID, CollectionUtils.toList(PROCESS_ID, PROCESS_ID));

        underTest.releaseByProcessId(PROCESS_ID);

        assertThat(underTest.get(BUILDING_ID)).isEmpty();
    }
}