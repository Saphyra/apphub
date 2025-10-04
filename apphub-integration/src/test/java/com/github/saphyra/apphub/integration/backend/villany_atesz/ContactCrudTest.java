package com.github.saphyra.apphub.integration.backend.villany_atesz;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszCartActions;
import com.github.saphyra.apphub.integration.action.backend.villany_atesz.VillanyAteszContactActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.CustomAssertions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ContactModel;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactCrudTest extends BackEndTest {
    private static final String CODE = "code";
    private static final String PHONE = "phone";
    private static final String ADDRESS = "address";
    private static final String NOTE = "note";
    private static final String NAME = "name";
    private static final String NEW_CODE = "new-code";
    private static final String NEW_PHONE = "new-phone";
    private static final String NEW_ADDRESS = "new-address";
    private static final String NEW_NOTE = "new-note";
    private static final String NEW_NAME = "new-name";

    @Test(groups = {"be", "villany-atesz"})
    public void contactCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_VILLANY_ATESZ);

        createContact_blankName(accessTokenId);
        createContact_nullCode(accessTokenId);
        createContact_nullPhone(accessTokenId);
        createContact_nullAddress(accessTokenId);
        createContact_nullNote(accessTokenId);
        createContact(accessTokenId);

        UUID contactId = gteContactId(accessTokenId);

        editContact_blankName(accessTokenId, contactId);
        editContact_nullCode(accessTokenId, contactId);
        editContact_nullPhone(accessTokenId, contactId);
        editContact_nullAddress(accessTokenId, contactId);
        editContact_nullNote(accessTokenId, contactId);
        editContact(accessTokenId, contactId);

        deleteContact(accessTokenId, contactId);
    }

    private UUID gteContactId(UUID accessTokenId) {
        return VillanyAteszContactActions.getContacts(getServerPort(), accessTokenId)
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("User has no contact created."))
            .getContactId();
    }

    private void createContact_blankName(UUID accessTokenId) {
        ContactModel request = ContactModel.builder()
            .name(" ")
            .code(CODE)
            .phone(PHONE)
            .address(ADDRESS)
            .note(NOTE)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszContactActions.getCreateContactResponse(getServerPort(), accessTokenId, request), "name", "must not be null or blank");
    }

    private void createContact_nullCode(UUID accessTokenId) {
        ContactModel request = ContactModel.builder()
            .name(NAME)
            .code(null)
            .phone(PHONE)
            .address(ADDRESS)
            .note(NOTE)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszContactActions.getCreateContactResponse(getServerPort(), accessTokenId, request), "code", "must not be null");
    }

    private void createContact_nullPhone(UUID accessTokenId) {
        ContactModel request = ContactModel.builder()
            .name(NAME)
            .code(CODE)
            .phone(null)
            .address(ADDRESS)
            .note(NOTE)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszContactActions.getCreateContactResponse(getServerPort(), accessTokenId, request), "phone", "must not be null");
    }

    private void createContact_nullAddress(UUID accessTokenId) {
        ContactModel request = ContactModel.builder()
            .name(NAME)
            .code(CODE)
            .phone(PHONE)
            .address(null)
            .note(NOTE)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszContactActions.getCreateContactResponse(getServerPort(), accessTokenId, request), "address", "must not be null");
    }

    private void createContact_nullNote(UUID accessTokenId) {
        ContactModel request = ContactModel.builder()
            .name(NAME)
            .code(CODE)
            .phone(PHONE)
            .address(ADDRESS)
            .note(null)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszContactActions.getCreateContactResponse(getServerPort(), accessTokenId, request), "note", "must not be null");
    }

    private void createContact(UUID accessTokenId) {
        ContactModel request = ContactModel.builder()
            .name(NAME)
            .code(CODE)
            .phone(PHONE)
            .address(ADDRESS)
            .note(NOTE)
            .build();

        CustomAssertions.singleListAssertThat(VillanyAteszContactActions.createContact(getServerPort(), accessTokenId, request))
            .returns(NAME, ContactModel::getName)
            .returns(CODE, ContactModel::getCode)
            .returns(PHONE, ContactModel::getPhone)
            .returns(ADDRESS, ContactModel::getAddress)
            .returns(NOTE, ContactModel::getNote);
    }

    private void editContact_blankName(UUID accessTokenId, UUID contactId) {
        ContactModel request = ContactModel.builder()
            .name(" ")
            .code(NEW_CODE)
            .phone(NEW_PHONE)
            .address(NEW_ADDRESS)
            .note(NEW_NOTE)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszContactActions.getEditContactResponse(getServerPort(), accessTokenId, contactId, request), "name", "must not be null or blank");
    }

    private void editContact_nullCode(UUID accessTokenId, UUID contactId) {
        ContactModel request = ContactModel.builder()
            .name(NEW_NAME)
            .code(null)
            .phone(NEW_PHONE)
            .address(NEW_ADDRESS)
            .note(NEW_NOTE)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszContactActions.getEditContactResponse(getServerPort(), accessTokenId, contactId, request), "code", "must not be null");
    }

    private void editContact_nullPhone(UUID accessTokenId, UUID contactId) {
        ContactModel request = ContactModel.builder()
            .name(NEW_NAME)
            .code(NEW_CODE)
            .phone(null)
            .address(NEW_ADDRESS)
            .note(NEW_NOTE)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszContactActions.getEditContactResponse(getServerPort(), accessTokenId, contactId, request), "phone", "must not be null");
    }

    private void editContact_nullAddress(UUID accessTokenId, UUID contactId) {
        ContactModel request = ContactModel.builder()
            .name(NEW_NAME)
            .code(NEW_CODE)
            .phone(NEW_PHONE)
            .address(null)
            .note(NEW_NOTE)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszContactActions.getEditContactResponse(getServerPort(), accessTokenId, contactId, request), "address", "must not be null");
    }

    private void editContact_nullNote(UUID accessTokenId, UUID contactId) {
        ContactModel request = ContactModel.builder()
            .name(NEW_NAME)
            .code(NEW_CODE)
            .phone(NEW_PHONE)
            .address(NEW_ADDRESS)
            .note(null)
            .build();

        ResponseValidator.verifyInvalidParam(VillanyAteszContactActions.getEditContactResponse(getServerPort(), accessTokenId, contactId, request), "note", "must not be null");
    }

    private void editContact(UUID accessTokenId, UUID contactId) {
        ContactModel request = ContactModel.builder()
            .name(NEW_NAME)
            .code(NEW_CODE)
            .phone(NEW_PHONE)
            .address(NEW_ADDRESS)
            .note(NEW_NOTE)
            .build();

        CustomAssertions.singleListAssertThat(VillanyAteszContactActions.editContact(getServerPort(), accessTokenId, contactId, request))
            .returns(contactId, ContactModel::getContactId)
            .returns(NEW_NAME, ContactModel::getName)
            .returns(NEW_CODE, ContactModel::getCode)
            .returns(NEW_PHONE, ContactModel::getPhone)
            .returns(NEW_ADDRESS, ContactModel::getAddress)
            .returns(NEW_NOTE, ContactModel::getNote);
    }

    private void deleteContact(UUID accessTokenId, UUID contactId) {
        VillanyAteszCartActions.createCart(getServerPort(), accessTokenId, contactId);

        assertThat(VillanyAteszContactActions.deleteContact(getServerPort(), accessTokenId, contactId)).isEmpty();

        assertThat(VillanyAteszCartActions.getCarts(getServerPort(), accessTokenId)).isEmpty();
    }
}
