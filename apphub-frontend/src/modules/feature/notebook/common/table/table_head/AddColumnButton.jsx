import Button from "common/component/input/Button";

const AddColumnButton = ({ id, label, callback }) => {
    return (
        <Button
            id={id}
            label={label}
            onclick={callback}
        />
    );
}

export default AddColumnButton;