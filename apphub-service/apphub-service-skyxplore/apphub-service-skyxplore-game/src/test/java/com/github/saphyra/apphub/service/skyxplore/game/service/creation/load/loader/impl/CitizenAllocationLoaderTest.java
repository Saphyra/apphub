package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CitizenAllocationLoaderTest {
    private static final UUID CITIZEN_ALLOCATION_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private CitizenAllocationLoader underTest;

    @Mock
    private GameData gameData;

    @Mock
    private CitizenAllocations citizenAllocations;

    @Mock
    private CitizenAllocation citizenAllocation;

    @Mock
    private CitizenAllocationModel model;

    @Test
    void getGameItemType() {
        assertThat(underTest.getGameItemType()).isEqualTo(GameItemType.CITIZEN_ALLOCATION);
    }

    @Test
    void getArrayClass() {
        assertThat(underTest.getArrayClass()).isEqualTo(CitizenAllocationModel[].class);
    }

    @Test
    void addToGameData() {
        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);

        underTest.addToGameData(gameData, List.of(citizenAllocation));

        verify(citizenAllocations).addAll(List.of(citizenAllocation));
    }

    @Test
    void convert() {
        given(model.getId()).willReturn(CITIZEN_ALLOCATION_ID);
        given(model.getCitizenId()).willReturn(CITIZEN_ID);
        given(model.getProcessId()).willReturn(PROCESS_ID);

        CitizenAllocation result = underTest.convert(model);

        assertThat(result.getCitizenAllocationId()).isEqualTo(CITIZEN_ALLOCATION_ID);
        assertThat(result.getCitizenId()).isEqualTo(CITIZEN_ID);
        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
    }
}