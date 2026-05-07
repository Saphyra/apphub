import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./index_page_localization.json";
import "./index_page.css";
import { useSearchParams } from "react-router-dom";
import { useEffect, useState } from "react";
import NotificationService from "common/js/notification/NotificationService";
import ErrorHandler from "common/js/dao/ErrorHandler";
import Constants from "common/js/Constants";
import Header from "common/component/Header";
import LoginForm from "./index_page/LoginForm";
import RegistrationForm from "./index_page/RegistrationForm";
import Footer from "common/component/Footer";
import LanguageSelector from "common/component/language_selector/LanguageSelector";
import { ToastContainer } from "react-toastify";
import { CHECK_SESSION } from "common/js/GenericEndpoints";
import { MODULES_PAGE } from "../modules/ModulesEndpoints";
import ResponseStatus from "common/js/dao/ResponseStatus";
import Spinner from "common/component/Spinner";

const IndexPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [searchParams, setSearchParams] = useSearchParams();

    const [displaySpinner, setDisplaySpinner] = useState(false);

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

            const location = searchParams.get("redirect") || MODULES_PAGE;

            window.location.href = location;
        }
        queryAndRedirect();
    }

    return (
        <div className="main-page">
            <Header label={localizationHandler.get("title")} />

            <main>
                <LoginForm
                    localizationHandler={localizationHandler}
                    setDisplaySpinner={setDisplaySpinner}
                />

                <RegistrationForm
                    localizationHandler={localizationHandler}
                    setDisplaySpinner={setDisplaySpinner}
                />
            </main>

            <Footer
                centerButtons={<LanguageSelector
                    currentLanguage={localizationHandler.getLocale()}
                    updateCallback={(locale) => {
                        localStorage[Constants.STORAGE_KEY_LOCALE] = locale;
                        window.location.reload();
                    }}
                />}
            />

            <ToastContainer />
            {displaySpinner && <Spinner />}
        </div>
    );
}

export default IndexPage;
