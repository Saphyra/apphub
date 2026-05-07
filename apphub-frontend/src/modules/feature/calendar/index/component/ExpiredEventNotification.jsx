import Button from "common/component/input/Button";
import useLoader from "common/hook/Loader";
import { useState } from "react";
import { CALENDAR_EXPIRED_EVENTS_PAGE, CALENDAR_GET_EXPIRED_EVENTS } from "../../CalendarEndpoints";

const ExpiredEventNotification = ({ setDisplaySpinner, localizationHandler, refreshCounter }) => {
    const [expiredEvents, setExpiredEvents] = useState([]);

    useLoader({
        request: CALENDAR_GET_EXPIRED_EVENTS.createRequest(),
        mapper: setExpiredEvents,
        setDisplaySpinner: setDisplaySpinner,
        listener: [refreshCounter]
    });

    if (expiredEvents.length > 0) {
        return (
            <Button
                id="calendar-expired-events-button"
                label={localizationHandler.get("expired-events", { amount: expiredEvents.length })}
                onclick={() => window.location.href = CALENDAR_EXPIRED_EVENTS_PAGE}
            />
        )
    }
}

export default ExpiredEventNotification;