package com.github.saphyra.apphub.service.custom.villany_atesz.contacts.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.ContactModel;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;

class ContactValidatorTest {
    private static final String CODE = "code";
    private static final String PHONE = "phone";
    private static final String ADDRESS = "address";
    private static final String NOTE = "note";
    private static final String NAME = "name";

    private final ContactValidator underTest = new ContactValidator();

    @Test
    void blankName() {
        ContactModel request = ContactModel.builder()
            .name(" ")
            .code(CODE)
            .phone(PHONE)
            .address(ADDRESS)
            .note(NOTE)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "name", "must not be null or blank");
    }

    @Test
    void nullCode() {
        ContactModel request = ContactModel.builder()
            .name(NAME)
            .code(null)
            .phone(PHONE)
            .address(ADDRESS)
            .note(NOTE)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "code", "must not be null");
    }

    @Test
    void nullPhone() {
        ContactModel request = ContactModel.builder()
            .name(NAME)
            .code(CODE)
            .phone(null)
            .address(ADDRESS)
            .note(NOTE)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "phone", "must not be null");
    }

    @Test
    void nullAddress() {
        ContactModel request = ContactModel.builder()
            .name(NAME)
            .code(CODE)
            .phone(PHONE)
            .address(null)
            .note(NOTE)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "address", "must not be null");
    }

    @Test
    void nullNote() {
        ContactModel request = ContactModel.builder()
            .name(NAME)
            .code(CODE)
            .phone(PHONE)
            .address(ADDRESS)
            .note(null)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "note", "must not be null");
    }

    @Test
    void valid() {
        ContactModel request = ContactModel.builder()
            .name(NAME)
            .code(CODE)
            .phone(PHONE)
            .address(ADDRESS)
            .note(NOTE)
            .build();

        underTest.validate(request);
    }
}