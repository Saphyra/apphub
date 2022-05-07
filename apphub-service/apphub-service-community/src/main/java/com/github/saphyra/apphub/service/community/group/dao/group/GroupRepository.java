package com.github.saphyra.apphub.service.community.group.dao.group;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface GroupRepository extends CrudRepository<GroupEntity, String> {
    List<GroupEntity> getByOwnerId(String ownerId);
}
