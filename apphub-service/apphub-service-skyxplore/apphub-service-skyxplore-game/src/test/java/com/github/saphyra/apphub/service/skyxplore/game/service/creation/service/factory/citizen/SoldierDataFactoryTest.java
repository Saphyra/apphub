package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.citizen;

import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.SoldierData;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.GameCreationProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SoldierDataFactoryTest {
    private static final Integer HIT_POINTS = 324;

    @Mock
    private GameCreationProperties properties;

    @InjectMocks
    private SoldierDataFactory underTest;

    @Mock
    private GameCreationProperties.CitizenProperties citizenProperties;

    @Test
    public void create() {
        given(properties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getHitPointsPerStamina()).willReturn(HIT_POINTS);

        SoldierData result = underTest.create();

        assertThat(result.getMaxHitPoints()).isEqualTo(HIT_POINTS);
        assertThat(result.getCurrentHitPoints()).isEqualTo(HIT_POINTS);
        assertThat(result.getArmor()).isEqualTo(new ConcurrentHashMap<>());
        assertThat(result.getEnergyShield()).isNull();
        assertThat(result.getWeaponDataId()).isNull();
    }
}