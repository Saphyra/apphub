package com.github.saphyra.apphub.service.custom.villany_atesz.contacts.service;

import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao.ContactDao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class DeleteContactService {
    private final ContactDao contactDao;

    @Transactional
    public void delete(UUID userId, UUID contactId) {
        contactDao.deleteByUserIdAndContactId(userId, contactId);

        //TODO delete cart
    }
}
