import { useEffect, useState } from "react";
import Optional from "../../../../common/js/collection/Optional";
import InputField from "../../../../common/component/input/InputField";
import { isBlank, isTestMode } from "../../../../common/js/Utils";
import LocalTime from "../../../../common/js/date/LocalTime";

const TestableTimeInput = ({ id, time, setTime }) => {
    const [tempTime, setTempTime] = useState("0");

    useEffect(syncTime, [tempTime])
    useEffect(() => setTempTime(new Optional(time).map(d => d.formatWithoutSeconds()).orElse("")), [time]);

    return <InputField
        id={id}
        type={isTestMode() ? "text" : "time"}
        value={tempTime}
        onchangeCallback={d => setTempTime(d)}
    />

    function syncTime() {
        if (new Optional(time).map(d => d.toString()).orElse(null) === tempTime) {
            return;
        } else if (isBlank(tempTime)) {
            setTime(null);
            return;
        } else if (isValidTime()) {
            setTime(LocalTime.parse(tempTime));
            return;
        }

        function isValidTime() {
            const regex = /^\d{2}:\d{2}$/;
            if (!regex.test(tempTime)) {
                return false;
            }

            // Parse the time
            const [hours, minutes] = tempTime.split(':').map(Number);
            return hours >= 0 && hours < 24 && minutes >= 0 && minutes < 60;
        }
    }
}

export default TestableTimeInput;