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
class CreateContactServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private ContactValidator contactValidator;

    @Mock
    private ContactFactory contactFactory;

    @Mock
    private ContactDao contactDao;

    @InjectMocks
    private CreateContactService underTest;

    @Mock
    private ContactModel request;

    @Mock
    private Contact contact;

    @Test
    void create() {
        given(contactFactory.create(USER_ID, request)).willReturn(contact);

        underTest.create(USER_ID, request);

        then(contactValidator).should().validate(request);
        then(contactDao).should().save(contact);
    }
}