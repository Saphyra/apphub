package com.github.saphyra.apphub.service.custom.villany_atesz.contacts.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.ContactModel;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao.Contact;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao.ContactDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ContactQueryServiceTest {
    private static final UUID CONTACT_ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final String CODE = "code";
    private static final String PHONE = "phone";
    private static final String ADDRESS = "address";
    private static final String NOTE = "note";

    @Mock
    private ContactDao contactDao;

    @InjectMocks
    private ContactQueryService underTest;

    @Test
    void getContact() {
        Contact contact = Contact.builder()
            .contactId(CONTACT_ID)
            .name(NAME)
            .code(CODE)
            .phone(PHONE)
            .address(ADDRESS)
            .note(NOTE)
            .build();

        given(contactDao.findByIdValidated(CONTACT_ID)).willReturn(contact);

        assertThat(underTest.getContact(CONTACT_ID))
            .returns(CONTACT_ID, ContactModel::getContactId)
            .returns(NAME, ContactModel::getName)
            .returns(CODE, ContactModel::getCode)
            .returns(PHONE, ContactModel::getPhone)
            .returns(ADDRESS, ContactModel::getAddress)
            .returns(NOTE, ContactModel::getNote);
    }
}