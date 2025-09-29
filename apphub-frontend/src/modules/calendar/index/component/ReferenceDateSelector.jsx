import Button from "../../../../common/component/input/Button";
import InputField from "../../../../common/component/input/InputField";
import LocalDate from "../../../../common/js/date/LocalDate";
import { isTestMode } from "../../../../common/js/Utils";

const ReferenceDateSelector = ({ referenceDate, setReferenceDate, view }) => {
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
                value={referenceDate}
                onchangeCallback={date => setReferenceDate(LocalDate.parse(date))}
            />

            <Button
                id="calendar-reference-date-forward"
                label=">"
                onclick={() => view.forward(referenceDate, setReferenceDate)}
            />
        </div>
    )
}

export default ReferenceDateSelector;