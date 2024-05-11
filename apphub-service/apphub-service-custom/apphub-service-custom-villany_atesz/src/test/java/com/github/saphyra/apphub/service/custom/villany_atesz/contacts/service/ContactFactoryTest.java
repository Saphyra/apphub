package com.github.saphyra.apphub.service.custom.villany_atesz.contacts.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.ContactModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao.Contact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ContactFactoryTest {
    private static final UUID CONTACT_ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final String CODE = "code";
    private static final String PHONE = "phone";
    private static final String ADDRESS = "address";
    private static final String NOTE = "note";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private ContactFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(CONTACT_ID);

        ContactModel request = ContactModel.builder()
            .name(NAME)
            .code(CODE)
            .phone(PHONE)
            .address(ADDRESS)
            .note(NOTE)
            .build();

        assertThat(underTest.create(USER_ID, request))
            .returns(CONTACT_ID, Contact::getContactId)
            .returns(USER_ID, Contact::getUserId)
            .returns(NAME, Contact::getName)
            .returns(CODE, Contact::getCode)
            .returns(PHONE, Contact::getPhone)
            .returns(ADDRESS, Contact::getAddress)
            .returns(NOTE, Contact::getNote);
    }
}