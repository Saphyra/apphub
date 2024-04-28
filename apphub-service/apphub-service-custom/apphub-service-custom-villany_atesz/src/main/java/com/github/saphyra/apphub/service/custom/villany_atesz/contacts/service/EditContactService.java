package com.github.saphyra.apphub.service.custom.villany_atesz.contacts.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.contact.ContactModel;
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
public class EditContactService {
    private final ContactValidator contactValidator;
    private final ContactDao contactDao;

    public void edit(UUID contactId, ContactModel request) {
        contactValidator.validate(request);

        Contact contact = contactDao.findByIdValidated(contactId);

        contact.setName(request.getName());
        contact.setCode(request.getCode());
        contact.setPhone(request.getPhone());
        contact.setAddress(request.getAddress());
        contact.setName(request.getName());

        contactDao.save(contact);
    }
}
