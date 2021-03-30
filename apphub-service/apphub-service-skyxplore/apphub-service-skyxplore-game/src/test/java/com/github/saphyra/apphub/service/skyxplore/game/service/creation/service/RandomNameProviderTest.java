package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.FirstNames;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.LastNames;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class RandomNameProviderTest {
    @Mock
    private FirstNames firstNames;

    @Mock
    private LastNames lastNames;

    @InjectMocks
    private RandomNameProvider underTest;

    @Test
    public void getRandomName() {
        given(firstNames.getRandom()).willReturn("A");
        given(lastNames.getRandom()).willReturn("B")
            .willReturn("C");

        String result = underTest.getRandomName(Arrays.asList("B A"));

        assertThat(result).isEqualTo("C A");
    }
}