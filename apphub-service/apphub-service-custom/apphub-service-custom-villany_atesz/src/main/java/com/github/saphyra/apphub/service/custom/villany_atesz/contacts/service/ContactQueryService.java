package com.github.saphyra.apphub.service.custom.villany_atesz.contacts.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.ContactModel;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao.Contact;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao.ContactDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContactQueryService {
    private final ContactDao contactDao;

    public List<ContactModel> getContacts(UUID userId) {
        return contactDao.getByUserId(userId)
            .stream()
            .map(this::convert)
            .collect(Collectors.toList());
    }

    public ContactModel getContact(UUID contactId) {
        return convert(contactDao.findByIdValidated(contactId));
    }

    private ContactModel convert(Contact contact) {
        return ContactModel.builder()
            .contactId(contact.getContactId())
            .name(contact.getName())
            .code(contact.getCode())
            .phone(contact.getPhone())
            .address(contact.getAddress())
            .note(contact.getNote())
            .build();
    }
}
