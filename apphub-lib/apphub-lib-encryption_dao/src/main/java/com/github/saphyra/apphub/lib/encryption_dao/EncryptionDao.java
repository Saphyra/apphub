package com.github.saphyra.apphub.lib.encryption_dao;

import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
//TODO unit test
public abstract class EncryptionDao<ENTITY extends EncryptedEntity, DOMAIN, REPOSITORY extends CrudRepository<ENTITY, String>> {
    protected final EncryptionConverter<ENTITY, DOMAIN> converter;
    protected final REPOSITORY repository;
    protected final EncryptionProxy encryptionProxy;
    protected final DataType dataType;
    protected final UuidConverter uuidConverter;

    public void delete(DOMAIN domain) {
        ENTITY entity = converter.convertDomain(domain);
        repository.delete(entity);
        encryptionProxy.deleteEncryptionKey(uuidConverter.convertEntity(entity.getId()), dataType);
    }

    public void deleteById(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            encryptionProxy.deleteEncryptionKey(uuidConverter.convertEntity(id), dataType);
        } else {
            log.debug("No record to delete with id " + id);
        }
    }

    public Optional<DOMAIN> findById(String id) {
        return converter.convertEntity(repository.findById(id));
    }

    public void save(DOMAIN domain) {
        repository.save(converter.convertDomain(domain));
    }

    public void saveAll(List<DOMAIN> domains) {
        repository.saveAll(converter.convertDomain(domains));
    }
}
