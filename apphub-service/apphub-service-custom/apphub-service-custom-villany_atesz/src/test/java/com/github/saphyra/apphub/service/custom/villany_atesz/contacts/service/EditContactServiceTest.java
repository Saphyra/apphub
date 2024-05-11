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

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EditContactServiceTest {
    private static final UUID CONTACT_ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final String CODE = "code";
    private static final String PHONE = "phone";
    private static final String ADDRESS = "address";
    private static final String NOTE = "note";

    @Mock
    private ContactValidator contactValidator;

    @Mock
    private ContactDao contactDao;

    @InjectMocks
    private EditContactService underTest;

    @Mock
    private Contact contact;

    @Test
    void edit() {
        ContactModel request = ContactModel.builder()
            .name(NAME)
            .code(CODE)
            .phone(PHONE)
            .address(ADDRESS)
            .note(NOTE)
            .build();

        given(contactDao.findByIdValidated(CONTACT_ID)).willReturn(contact);

        underTest.edit(CONTACT_ID, request);

        then(contact).should().setName(NAME);
        then(contact).should().setCode(CODE);
        then(contact).should().setPhone(PHONE);
        then(contact).should().setAddress(ADDRESS);
        then(contact).should().setNote(NOTE);

        then(contactDao).should().save(contact);
        then(contactValidator).should().validate(request);
    }
}