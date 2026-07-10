import { useParams } from "react-router";
import localizationData from "./calendar_share_page_localization.json";
import LocalizationHandler from "common/js/LocalizationHandler";
import { useState } from "react";
import Header from "common/component/Header";
import Footer from "common/component/Footer";
import Button from "common/component/input/Button";
import useQueryParams from "common/hook/UseQueryParams";
import { hasValue } from "common/js/Utils";
import { CALENDAR_GET_SHARED_OBJECT, CALENDAR_PAGE } from "../CalendarEndpoints";
import { ToastContainer } from "react-toastify";
import ConfirmationDialog from "common/component/confirmation_dialog/ConfirmationDialog";
import Spinner from "common/component/Spinner";
import useLoader from "common/hook/Loader";
import ShareWith from "./share_with/ShareWith";
import SharedWith from "./shared_with/SharedWith";
import "./calendar_share.css";
import useRefresh from "common/hook/Refresh";

const CalendarSharePage = () => {
    const { type, id } = useParams();
    const queryParams = useQueryParams();

    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title", { type: localizationHandler.get(type) });
    const [displaySpinner, setDisplaySpinner] = useState(false);
    const [confirmationDialogData, setConfirmationDialogData] = useState(null);
    const [backUrl, setBackUrl] = useState(hasValue(queryParams.backUrl) ? queryParams.backUrl : CALENDAR_PAGE);
    const [refreshCounter, refresh] = useRefresh();

    const [objectData, setObjectData] = useState(null);

    useLoader({
        request: CALENDAR_GET_SHARED_OBJECT.createRequest(null, { type: type, id: id }),
        mapper: setObjectData,
        setDisplaySpinner: setDisplaySpinner,
        listener: [refreshCounter],
    });

    return (
        <div id="calendar-share" className="main-page">
            {
                hasValue(objectData) &&
                <Header
                    label={
                        localizationHandler.get(
                            "page-title",
                            {
                                type: localizationHandler.get(type),
                                name: objectData.name
                            }
                        )
                    }
                />
            }

            <main>
                <ShareWith
                    localizationHandler={localizationHandler}
                    type={type}
                    setDisplaySpinner={setDisplaySpinner}
                    objectData={objectData}
                    refresh={refresh}
                />

                {hasValue(objectData) &&
                    <SharedWith
                        localizationHandler={localizationHandler}
                        type={type}
                        sharedWith={objectData.sharedWith}
                        setDisplaySpinner={setDisplaySpinner}
                        objectId={id}
                        setConfirmationDialogData={setConfirmationDialogData}
                        itemName={objectData.name}
                        refresh={refresh}
                    />
                }
            </main>

            <Footer rightButtons={[
                <Button
                    id="calendar-labels-back-button"
                    key="back"
                    onclick={() => window.location.href = backUrl}
                    label={localizationHandler.get("back")}
                />
            ]} />

            <ToastContainer />

            {confirmationDialogData &&
                <ConfirmationDialog
                    id={confirmationDialogData.id}
                    title={confirmationDialogData.title}
                    content={confirmationDialogData.content}
                    choices={confirmationDialogData.choices}
                />
            }

            {displaySpinner > 0 && <Spinner />}
        </div>
    );
}

export default CalendarSharePage;