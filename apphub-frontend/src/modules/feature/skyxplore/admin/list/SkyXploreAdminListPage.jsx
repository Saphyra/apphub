import { useLoaderData } from "react-router";
import "./skyxplore_admin_list_page.css";
import { useEffect, useState } from "react";
import sessionChecker from "common/js/SessionChecker";
import NotificationService from "common/js/notification/NotificationService";
import useLoader from "common/hook/Loader";
import { SKYXPLORE_GAME_ADMIN_GET_BY_TYPE } from "common/js/dao/endpoints/skyxplore/SkyXploreAdminEndpoints";
import Header from "common/component/Header";
import SearchBar from "./SearchBar";
import ListContent from "./ListContent";
import Footer from "common/component/Footer";
import Button from "common/component/input/Button";
import { ToastContainer } from "react-toastify";
import Spinner from "common/component/Spinner";

const SkyXploreAdminListPage = () => {
    const { gameId, type } = useLoaderData();

    document.title = "Admin - SkyXplore - Apphub";
    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);
    const [displaySpinner, setDisplaySpinner] = useState(false);

    const [items, setItems] = useState([]);
    const [filters, setFilters] = useState({});

    useLoader({
        request: SKYXPLORE_GAME_ADMIN_GET_BY_TYPE.createRequest(null, { type: type }, { gameId: gameId }),
        mapper: setItems,
        setDisplaySpinner: setDisplaySpinner,
        listener: [gameId, type]
    })

    return (
        <div id="skyxplore-admin-list" className="main-page">
            <Header label={`gameId: ${gameId}, type: ${type}`} />

            <main>
                <SearchBar
                    items={items}
                    filters={filters}
                    setFilters={setFilters}
                />

                <ListContent
                    items={items}
                    filters={filters}
                    gameId={gameId}
                    type={type}
                />
            </main>

            <Footer
                rightButtons={[
                    <Button
                        key="close"
                        label="X"
                        onclick={() => window.close()}
                    />
                ]}
            />

            <ToastContainer />

            {displaySpinner && <Spinner />}
        </div>
    );
}

export default SkyXploreAdminListPage;