import React from "react";

const PostLabeledInputField = ({ label, input }) => {
    return (
        <label>
            {input}
            <span> </span>
            <span>{label}</span>
        </label>
    );
}

export default PostLabeledInputField;