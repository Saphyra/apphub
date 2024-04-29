package com.github.saphyra.apphub.service.custom.villany_atesz.contacts;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.ContactModel;
import com.github.saphyra.apphub.api.custom.villany_atesz.server.ContactController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.service.ContactQueryService;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.service.CreateContactService;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.service.DeleteContactService;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.service.EditContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

//TODO unit test
@RestController
@RequiredArgsConstructor
@Slf4j
class ContactControllerImpl implements ContactController {
    private final ContactQueryService contactQueryService;
    private final CreateContactService createContactService;
    private final EditContactService editContactService;
    private final DeleteContactService deleteContactService;

    @Override
    public List<ContactModel> createContact(ContactModel request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a new contact.", accessTokenHeader.getUserId());

        createContactService.create(accessTokenHeader.getUserId(), request);

        return getContacts(accessTokenHeader);
    }

    @Override
    public List<ContactModel> editContact(ContactModel request, UUID contactId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit contact {}", accessTokenHeader.getUserId(), contactId);

        editContactService.edit(contactId, request);

        return getContacts(accessTokenHeader);
    }

    @Override
    public List<ContactModel> deleteContact(UUID contactId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete contact {}", accessTokenHeader.getUserId(), contactId);

        deleteContactService.delete(accessTokenHeader.getUserId(), contactId);

        return getContacts(accessTokenHeader);
    }

    @Override
    public List<ContactModel> getContacts(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know his  contacts", accessTokenHeader.getUserId());

        return contactQueryService.getContacts(accessTokenHeader.getUserId());
    }
}
