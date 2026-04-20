import React from "react";
import "./setting/setting.css"

const Setting = ({ label, content }) => {
    return (
        <div className="skyxplore-lobby-setting">
            <h4 className="skyxplore-lobby-setting-name">{label}</h4>
            <div className="skyxplore-lobby-setting-content">
                {content}
            </div>
        </div>
    )
}

export default Setting;