import React from "react";

const ContactsListItem = ({ label, buttons }) => {
    return (
        <div className="skyxplore-contacts-list-item">
            <span className="skyxplore-contacts-list-item-label">{label}</span>

            <div className="skyxplore-contacts-list-item-buttons">
                {buttons}
            </div>
        </div>
    );
}

export default ContactsListItem;