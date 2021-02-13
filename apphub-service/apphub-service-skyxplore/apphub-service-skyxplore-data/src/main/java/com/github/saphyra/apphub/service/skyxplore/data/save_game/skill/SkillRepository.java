package com.github.saphyra.apphub.service.skyxplore.data.save_game.skill;

import org.springframework.data.repository.CrudRepository;

//TODO unit test
interface SkillRepository extends CrudRepository<SkillEntity, String> {
    void deleteByGameId(String gameId);
}
