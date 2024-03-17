import React from "react";

const PostLabeledInputField = ({ id, labelId, label, input, className }) => {
    return (
        <label id={id} className={className}>
            {input}
            <span> </span>
            <span id={labelId}>{label}</span>
        </label>
    );
}

export default PostLabeledInputField;