import React, { useEffect, useState } from "react";
import Header from "../../common/component/Header";
import localizationData from "./error_page_localization.json";
import LocalizationHandler from "../../common/js/LocalizationHandler";
import Footer from "../../common/component/Footer";
import Button from "../../common/component/input/Button";
import Constants from "../../common/js/Constants";
import { useSearchParams } from "react-router-dom";
import errorCodeLocalizationData from "../../common/js/notification/notification_translations.json";
import logout from "../../common/js/LogoutController";
import { ResponseStatus } from "../../common/js/dao/dao";
import ErrorHandler from "../../common/js/dao/ErrorHandler";
import Optional from "../../common/js/collection/Optional";
import LocalDateTime from "../../common/js/date/LocalDateTime";
import "./error_page.css";
import useLoader from "../../common/hook/Loader";
import { hasValue } from "../../common/js/Utils";
import { ACCOUNT_BAN_GET_DETAILS_FOR_ERROR_PAGE } from "../../common/js/dao/endpoints/UserEndpoints";
import { CHECK_SESSION } from "../../common/js/dao/endpoints/GenericEndpoints";

const ErrorPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const ererorCodeLocalizationHandler = new LocalizationHandler(errorCodeLocalizationData);
    document.title = localizationHandler.get("page-title");

    const [searchParams] = useSearchParams();

    const [errorCode, setErrorCode] = useState("UNKNOWN_ERROR");
    const [userLoggedIn, setUserLoggedIn] = useState(false);
    const [userId, setUserId] = useState(null);
    const [requiredRoles, setRequiredRoles] = useState(null);
    const [bannedDetails, setBannedDetails] = useState(null);

    useEffect(() => setErrorCode(searchParams.get("error_code") || "UNKNOWN_ERROR"), [searchParams]);
    useEffect(() => setUserId(searchParams.get("user_id")), [searchParams]);
    useEffect(() => setRequiredRoles(new Optional(searchParams.get("required_roles")).map(s => s.split(",")).orElse(null)), [searchParams]);
    useEffect(() => checkUserLoggedIn(), []);

    useLoader({
        request: ACCOUNT_BAN_GET_DETAILS_FOR_ERROR_PAGE.createRequest({ userId: userId, requiredRoles: requiredRoles }),
        mapper: setBannedDetails,
        listener: [userId, requiredRoles],
        condition: () => hasValue(userId) && hasValue(requiredRoles)
    });

    const checkUserLoggedIn = () => {
        const fetch = async () => {
            await CHECK_SESSION.createRequest()
                .addErrorHandler(new ErrorHandler(
                    response => response.status === ResponseStatus.UNAUTHORIZED,
                    () => setUserLoggedIn(false)
                ))
                .send();

            setUserLoggedIn(true);
        }
        fetch();
    }

    const getLogoutButton = () => {
        if (!userLoggedIn) {
            return null;
        }

        return <Button
            id="error-logout-button"
            label={localizationHandler.get("logout")}
            onclick={() => logout()}
        />
    }

    const getErrorDetails = () => {
        if (!hasValue(userId) || !hasValue(requiredRoles) || !hasValue(bannedDetails) || !hasValue(bannedDetails.permanent)) {
            return;
        }

        if (bannedDetails.permanent) {
            return localizationHandler.get("permanently-banned");
        } else {
            return localizationHandler.get("temporarily-banned", { expiration: LocalDateTime.fromEpochSeconds(bannedDetails.bannedUntil).format() })
        }
    }

    return (
        <div id="error" className="main-page">
            <Header label={localizationHandler.get("title")} />

            <main>
                <div id="error-title">
                    <span id="error-code">{errorCode}</span>
                    <span> - </span>
                    <span id="error-message">{ererorCodeLocalizationHandler.get(errorCode)}</span>
                </div>
                <div id="error-details">{getErrorDetails()}</div>
            </main>

            <Footer
                leftButtons={
                    <Button
                        id="error-home-button"
                        label={localizationHandler.get("home")}
                        onclick={() => window.location.href = Constants.INDEX_PAGE}
                    />
                }
                rightButtons={getLogoutButton()}
            />
        </div>
    );
}

export default ErrorPage;