package com.github.saphyra.apphub.integration.action.backend.villany_atesz;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.VillanyAteszEndpoints;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.AcquisitionResponse;
import io.restassured.response.Response;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class VillanyAteszAcquisitionActions {
    public static List<LocalDate> getDates(int serverPort, UUID accessTokenId) {
        Response response = getDatesResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(String[].class))
            .map(LocalDate::parse)
            .collect(Collectors.toList());
    }

    public static Response getDatesResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, VillanyAteszEndpoints.VILLANY_ATESZ_GET_ACQUISITION_DATES));
    }

    public static List<AcquisitionResponse> getAcquisitionsOnDay(int serverPort, UUID accessTokenId, LocalDate date) {
        Response response = getAcquisitionsOnDayResponse(serverPort, accessTokenId, date);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(AcquisitionResponse[].class));
    }

    public static Response getAcquisitionsOnDayResponse(int serverPort, UUID accessTokenId, LocalDate date) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, VillanyAteszEndpoints.VILLANY_ATESZ_GET_ACQUISITIONS, "acquiredAt", date));
    }
}
