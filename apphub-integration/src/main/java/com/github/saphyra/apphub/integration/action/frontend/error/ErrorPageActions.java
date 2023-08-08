package com.github.saphyra.apphub.integration.action.frontend.error;

import com.github.saphyra.apphub.integration.structure.api.ErrorMessageElement;
import org.openqa.selenium.WebDriver;

public class ErrorPageActions {
    public static ErrorMessageElement getErrorMessage(WebDriver driver) {
        return new ErrorMessageElement(ErrorPage.errorMessageRoot(driver));
    }
}
