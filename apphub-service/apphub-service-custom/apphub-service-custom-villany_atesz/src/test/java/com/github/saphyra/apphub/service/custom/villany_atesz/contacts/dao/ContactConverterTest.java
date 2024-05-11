package com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao.ContactConverter.COLUMN_ADDRESS;
import static com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao.ContactConverter.COLUMN_CODE;
import static com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao.ContactConverter.COLUMN_NAME;
import static com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao.ContactConverter.COLUMN_NOTE;
import static com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao.ContactConverter.COLUMN_PHONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ContactConverterTest {
    private static final UUID CONTACT_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final String CODE = "code";
    private static final String PHONE = "phone";
    private static final String ADDRESS = "address";
    private static final String NOTE = "note";
    private static final String CONTACT_ID_STRING = "contact-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String USER_ID_FROM_ACCESS_TOKEN = "user-id-from-access-token";
    private static final String ENCRYPTED_NAME = "encrypted-name";
    private static final String ENCRYPTED_CODE = "encrypted-code";
    private static final String ENCRYPTED_PHONE = "encrypted-phone";
    private static final String ENCRYPTED_ADDRESS = "encrypted-address";
    private static final String ENCRYPTED_NOTE = "encrypted-note";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @InjectMocks
    private ContactConverter underTest;

    @Test
    void convertDomain() {
        Contact domain = Contact.builder()
            .contactId(CONTACT_ID)
            .userId(USER_ID)
            .name(NAME)
            .code(CODE)
            .phone(PHONE)
            .address(ADDRESS)
            .note(NOTE)
            .build();

        given(uuidConverter.convertDomain(CONTACT_ID)).willReturn(CONTACT_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(stringEncryptor.encrypt(NAME, USER_ID_FROM_ACCESS_TOKEN, CONTACT_ID_STRING, COLUMN_NAME)).willReturn(ENCRYPTED_NAME);
        given(stringEncryptor.encrypt(CODE, USER_ID_FROM_ACCESS_TOKEN, CONTACT_ID_STRING, COLUMN_CODE)).willReturn(ENCRYPTED_CODE);
        given(stringEncryptor.encrypt(PHONE, USER_ID_FROM_ACCESS_TOKEN, CONTACT_ID_STRING, COLUMN_PHONE)).willReturn(ENCRYPTED_PHONE);
        given(stringEncryptor.encrypt(ADDRESS, USER_ID_FROM_ACCESS_TOKEN, CONTACT_ID_STRING, COLUMN_ADDRESS)).willReturn(ENCRYPTED_ADDRESS);
        given(stringEncryptor.encrypt(NOTE, USER_ID_FROM_ACCESS_TOKEN, CONTACT_ID_STRING, COLUMN_NOTE)).willReturn(ENCRYPTED_NOTE);

        assertThat(underTest.convertDomain(domain))
            .returns(CONTACT_ID_STRING, ContactEntity::getContactId)
            .returns(USER_ID_STRING, ContactEntity::getUserId)
            .returns(ENCRYPTED_NAME, ContactEntity::getName)
            .returns(ENCRYPTED_CODE, ContactEntity::getCode)
            .returns(ENCRYPTED_PHONE, ContactEntity::getPhone)
            .returns(ENCRYPTED_ADDRESS, ContactEntity::getAddress)
            .returns(ENCRYPTED_NOTE, ContactEntity::getNote);
    }

    @Test
    void convertEntity() {
        ContactEntity entity = ContactEntity.builder()
            .contactId(CONTACT_ID_STRING)
            .userId(USER_ID_STRING)
            .name(ENCRYPTED_NAME)
            .code(ENCRYPTED_CODE)
            .phone(ENCRYPTED_PHONE)
            .address(ENCRYPTED_ADDRESS)
            .note(ENCRYPTED_NOTE)
            .build();

        given(uuidConverter.convertEntity(CONTACT_ID_STRING)).willReturn(CONTACT_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(stringEncryptor.decrypt(ENCRYPTED_NAME, USER_ID_FROM_ACCESS_TOKEN, CONTACT_ID_STRING, COLUMN_NAME)).willReturn(NAME);
        given(stringEncryptor.decrypt(ENCRYPTED_CODE, USER_ID_FROM_ACCESS_TOKEN, CONTACT_ID_STRING, COLUMN_CODE)).willReturn(CODE);
        given(stringEncryptor.decrypt(ENCRYPTED_PHONE, USER_ID_FROM_ACCESS_TOKEN, CONTACT_ID_STRING, COLUMN_PHONE)).willReturn(PHONE);
        given(stringEncryptor.decrypt(ENCRYPTED_ADDRESS, USER_ID_FROM_ACCESS_TOKEN, CONTACT_ID_STRING, COLUMN_ADDRESS)).willReturn(ADDRESS);
        given(stringEncryptor.decrypt(ENCRYPTED_NOTE, USER_ID_FROM_ACCESS_TOKEN, CONTACT_ID_STRING, COLUMN_NOTE)).willReturn(NOTE);

        assertThat(underTest.convertEntity(entity))
            .returns(CONTACT_ID, Contact::getContactId)
            .returns(USER_ID, Contact::getUserId)
            .returns(NAME, Contact::getName)
            .returns(CODE, Contact::getCode)
            .returns(PHONE, Contact::getPhone)
            .returns(ADDRESS, Contact::getAddress)
            .returns(NOTE, Contact::getNote);
    }
}