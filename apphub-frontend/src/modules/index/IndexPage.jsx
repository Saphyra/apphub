import React, { useEffect } from "react";
import "./index_page.css";
import LocalizationHandler from "../../common/js/LocalizationHandler";
import localizationData from "./index_page_localization.json";
import LoginForm from "./index_page/LoginForm";
import Header from "../../common/component/Header";
import RegistrationForm from "./index_page/RegistrationForm";
import { ToastContainer } from "react-toastify";
import NotificationService from "../../common/js/notification/NotificationService";
import { ResponseStatus } from "../../common/js/dao/dao";
import ErrorHandler from "../../common/js/dao/ErrorHandler";
import { useSearchParams } from "react-router-dom";
import Constants from "../../common/js/Constants";
import Footer from "../../common/component/Footer";
import LanguageSelector from "../../common/component/language_selector/LanguageSelector";
import { setCookie } from "../../common/js/Utils";
import { CHECK_SESSION } from "../../common/js/dao/endpoints/GenericEndpoints";

const IndexPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [searchParams, setSearchParams] = useSearchParams();

    useEffect(() => NotificationService.displayStoredMessages(), null);
    useEffect(() => redirectIfLoggedIn(), [searchParams]);

    document.title = localizationHandler.get("title");

    const redirectIfLoggedIn = () => {
        const queryAndRedirect = async () => {
            await CHECK_SESSION.createRequest()
                .addErrorHandler(new ErrorHandler(
                    (response) => response.statusKey === ResponseStatus.UNAUTHORIZED,
                    (response) => console.log("User is not logged in.")
                ))
                .send();

            const location = searchParams.get("redirect") || Constants.MODULES_PAGE;

            window.location.href = location;
        }
        queryAndRedirect();
    }

    return (
        <div className="main-page">
            <Header label={localizationHandler.get("title")} />

            <main>
                <LoginForm localizationHandler={localizationHandler} />

                <RegistrationForm localizationHandler={localizationHandler} />
            </main>

            <Footer
                centerButtons={<LanguageSelector
                    currentLanguage={localizationHandler.getLocale()}
                    updateCallback={(locale) => {
                        setCookie(Constants.COOKIE_LOCALE, locale);
                        window.location.reload();
                    }}
                />}
            />

            <ToastContainer />
        </div>
    );
}

export default IndexPage;
