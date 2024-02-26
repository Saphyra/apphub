import React, { useEffect, useState } from "react";
import localizationData from "./account_page_localization.json";
import LocalizationHandler from "../../common/js/LocalizationHandler";
import Header from "../../common/component/Header";
import Footer from "../../common/component/Footer";
import Button from "../../common/component/input/Button";
import Constants from "../../common/js/Constants";
import AccountLanguageSelector from "./language_selector/AccountLanguageSelector";
import "./account_page.css";
import EmailChanger from "./email_changer/EmailChanger";
import { ToastContainer } from "react-toastify";
import UsernameChanger from "./username_changer/UsernameChanger";
import PasswordChanger from "./password_changer/PasswordChanger";
import Utils from "../../common/js/Utils";
import ConfirmationDialog from "../../common/component/confirmation_dialog/ConfirmationDialog";
import AccountDeleter from "./account_deleter/AccountDeleter";
import sessionChecker from "../../common/js/SessionChecker";
import NotificationService from "../../common/js/notification/NotificationService";
import useLoader from "../../common/hook/Loader";
import Endpoints from "../../common/js/dao/dao";

const AccountPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("page-title");

    const [confirmationDialogData, setCinfirmationDialogData] = useState(null);
    const [userData, setUserData] = useState({});

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    useLoader(Endpoints.ACCOUNT_GET_USER.createRequest(), setUserData);

    return (
        <div className="main-page">
            <Header label={localizationHandler.get("title")} />

            <main>
                <AccountLanguageSelector />

                <EmailChanger
                    userData={userData}
                    setUserData={setUserData}
                />
                <UsernameChanger
                    userData={userData}
                    setUserData={setUserData}
                />
                <PasswordChanger />
                <AccountDeleter
                    setConfirmationDialogData={setCinfirmationDialogData}
                />
            </main>

            <Footer
                rightButtons={
                    <Button
                        id="account-home-button"
                        onclick={() => window.location.href = Constants.MODULES_PAGE}
                        label={localizationHandler.get("home")}
                    />
                }
            />

            {Utils.hasValue(confirmationDialogData) &&
                <ConfirmationDialog
                    id={confirmationDialogData.id}
                    title={confirmationDialogData.title}
                    content={confirmationDialogData.content}
                    choices={confirmationDialogData.choices}
                />
            }

            <ToastContainer />
        </div>
    );
}

export default AccountPage;