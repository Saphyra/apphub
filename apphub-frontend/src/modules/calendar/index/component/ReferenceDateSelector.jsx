import { useEffect, useState } from "react";
import Button from "../../../../common/component/input/Button";
import InputField from "../../../../common/component/input/InputField";
import LocalDate from "../../../../common/js/date/LocalDate";
import { isTestMode } from "../../../../common/js/Utils";

const ReferenceDateSelector = ({ referenceDate, setReferenceDate, view }) => {
    const [tempReferenceDate, setTempReferenceDate] = useState("");

    useEffect(syncReferenceDate, [tempReferenceDate])
    useEffect(() => setTempReferenceDate(referenceDate.toString()), [referenceDate]);

    return (
        <div id="calendar-reference-date-seletor">
            <Button
                id="calendar-reference-date-back"
                label="<"
                onclick={() => view.back(referenceDate, setReferenceDate)}
            />

            <InputField
                id="calendar-reference-date"
                type={isTestMode() ? "text" : "date"}
                value={tempReferenceDate}
                onchangeCallback={date => setTempReferenceDate(date)}
            />

            <Button
                id="calendar-reference-date-forward"
                label=">"
                onclick={() => view.forward(referenceDate, setReferenceDate)}
            />
        </div>
    );

    function syncReferenceDate() {
        if(referenceDate.toString() === tempReferenceDate){
            return;
        }

        if (isValidDate()) {
            setReferenceDate(LocalDate.parse(tempReferenceDate));
        }

        function isValidDate() {
            const regex = /^\d{4}-\d{2}-\d{2}$/;
            if (!regex.test(tempReferenceDate)) {
                return false;
            }

            // Parse the date
            const date = new Date(tempReferenceDate);

            // Check if date is valid and matches input
            const [year, month, day] = tempReferenceDate.split('-').map(Number);
            return date.getFullYear() === year &&
                date.getMonth() === month - 1 && // Month is 0-indexed
                date.getDate() === day;
        }
    }

}

export default ReferenceDateSelector;