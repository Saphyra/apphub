import React, { useState } from "react";
import InputField from "../../../../../common/component/input/InputField";
import Utils from "../../../../../common/js/Utils";

const ScheduledInputField = ({
    className,
    type, value,
    onchangeCallback,
    scheduledCallback,
    timeout = 1000,
    placeholder,
    disabled = false
}) => {
    const [schedule, setSchedule] = useState(null);

    const scheduleCallback = (newValue) => {
        if (Utils.hasValue(schedule)) {
            clearTimeout(schedule);
        }

        onchangeCallback(newValue);

        setSchedule(setTimeout(() => {
            if (scheduledCallback(newValue)) {
                setSchedule(null);
            }
        },
            timeout
        ));
    }

    return <InputField
        type={type}
        className={className + (Utils.hasValue(schedule) ? " scheduled" : "")}
        onchangeCallback={scheduleCallback}
        value={value}
        placeholder={placeholder}
        disabled={disabled}
    />
}

export default ScheduledInputField;