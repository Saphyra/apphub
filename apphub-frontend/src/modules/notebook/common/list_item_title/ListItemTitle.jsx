import React from "react";
import InputField from "../../../../common/component/input/InputField";
import "./list_item_title.css"

const ListItemTitle = ({ inputId, placeholder, value = "", setListItemTitle, disabled, closeButton }) => {
    return (
        <div className="notebook-list-item-title">
            <InputField
                id={inputId}
                type="text"
                placeholder={placeholder}
                onchangeCallback={setListItemTitle}
                value={value}
                disabled={disabled}
            />

            {closeButton}
        </div>
    );
}

export default ListItemTitle;