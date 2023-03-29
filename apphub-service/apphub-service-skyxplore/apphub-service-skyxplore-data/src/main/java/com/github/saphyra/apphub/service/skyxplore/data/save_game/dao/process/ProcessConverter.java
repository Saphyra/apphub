package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.process;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.StringStringMap;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessConverter extends ConverterBase<ProcessEntity, ProcessModel> {
    private final UuidConverter uuidConverter;
    private final ObjectMapperWrapper objectMapperWrapper;

    @Override
    protected ProcessModel processEntityConversion(ProcessEntity entity) {
        ProcessModel model = new ProcessModel();
        model.setId(uuidConverter.convertEntity(entity.getProcessId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.PROCESS);
        model.setProcessType(ProcessType.valueOf(entity.getProcessType()));
        model.setStatus(ProcessStatus.valueOf(entity.getStatus()));
        model.setLocation(uuidConverter.convertEntity(entity.getLocation()));
        model.setExternalReference(uuidConverter.convertEntity(entity.getExternalReference()));
        model.setData(objectMapperWrapper.readValue(entity.getData(), StringStringMap.class));
        return model;
    }

    @Override
    protected ProcessEntity processDomainConversion(ProcessModel domain) {
        return ProcessEntity.builder()
            .processId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .processType(domain.getProcessType().name())
            .status(domain.getStatus().name())
            .location(uuidConverter.convertDomain(domain.getLocation()))
            .externalReference(uuidConverter.convertDomain(domain.getExternalReference()))
            .data(objectMapperWrapper.writeValueAsString(domain.getData()))
            .build();
    }
}
