package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlanetModel;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class AbstractGameItemLoaderTest {
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private TestGameItemLoader underTest;

    @Mock
    private PlanetModel model;

    @Test
    void getByGameId() {
        given(gameItemLoader.loadPageForGame(GAME_ID, 0, GameItemType.PLANET, PlanetModel[].class)).willReturn(List.of(model));
        given(gameItemLoader.loadPageForGame(GAME_ID, 1, GameItemType.PLANET, PlanetModel[].class)).willReturn(Collections.emptyList());

        List<PlanetModel> result = underTest.getByGameId(GAME_ID);

        assertThat(result).containsExactly(model);
    }

    static class TestGameItemLoader extends AbstractGameItemLoader<PlanetModel> {

        public TestGameItemLoader(GameItemLoader gameItemLoader) {
            super(gameItemLoader);
        }

        @Override
        protected GameItemType getGameItemType() {
            return GameItemType.PLANET;
        }

        @Override
        protected Class<PlanetModel[]> getArrayClass() {
            return PlanetModel[].class;
        }
    }
}