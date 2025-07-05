package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.skill;

import com.github.saphyra.apphub.api.skyxplore.model.game.SkillModel;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class SkillDao extends AbstractDao<SkillEntity, SkillModel, String, SkillRepository> {
    private final UuidConverter uuidConverter;

    public SkillDao(SkillConverter converter, SkillRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }

    public Optional<SkillModel> findById(UUID skillId) {
        return findById(uuidConverter.convertDomain(skillId));
    }

    public List<SkillModel> getByCitizenId(UUID citizenId) {
        return converter.convertEntity(repository.getByCitizenId(uuidConverter.convertDomain(citizenId)));
    }

    public void deleteById(UUID skillId) {
        deleteById(uuidConverter.convertDomain(skillId));
    }

    public List<SkillModel> getPageByGameId(UUID gameId, Integer page, Integer itemsPerPage) {
        return converter.convertEntity(repository.getByGameId(uuidConverter.convertDomain(gameId), PageRequest.of(page, itemsPerPage)));
    }
}
