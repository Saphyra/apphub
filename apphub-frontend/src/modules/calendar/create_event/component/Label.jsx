import Button from "../../../../common/component/input/Button";

const Label = ({text, callback}) =>{
    return <Button
        className="calendar-label"
        label={text}
        onclick={callback}
    />
}

export default Label;