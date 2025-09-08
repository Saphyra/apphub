import React from "react";

const ConfirmationDialog = ({ id, title, content, choices }) => {
    return (
        <div className="confirmation-dialog">
            <div id={id} className="confirmation-dialog-content-wrapper">
                <h2 className="confirmation-dialog-title">{title}</h2>
                {getContent()}
                <div className="confirmation-dialog-button-container">{choices}</div>
            </div>
        </div>
    );

    function getContent() {
        if (typeof content === "string") {
            return <div className="confirmation-dialog-detail-container" dangerouslySetInnerHTML={{ __html: content }}></div>
        } else {
            return <div className="confirmation-dialog-detail-container">{content}</div>;
        }
    }
}
export default ConfirmationDialog;