package com.github.saphyra.apphub.service.platform.encryption.encryption_key.dao;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface EncryptionKeyRepository extends CrudRepository<EncryptionKeyEntity, EncryptionKeyPk> {
    List<EncryptionKeyEntity> getByUserId(String userUd);
}
