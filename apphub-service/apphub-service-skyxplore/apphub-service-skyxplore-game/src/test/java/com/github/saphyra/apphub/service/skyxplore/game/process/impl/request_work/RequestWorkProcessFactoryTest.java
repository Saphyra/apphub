package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessParamKeys;
import com.github.saphyra.apphub.test.common.ReflectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class RequestWorkProcessFactoryTest {
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String BUILDING_DATA_ID = "building-data-id";
    private static final int REQUIRED_WORK_POINTS = 32;
    private static final String TARGET_ID_STRING = "target-id";
    private static final UUID TARGET_ID = UUID.randomUUID();
    private static final int CYCLE = 245;
    private static final int COMPLETED_WORK_POINTS = 246;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private RequestWorkProcessFactory underTest;

    @Mock
    private Game game;

    @Mock
    private Universe universe;

    @Mock
    private Planet planet;

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.REQUEST_WORK);
    }

    @Test
    public void create() throws NoSuchFieldException, IllegalAccessException {
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);

        List<RequestWorkProcess> result = underTest.create(game, EXTERNAL_REFERENCE, planet, TARGET_ID, RequestWorkProcessType.CONSTRUCTION, SkillType.DOCTORING, REQUIRED_WORK_POINTS, 1);

        assertThat(result).hasSize(1);

        RequestWorkProcess process = result.get(0);

        assertThat(process.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(process.getStatus()).isEqualTo(ProcessStatus.CREATED);
        assertThat(process.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat((Game) ReflectionUtils.getFieldValue(process, "game")).isEqualTo(game);
        assertThat((Planet) ReflectionUtils.getFieldValue(process, "planet")).isEqualTo(planet);
        assertThat((String) ReflectionUtils.getFieldValue(process, "buildingDataId")).isNull();
        assertThat((SkillType) ReflectionUtils.getFieldValue(process, "skillType")).isEqualTo(SkillType.DOCTORING);
        assertThat((Integer) ReflectionUtils.getFieldValue(process, "requiredWorkPoints")).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat((UUID) ReflectionUtils.getFieldValue(process, "targetId")).isEqualTo(TARGET_ID);
        assertThat((RequestWorkProcessType) ReflectionUtils.getFieldValue(process, "requestWorkProcessType")).isEqualTo(RequestWorkProcessType.CONSTRUCTION);
        assertThat((ApplicationContextProxy) ReflectionUtils.getFieldValue(process, "applicationContextProxy")).isEqualTo(applicationContextProxy);
        assertThat((Integer) ReflectionUtils.getFieldValue(process, "cycle")).isEqualTo(0);
        assertThat((Integer) ReflectionUtils.getFieldValue(process, "completedWorkPoints")).isEqualTo(0);
    }

    @Test
    public void createFromModel() throws NoSuchFieldException, IllegalAccessException {
        ProcessModel model = new ProcessModel();
        model.setId(PROCESS_ID);
        model.setLocation(PLANET_ID);
        model.setExternalReference(EXTERNAL_REFERENCE);
        model.setStatus(ProcessStatus.IN_PROGRESS);
        model.setData(CollectionUtils.toMap(
            new BiWrapper<>(ProcessParamKeys.BUILDING_DATA_ID, BUILDING_DATA_ID),
            new BiWrapper<>(ProcessParamKeys.SKILL_TYPE, SkillType.DOCTORING.name()),
            new BiWrapper<>(ProcessParamKeys.REQUIRED_WORK_POINTS, String.valueOf(REQUIRED_WORK_POINTS)),
            new BiWrapper<>(ProcessParamKeys.REQUEST_WORK_PROCESS_TYPE, RequestWorkProcessType.CONSTRUCTION.name()),
            new BiWrapper<>(ProcessParamKeys.TARGET_ID, TARGET_ID_STRING),
            new BiWrapper<>(ProcessParamKeys.CYCLE, String.valueOf(CYCLE)),
            new BiWrapper<>(ProcessParamKeys.COMPLETED_WORK_POINTS, String.valueOf(COMPLETED_WORK_POINTS))
        ));

        given(game.getUniverse()).willReturn(universe);
        given(universe.findPlanetByIdValidated(PLANET_ID)).willReturn(planet);
        given(uuidConverter.convertEntity(TARGET_ID_STRING)).willReturn(TARGET_ID);

        RequestWorkProcess result = underTest.createFromModel(game, model);

        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat((Game) ReflectionUtils.getFieldValue(result, "game")).isEqualTo(game);
        assertThat((Planet) ReflectionUtils.getFieldValue(result, "planet")).isEqualTo(planet);
        assertThat((String) ReflectionUtils.getFieldValue(result, "buildingDataId")).isEqualTo(BUILDING_DATA_ID);
        assertThat((SkillType) ReflectionUtils.getFieldValue(result, "skillType")).isEqualTo(SkillType.DOCTORING);
        assertThat((Integer) ReflectionUtils.getFieldValue(result, "requiredWorkPoints")).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat((UUID) ReflectionUtils.getFieldValue(result, "targetId")).isEqualTo(TARGET_ID);
        assertThat((RequestWorkProcessType) ReflectionUtils.getFieldValue(result, "requestWorkProcessType")).isEqualTo(RequestWorkProcessType.CONSTRUCTION);
        assertThat((ApplicationContextProxy) ReflectionUtils.getFieldValue(result, "applicationContextProxy")).isEqualTo(applicationContextProxy);
        assertThat((Integer) ReflectionUtils.getFieldValue(result, "cycle")).isEqualTo(CYCLE);
        assertThat((Integer) ReflectionUtils.getFieldValue(result, "completedWorkPoints")).isEqualTo(COMPLETED_WORK_POINTS);
    }
}