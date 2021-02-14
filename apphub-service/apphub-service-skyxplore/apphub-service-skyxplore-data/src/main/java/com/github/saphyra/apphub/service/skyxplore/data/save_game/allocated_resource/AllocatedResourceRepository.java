package com.github.saphyra.apphub.service.skyxplore.data.save_game.allocated_resource;

import org.springframework.data.repository.CrudRepository;

//TODO unit test
interface AllocatedResourceRepository extends CrudRepository<AllocatedResourceEntity, String> {
    void deleteByGameId(String gameId);
}
