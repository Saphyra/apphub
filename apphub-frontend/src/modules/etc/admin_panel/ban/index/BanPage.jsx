import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./ban_page_localization.json";
import "./ban.css";
import { useEffect, useState } from "react";
import NotificationService from "common/js/notification/NotificationService";
import Header from "common/component/Header";
import BanSearch from "./search/BanSearch";
import BanUsers from "./users/BanUsers";
import Footer from "common/component/Footer";
import Button from "common/component/input/Button";
import { ToastContainer } from "react-toastify";
import { MODULES_PAGE } from "modules/etc/modules/ModulesEndpoints";
import Spinner from "common/component/Spinner";

const BanPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [displaySpinner, setDisplaySpinner] = useState(false);

    const [users, setUsers] = useState([]);

    useEffect(() => NotificationService.displayStoredMessages(), []);

    return (
        <div id="ban" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main>
                <BanSearch
                    setUsers={setUsers}
                    setDisplaySpinner={setDisplaySpinner}
                />

                <BanUsers
                    users={users}
                />
            </main>

            <Footer
                rightButtons={[
                    <Button
                        key="home"
                        id="error-report-home"
                        onclick={() => window.location.href = MODULES_PAGE}
                        label={localizationHandler.get("home")}
                    />
                ]}
            />

            <ToastContainer />

            {displaySpinner && <Spinner />}
        </div>
    );
}

export default BanPage;