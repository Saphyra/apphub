package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizens;
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
class CitizenLoaderTest {
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String NAME = "name";
    private static final Integer MORALE = 346;
    private static final Integer SATIETY = 5687;

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private CitizenLoader underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Citizen citizen;

    @Mock
    private CitizenModel model;

    @Mock
    private Citizens citizens;

    @Test
    void getGameItemType() {
        assertThat(underTest.getGameItemType()).isEqualTo(GameItemType.CITIZEN);
    }

    @Test
    void getArrayClass() {
        assertThat(underTest.getArrayClass()).isEqualTo(CitizenModel[].class);
    }

    @Test
    void addToGameModel() {
        given(gameData.getCitizens()).willReturn(citizens);

        underTest.addToGameData(gameData, List.of(citizen));

        verify(citizens).addAll(List.of(citizen));
    }

    @Test
    void convert() {
        given(model.getId()).willReturn(CITIZEN_ID);
        given(model.getLocation()).willReturn(LOCATION);
        given(model.getName()).willReturn(NAME);
        given(model.getMorale()).willReturn(MORALE);
        given(model.getSatiety()).willReturn(SATIETY);

        Citizen result = underTest.convert(model);

        assertThat(result.getCitizenId()).isEqualTo(CITIZEN_ID);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getName()).isEqualTo(NAME);
        assertThat(result.getMorale()).isEqualTo(MORALE);
        assertThat(result.getSatiety()).isEqualTo(SATIETY);
    }
}