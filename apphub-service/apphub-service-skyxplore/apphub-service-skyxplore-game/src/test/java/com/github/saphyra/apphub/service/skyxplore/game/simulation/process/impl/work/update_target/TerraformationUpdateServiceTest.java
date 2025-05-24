package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.update_target;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TerraformationUpdateServiceTest {
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final int COMPLETED_WORK_POINTS = 245;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private ConstructionConverter constructionConverter;

    @InjectMocks
    private TerraformationUpdateService underTest;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private GameData gameData;

    @Mock
    private Constructions constructions;

    @Mock
    private Construction terraformation;

    @Mock
    private ConstructionModel constructionModel;

    @Test
    void updateConstruction() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByIdValidated(CONSTRUCTION_ID)).willReturn(terraformation);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(constructionConverter.toModel(GAME_ID, terraformation)).willReturn(constructionModel);

        underTest.updateTerraformation(progressDiff, gameData, CONSTRUCTION_ID, COMPLETED_WORK_POINTS);

        then(progressDiff).should().save(constructionModel);
    }
}