import React, { useEffect, useState } from "react";
import "./ban.css";
import localizationData from "./ban_page_localization.json";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import sessionChecker from "../../../../common/js/SessionChecker";
import NotificationService from "../../../../common/js/notification/NotificationService";
import Header from "../../../../common/component/Header";
import Footer from "../../../../common/component/Footer";
import { ToastContainer } from "react-toastify";
import Button from "../../../../common/component/input/Button";
import Constants from "../../../../common/js/Constants";
import BanSearch from "./search/BanSearch";
import BanUsers from "./users/BanUsers";

const BanPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [users, setUsers] = useState([]);

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    return (
        <div id="ban" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main>
                <BanSearch
                    setUsers={setUsers}
                />

                <BanUsers
                    users={users}
                />
            </main>

            <Footer
                leftButtons={[
                    <Button
                        key="home"
                        id="error-report-home"
                        onclick={() => window.location.href = Constants.MODULES_PAGE}
                        label={localizationHandler.get("home")}
                    />
                ]}
            />

            <ToastContainer />
        </div>
    );
}

export default BanPage;