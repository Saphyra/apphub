package com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ContactDao extends AbstractDao<ContactEntity, Contact, String, ContactRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    ContactDao(ContactConverter converter, ContactRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public List<Contact> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    public Contact findByIdValidated(UUID contactId) {
        return findById(uuidConverter.convertDomain(contactId))
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Contact not found by id " + contactId));
    }

    public void deleteByUserIdAndContactId(UUID userId, UUID contactId) {
        repository.deleteByUserIdAndContactId(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(contactId));
    }
}
