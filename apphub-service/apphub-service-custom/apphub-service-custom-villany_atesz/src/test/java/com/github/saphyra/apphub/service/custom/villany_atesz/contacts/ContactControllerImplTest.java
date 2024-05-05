package com.github.saphyra.apphub.service.custom.villany_atesz.contacts;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.ContactModel;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.service.ContactQueryService;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.service.CreateContactService;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.service.DeleteContactService;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.service.EditContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ContactControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CONTACT_ID = UUID.randomUUID();

    @Mock
    private ContactQueryService contactQueryService;

    @Mock
    private CreateContactService createContactService;

    @Mock
    private EditContactService editContactService;

    @Mock
    private DeleteContactService deleteContactService;

    @InjectMocks
    private ContactControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private ContactModel contactModel;

    @BeforeEach
    void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    void getContacts() {
        given(contactQueryService.getContacts(USER_ID)).willReturn(List.of(contactModel));

        assertThat(underTest.getContacts(accessTokenHeader)).containsExactly(contactModel);
    }

    @Test
    void createContact() {
        given(contactQueryService.getContacts(USER_ID)).willReturn(List.of(contactModel));

        assertThat(underTest.createContact(contactModel, accessTokenHeader)).containsExactly(contactModel);

        then(createContactService).should().create(USER_ID, contactModel);
    }

    @Test
    void editContact() {
        given(contactQueryService.getContacts(USER_ID)).willReturn(List.of(contactModel));

        assertThat(underTest.editContact(contactModel, CONTACT_ID, accessTokenHeader)).containsExactly(contactModel);

        then(editContactService).should().edit(CONTACT_ID, contactModel);
    }

    @Test
    void deleteContact() {
        given(contactQueryService.getContacts(USER_ID)).willReturn(List.of(contactModel));

        assertThat(underTest.deleteContact(CONTACT_ID, accessTokenHeader)).containsExactly(contactModel);

        then(deleteContactService).should().delete(USER_ID, CONTACT_ID);
    }
}