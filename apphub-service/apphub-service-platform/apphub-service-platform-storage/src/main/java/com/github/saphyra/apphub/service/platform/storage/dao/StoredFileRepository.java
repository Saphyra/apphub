package com.github.saphyra.apphub.service.platform.storage.dao;

import org.springframework.data.repository.CrudRepository;

interface StoredFileRepository extends CrudRepository<StoredFileEntity, String> {
}
