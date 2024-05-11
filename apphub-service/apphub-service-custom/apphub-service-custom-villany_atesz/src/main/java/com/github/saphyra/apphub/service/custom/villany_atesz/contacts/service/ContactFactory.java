package com.github.saphyra.apphub.service.custom.villany_atesz.contacts.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.ContactModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao.Contact;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ContactFactory {
    private final IdGenerator idGenerator;

    Contact create(UUID userId, ContactModel request) {
        return Contact.builder()
            .contactId(idGenerator.randomUuid())
            .userId(userId)
            .name(request.getName())
            .code(request.getCode())
            .phone(request.getPhone())
            .address(request.getAddress())
            .note(request.getNote())
            .build();

    }
}
