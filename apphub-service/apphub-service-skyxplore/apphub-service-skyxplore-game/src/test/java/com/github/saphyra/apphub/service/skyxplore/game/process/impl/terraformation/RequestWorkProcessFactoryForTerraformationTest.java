package com.github.saphyra.apphub.service.skyxplore.game.process.impl.terraformation;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibilities;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibilitiesService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibility;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcessType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class RequestWorkProcessFactoryForTerraformationTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 3124;
    private static final Integer PARALLEL_WORKERS = 234;
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();

    @Mock
    private TerraformingPossibilitiesService terraformingPossibilitiesService;

    @Mock
    private RequestWorkProcessFactory requestWorkProcessFactory;

    @InjectMocks
    private RequestWorkProcessFactoryForTerraformation underTest;

    @Mock
    private Game game;

    @Mock
    private Planet planet;

    @Mock
    private Surface surface;

    @Mock
    private Construction terraformation;

    @Mock
    private TerraformingPossibility terraformingPossibility;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @Mock
    private RequestWorkProcess requestWorkProcess;

    @Test
    public void createRequestWorkProcess() {
        given(surface.getTerraformation()).willReturn(terraformation);
        given(terraformation.getData()).willReturn(SurfaceType.DESERT.name());
        given(surface.getSurfaceType()).willReturn(SurfaceType.CONCRETE);
        given(terraformation.getConstructionId()).willReturn(CONSTRUCTION_ID);

        given(terraformingPossibilitiesService.getOptional(SurfaceType.CONCRETE)).willReturn(Optional.of(CollectionUtils.toList(new TerraformingPossibilities(), terraformingPossibility)));
        given(terraformingPossibility.getSurfaceType()).willReturn(SurfaceType.DESERT);
        given(terraformingPossibility.getConstructionRequirements()).willReturn(constructionRequirements);
        given(constructionRequirements.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(constructionRequirements.getParallelWorkers()).willReturn(PARALLEL_WORKERS);

        given(requestWorkProcessFactory.create(game, PROCESS_ID, planet, CONSTRUCTION_ID, RequestWorkProcessType.TERRAFORMATION, SkillType.BUILDING, REQUIRED_WORK_POINTS, PARALLEL_WORKERS))
            .willReturn(List.of(requestWorkProcess));

        List<RequestWorkProcess> result = underTest.createRequestWorkProcesses(PROCESS_ID, game, planet, surface);

        assertThat(result).containsExactly(requestWorkProcess);
    }
}