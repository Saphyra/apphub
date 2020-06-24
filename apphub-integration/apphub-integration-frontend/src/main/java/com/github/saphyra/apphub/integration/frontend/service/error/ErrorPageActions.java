package com.github.saphyra.apphub.integration.frontend.service.error;

import com.github.saphyra.apphub.integration.frontend.model.error.ErrorMessageElement;
import org.openqa.selenium.WebDriver;

public class ErrorPageActions {
    public static ErrorMessageElement getErrorMessage(WebDriver driver) {
        return new ErrorMessageElement(ErrorPage.errorMessageRoot(driver));
    }
}
