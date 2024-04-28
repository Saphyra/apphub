package com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
//TODO unit test
class ContactConverter extends ConverterBase<ContactEntity, Contact> {
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_CODE = "code";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_NOTE = "note";

    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;
    private final AccessTokenProvider accessTokenProvider;

    @Override
    protected ContactEntity processDomainConversion(Contact domain) {
        String userId = accessTokenProvider.getUserIdAsString();

        String contactId = uuidConverter.convertDomain(domain.getContactId());
        return ContactEntity.builder()
            .contactId(contactId)
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .name(stringEncryptor.encrypt(domain.getName(), userId, contactId, COLUMN_NAME))
            .code(stringEncryptor.encrypt(domain.getCode(), userId, contactId, COLUMN_CODE))
            .phone(stringEncryptor.encrypt(domain.getPhone(), userId, contactId, COLUMN_PHONE))
            .address(stringEncryptor.encrypt(domain.getAddress(), userId, contactId, COLUMN_ADDRESS))
            .note(stringEncryptor.encrypt(domain.getNote(), userId, contactId, COLUMN_NOTE))
            .build();
    }

    @Override
    protected Contact processEntityConversion(ContactEntity entity) {
        String userId = accessTokenProvider.getUserIdAsString();

        return Contact.builder()
            .contactId(uuidConverter.convertEntity(entity.getContactId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .name(stringEncryptor.decrypt(entity.getName(), userId, entity.getContactId(), COLUMN_NAME))
            .code(stringEncryptor.decrypt(entity.getCode(), userId, entity.getContactId(), COLUMN_CODE))
            .phone(stringEncryptor.decrypt(entity.getPhone(), userId, entity.getContactId(), COLUMN_PHONE))
            .address(stringEncryptor.decrypt(entity.getAddress(), userId, entity.getContactId(), COLUMN_ADDRESS))
            .note(stringEncryptor.decrypt(entity.getNote(), userId, entity.getContactId(), COLUMN_NOTE))
            .build();
    }
}
