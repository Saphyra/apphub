package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.skill;

import com.github.saphyra.apphub.api.skyxplore.model.game.SkillModel;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

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
}
