package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class GameDataItemValidatorTest {
    @InjectMocks
    private GameDataItemValidator underTest;

    @Mock
    private GameDataItem gameDataItem;

    @Test
    public void nullId() {
        given(gameDataItem.getId()).willReturn(null);

        assertThat(catchThrowable(() -> underTest.validate(gameDataItem))).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void valid() {
        given(gameDataItem.getId()).willReturn("id");

        underTest.validate(gameDataItem);
    }
}