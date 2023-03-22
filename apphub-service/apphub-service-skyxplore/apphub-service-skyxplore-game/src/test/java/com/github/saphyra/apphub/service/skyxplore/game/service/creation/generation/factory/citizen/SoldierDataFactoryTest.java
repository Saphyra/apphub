package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.citizen;

import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenHitPointsProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.soldier_data.SoldierWeapon;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.population.SoldierDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SoldierDataFactoryTest {
    private static final Integer HIT_POINTS = 324;

    @Mock
    private GameProperties properties;

    @InjectMocks
    private SoldierDataFactory underTest;

    @Mock
    private CitizenProperties citizenProperties;

    @Mock
    private CitizenHitPointsProperties hitPointsProperties;

    @Test
    public void create() {
        given(properties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getHitPoints()).willReturn(hitPointsProperties);
        given(hitPointsProperties.getPerStaminaLevel()).willReturn(HIT_POINTS);

        SoldierWeapon result = underTest.create();

        assertThat(result.getMaxHitPoints()).isEqualTo(HIT_POINTS);
        assertThat(result.getCurrentHitPoints()).isEqualTo(HIT_POINTS);
        assertThat(result.getArmor()).isEqualTo(new ConcurrentHashMap<>());
        assertThat(result.getEnergyShield()).isNull();
        assertThat(result.getRangedWeaponDataId()).isNull();
    }
}