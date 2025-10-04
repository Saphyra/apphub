import { useEffect, useState } from "react";
import InputField from "../../../../common/component/input/InputField";
import { isBlank, isTestMode } from "../../../../common/js/Utils";
import LocalDate from "../../../../common/js/date/LocalDate";
import Optional from "../../../../common/js/collection/Optional";

const TestableDateInput = ({ id, date, setDate }) => {
    const [tempDate, setTempDate] = useState("0");

    useEffect(syncDate, [tempDate])
    useEffect(() => setTempDate(new Optional(date).map(d => d.toString()).orElse("")), [date]);

    return <InputField
        id={id}
        type={isTestMode() ? "text" : "date"}
        value={tempDate}
        onchangeCallback={d => setTempDate(d)}
    />

    function syncDate() {
        if (new Optional(date).map(d => d.toString()).orElse(null) === tempDate) {
            return;
        } else if (isBlank(tempDate)) {
            setDate(null);
            return;
        } else if (isValidDate()) {
            setDate(LocalDate.parse(tempDate));
            return;
        }

        function isValidDate() {
            const regex = /^\d{4}-\d{2}-\d{2}$/;
            if (!regex.test(tempDate)) {
                return false;
            }

            // Parse the date
            const date = new Date(tempDate);

            // Check if date is valid and matches input
            const [year, month, day] = tempDate.split('-').map(Number);
            return date.getFullYear() === year &&
                date.getMonth() === month - 1 && // Month is 0-indexed
                date.getDate() === day;
        }
    }
}

export default TestableDateInput;