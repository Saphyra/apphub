import React from "react";

const PostLabeledInputField = ({ label, input, className }) => {
    return (
        <label className={className}>
            {input}
            <span> </span>
            <span>{label}</span>
        </label>
    );
}

export default PostLabeledInputField;