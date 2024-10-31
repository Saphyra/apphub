package com.github.saphyra.apphub.service.skyxplore.data.game_data;

import com.github.saphyra.apphub.api.skyxplore.response.game.citizen.CitizenStat;
import com.github.saphyra.apphub.api.skyxplore.response.game.citizen.CitizenStatsAndSkills;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.data.AbstractDataService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItem;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibilities;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibilitiesService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibility;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class GameDataControllerImplTest {
    private static final String DATA_ID = "data-id";
    private static final String RESOURCE_DATA_ID = "resource-data-id";

    private final AbstractDataService<String, GameDataItem> dataService = new DummyDataService();

    @Mock
    private TerraformingPossibilitiesService terraformingPossibilitiesService;

    @Mock
    private ResourceDataService resourceDataService;

    private GameDataControllerImpl underTest;

    @Mock
    private GameDataItem gameDataItem;

    @Mock
    private TerraformingPossibility terraformingPossibility;

    @BeforeEach
    public void setUp() {
        given(gameDataItem.getId()).willReturn(DATA_ID);
        dataService.put(DATA_ID, gameDataItem);
        given(resourceDataService.keySet()).willReturn(Set.of(RESOURCE_DATA_ID));

        underTest = new GameDataControllerImpl(List.of(dataService), terraformingPossibilitiesService, resourceDataService);
    }

    @Test
    public void dataNotFound() {
        Throwable ex = catchThrowable(() -> underTest.getGameData("asd"));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void dataFound() {
        Object result = underTest.getGameData(DATA_ID);

        assertThat(result).isEqualTo(gameDataItem);
    }

    @Test
    public void getTerraformingPossibilities_surfaceTypeCannotBeTerraformed() {
        given(terraformingPossibilitiesService.getOptional(SurfaceType.CONCRETE)).willReturn(Optional.empty());

        List<Object> result = underTest.getTerraformingPossibilities(SurfaceType.CONCRETE.name());

        assertThat(result).isEmpty();
    }

    @Test
    public void getTerraformingPossibilities() {
        given(terraformingPossibilitiesService.getOptional(SurfaceType.CONCRETE)).willReturn(Optional.of(new TerraformingPossibilities(List.of(terraformingPossibility))));

        List<Object> result = underTest.getTerraformingPossibilities(SurfaceType.CONCRETE.name());

        assertThat(result).containsExactly(terraformingPossibility);
    }

    @Test
    void getStatsAndSkills() {
        CitizenStatsAndSkills result = underTest.getStatsAndSkills();

        assertThat(new HashSet<>(result.getSkills())).hasSize(SkillType.values().length);
        assertThat(new HashSet<>(result.getStats())).hasSize(CitizenStat.values().length);
    }

    @Test
    void getResourceDataIds() {
        assertThat(underTest.getResourceDataIds()).containsExactly(RESOURCE_DATA_ID);
    }

    private static class DummyDataService extends AbstractDataService<String, GameDataItem> {

        public DummyDataService() {
            super(null, null);
        }

        @Override
        public void init() {

        }

        @Override
        public void addItem(GameDataItem content, String fileName) {

        }
    }
}