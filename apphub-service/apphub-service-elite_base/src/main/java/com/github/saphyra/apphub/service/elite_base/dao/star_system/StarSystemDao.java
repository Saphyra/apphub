package com.github.saphyra.apphub.service.elite_base.dao.star_system;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
//TODO unit test
public class StarSystemDao extends AbstractDao<StarSystemEntity, StarSystem, String, StarSystemRepository> {
    public StarSystemDao(StarSystemConverter converter, StarSystemRepository repository) {
        super(converter, repository);
    }

    public Optional<StarSystem> findByStarId(Long starId) {
        return converter.convertEntity(repository.findByStarId(starId));
    }

    public Optional<StarSystem> findByStarName(String starName) {
        return converter.convertEntity(repository.findByStarName(starName));
    }
}
