package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class GameDataItemValidatorTest {
    @InjectMocks
    private GameDataItemValidator underTest;

    @Mock
    private GameDataItem gameDataItem;

    @Test(expected = NullPointerException.class)
    public void nullId() {
        given(gameDataItem.getId()).willReturn(null);

        underTest.validate(gameDataItem);
    }

    @Test
    public void valid() {
        given(gameDataItem.getId()).willReturn("id");

        underTest.validate(gameDataItem);
    }
}