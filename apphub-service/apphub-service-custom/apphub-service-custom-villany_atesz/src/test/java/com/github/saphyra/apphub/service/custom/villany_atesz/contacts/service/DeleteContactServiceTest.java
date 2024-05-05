package com.github.saphyra.apphub.service.custom.villany_atesz.contacts.service;

import com.github.saphyra.apphub.service.custom.villany_atesz.cart.service.DeleteCartService;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao.ContactDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DeleteContactServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CONTACT_ID = UUID.randomUUID();
    @Mock
    private ContactDao contactDao;

    @Mock
    private DeleteCartService deleteCartService;

    @InjectMocks
    private DeleteContactService underTest;

    @Test
    void delete() {
        underTest.delete(USER_ID, CONTACT_ID);

        then(contactDao).should().deleteByUserIdAndContactId(USER_ID, CONTACT_ID);
        then(deleteCartService).should().deleteForContact(USER_ID, CONTACT_ID);
    }
}