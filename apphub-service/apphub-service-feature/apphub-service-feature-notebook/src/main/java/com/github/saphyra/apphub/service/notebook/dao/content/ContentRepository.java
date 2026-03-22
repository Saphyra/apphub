package com.github.saphyra.apphub.service.notebook.dao.content;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

interface ContentRepository extends CrudRepository<ContentEntity, String> {
    void deleteByParent(String parent);

    Optional<ContentEntity> findByParent(String parent);

    void deleteByUserId(String userId);

    List<ContentEntity> getByUserId(String userId);
}
