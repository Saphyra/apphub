import React, { useEffect, useState } from "react";
import "./ban_details.css";
import localizationData from "./ban_details_localization.json";
import { useParams } from "react-router";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import sessionChecker from "../../../../common/js/SessionChecker";
import NotificationService from "../../../../common/js/notification/NotificationService";
import useLoader from "../../../../common/hook/Loader";
import Header from "../../../../common/component/Header";
import Footer from "../../../../common/component/Footer";
import Button from "../../../../common/component/input/Button";
import ConfirmationDialog from "../../../../common/component/confirmation_dialog/ConfirmationDialog";
import { ToastContainer } from "react-toastify";
import BanUserInfo from "./user_info/BanUserInfo";
import BanUserDeletion from "./deletion/BanUserDeletion";
import BanUserRole from "./role/BanUserRole";
import BanUserBannedRoles from "./table/BanUserBannedRoles";
import { hasValue } from "../../../../common/js/Utils";
import { ACCOUNT_GET_BANS } from "../../../../common/js/dao/endpoints/UserEndpoints";
import Spinner from "../../../../common/component/Spinner";

const BanDetailsPage = () => {
    const { userId } = useParams();
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [confirmationDialogData, setConfirmationDialogData] = useState(null);
    const [userData, setUserdata] = useState(null);
    const [displaySpinner, setDisplaySpinner] = useState(false);

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);
    useLoader({ request: ACCOUNT_GET_BANS.createRequest(null, { userId: userId }), mapper: setUserdata, setDisplaySpinner: setDisplaySpinner });

    return (
        <div id="ban-details" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            {hasValue(userData) &&
                <main>
                    <BanUserInfo
                        userData={userData}
                    />

                    <BanUserDeletion
                        userData={userData}
                        setUserData={setUserdata}
                        setConfirmationDialogData={setConfirmationDialogData}
                    />

                    <BanUserRole
                        userData={userData}
                        setUserData={setUserdata}
                    />

                    <BanUserBannedRoles
                        userData={userData}
                        setUserData={setUserdata}
                    />
                </main>
            }

            <Footer
                leftButtons={[
                    <Button
                        key="close"
                        id="error-report-details-close"
                        label={localizationHandler.get("close")}
                        onclick={() => window.close()}
                    />
                ]}
            />

            {hasValue(confirmationDialogData) &&
                <ConfirmationDialog
                    id={confirmationDialogData.id}
                    title={confirmationDialogData.title}
                    content={confirmationDialogData.content}
                    choices={confirmationDialogData.choices}
                />
            }

            {displaySpinner && <Spinner />}

            <ToastContainer />
        </div>
    );
}

export default BanDetailsPage;