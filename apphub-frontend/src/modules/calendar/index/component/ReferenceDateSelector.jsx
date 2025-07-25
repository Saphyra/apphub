import Button from "../../../../common/component/input/Button";
import InputField from "../../../../common/component/input/InputField";
import LocalDate from "../../../../common/js/date/LocalDate";

const ReferenceDateSelector = ({ referenceDate, setReferenceDate, view }) => {
    return (
        <div id="calendar-reference-date-seletor">
            <Button
                id="calendar-reference-date-back"
                label="<"
                onclick={() => view.back(referenceDate, setReferenceDate)}
            />

            <InputField
                type="date"
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