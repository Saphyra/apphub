package com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ContactDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID CONTACT_ID = UUID.randomUUID();
    private static final String CONTACT_ID_STRING = "contact-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ContactConverter converter;

    @Mock
    private ContactRepository repository;

    @InjectMocks
    private ContactDao underTest;

    @Mock
    private Contact domain;

    @Mock
    private ContactEntity entity;

    @Test
    void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        then(repository).should().deleteByUserId(USER_ID_STRING);
    }

    @Test
    void getByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByUserId(USER_ID)).containsExactly(domain);
    }

    @Test
    void findByIdValidated() {
        given(uuidConverter.convertDomain(CONTACT_ID)).willReturn(CONTACT_ID_STRING);
        given(repository.findById(CONTACT_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByIdValidated(CONTACT_ID)).isEqualTo(domain);
    }

    @Test
    void findByIdValidated_notFound() {
        given(uuidConverter.convertDomain(CONTACT_ID)).willReturn(CONTACT_ID_STRING);
        given(repository.findById(CONTACT_ID_STRING)).willReturn(Optional.empty());

        ExceptionValidator.validateNotLoggedException(catchThrowable(() -> underTest.findByIdValidated(CONTACT_ID)), HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void deleteByUserIdAndContactId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(CONTACT_ID)).willReturn(CONTACT_ID_STRING);

        underTest.deleteByUserIdAndContactId(USER_ID, CONTACT_ID);

        then(repository).should().deleteByUserIdAndContactId(USER_ID_STRING, CONTACT_ID_STRING);
    }
}