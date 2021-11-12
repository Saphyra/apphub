package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;


import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityItemModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class DurabilityItemLoaderTest {
    private static final UUID PARENT = UUID.randomUUID();

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private DurabilityItemLoader underTest;

    @Mock
    private DurabilityItemModel durabilityItemModel;

    @Test
    public void load() {
        given(gameItemLoader.loadChildren(PARENT, GameItemType.DURABILITY_ITEM_MODEL, DurabilityItemModel[].class)).willReturn(Arrays.asList(durabilityItemModel));

        List<DurabilityItemModel> result = underTest.load(PARENT);

        assertThat(result).containsExactly(durabilityItemModel);
    }
}