package com.github.saphyra.apphub.service.notebook.dao.image;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

//TODO unit test
interface ImageRepository extends CrudRepository<ImageEntity, String> {
    Optional<ImageEntity> findByParent(String parent);

    void deleteByUserId(String userId);
}
