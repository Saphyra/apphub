import Button from "common/component/input/Button";
import Constants from "common/js/Constants";

const Label = ({text, shared, callback}) =>{
    return <Button
        className="calendar-label"
        label={text + (shared ? Constants.ICON_SHARED : "")}
        onclick={callback}
    />
}

export default Label;