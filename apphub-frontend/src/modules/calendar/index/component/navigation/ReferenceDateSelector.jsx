import Button from "../../../../../common/component/input/Button";
import LocalDate from "../../../../../common/js/date/LocalDate";
import TestableDateInput from "../../../common/input/TestableDateInput";

const ReferenceDateSelector = ({ referenceDate, setReferenceDate, view, localizationHandler }) => {
    return (
        <div id="calendar-reference-date-seletor" className="nowrap">
            <Button
                id="calendar-reference-date-back"
                label="<"
                onclick={() => view.back(referenceDate, setReferenceDate)}
            />

            <TestableDateInput
                id="calendar-reference-date"
                date={referenceDate}
                setDate={setReferenceDate}
            />

            <Button
                id="calendar-reference-date-forward"
                label=">"
                onclick={() => view.forward(referenceDate, setReferenceDate)}
            />

            <Button
                id="calendar-reference-date-today"
                label={localizationHandler.get("today")}
                onclick={() => setReferenceDate(LocalDate.now())}
            />
        </div>
    );

}

export default ReferenceDateSelector;