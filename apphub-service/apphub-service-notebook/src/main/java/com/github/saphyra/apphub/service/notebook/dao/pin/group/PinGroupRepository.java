package com.github.saphyra.apphub.service.notebook.dao.pin.group;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

//TODO unit test
interface PinGroupRepository extends CrudRepository<PinGroupEntity, String> {
    void deleteByUserId(String userId);

    List<PinGroupEntity> getByUserId(String userId);
}
