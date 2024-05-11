package com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
class ContactRepositoryTest {
    private static final String CONTACT_ID_1 = "contact-id-1";
    private static final String CONTACT_ID_2 = "contact-id-2";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";

    @Autowired
    private ContactRepository underTest;

    @AfterEach
    void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    void deleteByUserId() {
        ContactEntity entity1 = ContactEntity.builder()
            .contactId(CONTACT_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        ContactEntity entity2 = ContactEntity.builder()
            .contactId(CONTACT_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserId(USER_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    void getByUserId() {
        ContactEntity entity1 = ContactEntity.builder()
            .contactId(CONTACT_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        ContactEntity entity2 = ContactEntity.builder()
            .contactId(CONTACT_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        assertThat(underTest.getByUserId(USER_ID_1)).containsExactly(entity1);
    }

    @Test
    @Transactional
    void deleteByUserIdAndContactId() {
        ContactEntity entity1 = ContactEntity.builder()
            .contactId(CONTACT_ID_1)
            .userId(USER_ID_1)
            .build();
        underTest.save(entity1);

        ContactEntity entity2 = ContactEntity.builder()
            .contactId(CONTACT_ID_2)
            .userId(USER_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByUserIdAndContactId(USER_ID_1, CONTACT_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }
}