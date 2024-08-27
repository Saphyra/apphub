package com.github.saphyra.apphub.integration.action.backend.villany_atesz;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ContactModel;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class VillanyAteszContactActions {
    public static Response getCreateContactResponse(int serverPort, UUID accessTokenId, ContactModel request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_CREATE_CONTACT));
    }

    public static List<ContactModel> createContact(int serverPort, UUID accessTokenId, ContactModel request) {
        Response response = getCreateContactResponse(serverPort, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(ContactModel[].class));
    }

    public static List<ContactModel> getContacts(int serverPort, UUID accessTokenId) {
        Response response = getContactsResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(ContactModel[].class));
    }

    public static Response getContactsResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_GET_CONTACTS));
    }

    public static Response getEditContactResponse(int serverPort, UUID accessTokenId, UUID contactId, ContactModel request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_EDIT_CONTACT, "contactId", contactId));
    }

    public static List<ContactModel> editContact(int serverPort, UUID accessTokenId, UUID contactId, ContactModel request) {
        Response response = getEditContactResponse(serverPort, accessTokenId, contactId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(ContactModel[].class));
    }

    public static List<ContactModel> deleteContact(int serverPort, UUID accessTokenId, UUID contactId) {
        Response response = getDeleteContactResponse(serverPort, accessTokenId, contactId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(ContactModel[].class));
    }

    public static Response getDeleteContactResponse(int serverPort, UUID accessTokenId, UUID contactId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_DELETE_CONTACT, "contactId", contactId));
    }
}
