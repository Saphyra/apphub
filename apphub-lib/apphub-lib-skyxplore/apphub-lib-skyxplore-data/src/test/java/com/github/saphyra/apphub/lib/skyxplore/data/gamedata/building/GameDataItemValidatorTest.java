package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
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