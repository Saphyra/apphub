package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AutoLoaderTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private TestAutoLoader underTest;

    @Mock
    private GameData gameData;

    private final Constructions constructions = new Constructions();

    @Mock
    private ConstructionModel constructionModel;

    @Test
    void autoLoad() {
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(gameData.getConstructions()).willReturn(constructions);
        given(gameItemLoader.loadPageForGame(GAME_ID, 0, GameItemType.CONSTRUCTION, ConstructionModel[].class)).willReturn(List.of(constructionModel));
        given(gameItemLoader.loadPageForGame(GAME_ID, 1, GameItemType.CONSTRUCTION, ConstructionModel[].class)).willReturn(Collections.emptyList());
        given(constructionModel.getId()).willReturn(CONSTRUCTION_ID);

        underTest.autoLoad(gameData);

        assertThat(constructions).hasSize(1);
        assertThat(constructions.get(0).getConstructionId()).isEqualTo(CONSTRUCTION_ID);
    }

    static class TestAutoLoader extends AutoLoader<ConstructionModel, Construction> {
        public TestAutoLoader(GameItemLoader gameItemLoader) {
            super(gameItemLoader);
        }

        @Override
        protected GameItemType getGameItemType() {
            return GameItemType.CONSTRUCTION;
        }

        @Override
        protected Class<ConstructionModel[]> getArrayClass() {
            return ConstructionModel[].class;
        }

        @Override
        protected void addToGameData(GameData gameData, List<Construction> items) {
            gameData.getConstructions()
                .addAll(items);
        }

        @Override
        protected Construction convert(ConstructionModel constructionModel) {
            Construction construction = Mockito.mock(Construction.class);
            UUID constructionId = constructionModel.getId();
            given(construction.getConstructionId()).willReturn(constructionId);
            return construction;
        }
    }
}