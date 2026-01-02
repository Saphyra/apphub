import React from "react";
import Button from "../../../../../../../../../../common/component/input/Button";
import "./surface_tile_content_footer_progress_bar.css";
import ProgressBar from "../../../../../../../../../../common/component/progress_bar/ProgressBar";

const SurfaceTileContentFooterProgressBar = ({ actual, max, cancelCallback, title }) => {
    return <ProgressBar
        className="skyxplore-game-planet-surface-footer-progress-bar"
        currentPoints={actual}
        targetPoints={max}
        operations={<Button
            className="skyxplore-game-planet-surface-footer-cancel-button"
            label="X"
            title={title}
            onclick={cancelCallback}
        />}
    />
}

export default SurfaceTileContentFooterProgressBar;