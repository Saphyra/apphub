package com.github.saphyra.apphub.integration.action.backend.villany_atesz;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.AcquisitionResponse;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.AcquisitionHistoryItem;
import io.restassured.response.Response;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class VillanyAteszAcquisitionActions {
    public static List<LocalDate> getDates(UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.VILLANY_ATESZ_GET_ACQUISITION_DATES));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(String[].class))
            .map(LocalDate::parse)
            .collect(Collectors.toList());
    }

    public static List<AcquisitionResponse> getAcquisitionsOnDay(UUID accessTokenId, LocalDate date) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.VILLANY_ATESZ_GET_ACQUISITIONS, "acquiredAt", date));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(AcquisitionResponse[].class));
    }

    public static void openHistory(WebDriver driver) {
        driver.findElement(By.id("villany-atesz-stock-acquisition-show-history-button"))
            .click();
    }

    public static List<AcquisitionHistoryItem> getHistoryItems(WebDriver driver) {
        return driver.findElements(By.className("villany-atesz-stock-acquisition-history-item"))
            .stream()
            .map(AcquisitionHistoryItem::new)
            .collect(Collectors.toList());
    }
}
