import { useEffect, useState } from "react";
import { useParams } from "react-router";
import sessionChecker from "../../../../common/js/SessionChecker";
import NotificationService from "../../../../common/js/notification/NotificationService";
import useLoader from "../../../../common/hook/Loader";
import { SKYXPLORE_ADMIN_DETAILS_PAGE, SKYXPLORE_GAME_ADMIN_GET_BY_TYPE, SKYXPLORE_GAME_ADMIN_GET_ITEM } from "../../../../common/js/dao/endpoints/skyxplore/SkyXploreAdminEndpoints";
import Header from "../../../../common/component/Header";
import Footer from "../../../../common/component/Footer";
import Button from "../../../../common/component/input/Button";
import { ToastContainer } from "react-toastify";
import Spinner from "../../../../common/component/Spinner";
import ItemData from "../common/ItemData";
import { hasValue } from "../../../../common/js/Utils";
import Stream from "../../../../common/js/collection/Stream";

const SkyXploreAdminDetailsPage = () => {
    const { gameId, type, id } = useParams();

    document.title = "Admin - SkyXplore - Apphub";
    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);
    const [displaySpinner, setDisplaySpinner] = useState(false);

    const [item, setItem] = useState(null);

    useLoader({
        request: SKYXPLORE_GAME_ADMIN_GET_ITEM.createRequest(null, { type: type, gameId: gameId, itemId: id }),
        mapper: setItem,
        setDisplaySpinner: setDisplaySpinner,
        listener: [gameId, type, id]
    })

    return (
        <div id="skyxplore-admin-details" className="main-page">
            <Header label={`gameId: ${gameId}, type: ${type}, id: ${id}`} />

            <main>
                {hasValue(item) &&
                    <div>
                        <ItemData
                            id={id}
                            data={item.data}
                        />

                        <fieldset>
                            <legend>References</legend>

                            {getReferences(item.refersTo)}
                        </fieldset>

                        <fieldset>
                            <legend>Referenced by</legend>

                            {getReferences(item.referencedBy)}
                        </fieldset>
                    </div>
                }

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

    function getReferences(references) {
        return new Stream(references)
            .sorted((a, b) => a.type.localeCompare(b.type))
            .map(reference => <Reference
                key={reference.id + reference.type}
                gameId={gameId}
                reference={reference}
            />
            )
            .toList();
    }
}

const Reference = ({ gameId, reference }) => {
    if (hasValue(reference.id)) {
        return (
            <div
                className="skyxplore-admin-reference button"
                onClick={() => window.open(SKYXPLORE_ADMIN_DETAILS_PAGE.assembleUrl({ gameId: gameId, id: reference.id, type: reference.type }))}
            >
                {reference.type}: {reference.id}
            </div>
        )
    } else {
        return (
            <div
                className="skyxplore-admin-reference button"
                onClick={() => window.open(SKYXPLORE_GAME_ADMIN_GET_BY_TYPE.assembleUrl({ type: reference.type }, { gameId: gameId }))}
            >
                {reference.type}
            </div>
        )
    }
}

export default SkyXploreAdminDetailsPage;