package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.process;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.StringStringMap;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ProcessConverterTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String LOCATION_TYPE = "location-type";
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String KEY = "key";
    private static final String VALUE = "value";
    private static final String PROCESS_ID_STRING = "process-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String LOCATION_STRING = "location";
    private static final String EXTERNAL_REFERENCE_STRING = "external-reference";
    private static final String DATA_STRING = "data";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @InjectMocks
    private ProcessConverter underTest;

    @Test
    public void convertDomain() {
        ProcessModel model = new ProcessModel();
        model.setId(PROCESS_ID);
        model.setGameId(GAME_ID);
        model.setProcessType(ProcessType.PRODUCTION_ORDER);
        model.setStatus(ProcessStatus.IN_PROGRESS);
        model.setLocation(LOCATION);
        model.setLocationType(LOCATION_TYPE);
        model.setExternalReference(EXTERNAL_REFERENCE);
        model.setData(CollectionUtils.singleValueMap(KEY, VALUE));

        given(uuidConverter.convertDomain(PROCESS_ID)).willReturn(PROCESS_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(LOCATION)).willReturn(LOCATION_STRING);
        given(uuidConverter.convertDomain(EXTERNAL_REFERENCE)).willReturn(EXTERNAL_REFERENCE_STRING);

        given(objectMapperWrapper.writeValueAsString(CollectionUtils.singleValueMap(KEY, VALUE))).willReturn(DATA_STRING);

        ProcessEntity result = underTest.convertDomain(model);

        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID_STRING);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getProcessType()).isEqualTo(ProcessType.PRODUCTION_ORDER.name());
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS.name());
        assertThat(result.getLocation()).isEqualTo(LOCATION_STRING);
        assertThat(result.getLocationType()).isEqualTo(LOCATION_TYPE);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE_STRING);
        assertThat(result.getData()).isEqualTo(DATA_STRING);
    }

    @Test
    public void convertEntity() {
        ProcessEntity entity = new ProcessEntity();
        entity.setProcessId(PROCESS_ID_STRING);
        entity.setGameId(GAME_ID_STRING);
        entity.setProcessType(ProcessType.PRODUCTION_ORDER.name());
        entity.setStatus(ProcessStatus.IN_PROGRESS.name());
        entity.setLocation(LOCATION_STRING);
        entity.setLocationType(LOCATION_TYPE);
        entity.setExternalReference(EXTERNAL_REFERENCE_STRING);
        entity.setData(DATA_STRING);

        given(uuidConverter.convertEntity(PROCESS_ID_STRING)).willReturn(PROCESS_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(LOCATION_STRING)).willReturn(LOCATION);
        given(uuidConverter.convertEntity(EXTERNAL_REFERENCE_STRING)).willReturn(EXTERNAL_REFERENCE);

        given(objectMapperWrapper.readValue(DATA_STRING, StringStringMap.class)).willReturn(CollectionUtils.singleValueMap(KEY, VALUE, new StringStringMap()));

        ProcessModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(PROCESS_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.PROCESS);
        assertThat(result.getProcessType()).isEqualTo(ProcessType.PRODUCTION_ORDER);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getLocationType()).isEqualTo(LOCATION_TYPE);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getData()).containsEntry(KEY, VALUE);
    }
}