import React from "react";
import Button from "../../../../../../../../../../common/component/input/Button";
import "./surface_tile_content_footer_progress_bar.css";

const SurfaceTileContentFooterProgressBar = ({ actual, max, cancelCallback, title }) => {
    const width = actual / max * 100;

    return (
        <div>
            <div
                className="skyxplore-game-planet-surface-footer-progress-bar"
                style={{
                    width: width + "%"
                }}
            />

            <Button
                className="skyxplore-game-planet-surface-footer-cancel-button"
                label="X"
                title={title}
                onclick={cancelCallback}
            />
        </div>
    );
}

export default SurfaceTileContentFooterProgressBar;