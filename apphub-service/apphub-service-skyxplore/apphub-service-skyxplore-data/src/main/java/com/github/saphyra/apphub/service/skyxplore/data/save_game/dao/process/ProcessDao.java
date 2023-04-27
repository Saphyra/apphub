package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.process;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class ProcessDao extends AbstractDao<ProcessEntity, ProcessModel, String, ProcessRepository> {
    private final UuidConverter uuidConverter;

    public ProcessDao(ProcessConverter converter, ProcessRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }

    public Optional<ProcessModel> findById(UUID productionOrderId) {
        return findById(uuidConverter.convertDomain(productionOrderId));
    }

    public List<ProcessModel> getByGameId(UUID gameId) {
        return converter.convertEntity(repository.getByGameId(uuidConverter.convertDomain(gameId)));
    }

    public void deleteById(UUID productionOrderId) {
        deleteById(uuidConverter.convertDomain(productionOrderId));
    }

    public List<ProcessModel> getPageByGameId(UUID gameId, Integer page, Integer itemsPerPage) {
        return converter.convertEntity(repository.getByGameId(uuidConverter.convertDomain(gameId), PageRequest.of(page, itemsPerPage)));
    }
}
