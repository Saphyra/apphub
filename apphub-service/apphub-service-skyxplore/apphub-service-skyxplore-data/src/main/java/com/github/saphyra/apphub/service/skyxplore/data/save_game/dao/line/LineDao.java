package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.line;

import com.github.saphyra.apphub.api.skyxplore.model.game.LineModel;
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
public class LineDao extends AbstractDao<LineEntity, LineModel, String, LineRepository> {
    private final UuidConverter uuidConverter;

    public LineDao(LineConverter converter, LineRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }

    public Optional<LineModel> findById(UUID lineId) {
        return findById(uuidConverter.convertDomain(lineId));
    }

    public List<LineModel> getByReferenceId(UUID referenceId) {
        return converter.convertEntity(repository.getByReferenceId(uuidConverter.convertDomain(referenceId)));
    }

    public void deleteById(UUID lineId) {
        deleteById(uuidConverter.convertDomain(lineId));
    }

    public List<LineModel> getPageByGameId(UUID gameId, Integer page, Integer itemsPerPage) {
        return converter.convertEntity(repository.getByGameId(uuidConverter.convertDomain(gameId), PageRequest.of(page, itemsPerPage)));
    }
}
