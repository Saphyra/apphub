package com.github.saphyra.apphub.service.notebook.dao.text;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

interface TextRepository extends CrudRepository<TextEntity, String> {
    void deleteByParent(String parent);

    Optional<TextEntity> findByParent(String parent);
}
