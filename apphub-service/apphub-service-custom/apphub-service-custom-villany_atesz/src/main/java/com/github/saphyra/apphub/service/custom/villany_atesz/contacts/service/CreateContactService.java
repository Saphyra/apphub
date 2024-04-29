package com.github.saphyra.apphub.service.custom.villany_atesz.contacts.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.ContactModel;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao.Contact;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao.ContactDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CreateContactService {
    private final ContactValidator contactValidator;
    private final ContactFactory contactFactory;
    private final ContactDao contactDao;

    public void create(UUID userId, ContactModel request) {
        contactValidator.validate(request);

        Contact contact = contactFactory.create(userId, request);

        contactDao.save(contact);
    }
}
