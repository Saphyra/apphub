import Button from "../../../../common/component/input/Button";
import TestableDateInput from "../../common/input/TestableDateInput";

const ReferenceDateSelector = ({ referenceDate, setReferenceDate, view }) => {
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
        </div>
    );

}

export default ReferenceDateSelector;