import React, { useEffect, useState } from "react";
import Footer from "../../../common/component/Footer";
import Button from "../../../common/component/input/Button";
import localizationData from "./random_route_localization.json";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import Constants from "../../../common/js/Constants";
import DirectionSelector from "./DirectionSelector";
import { LEFT_OR_RIGHT, STRAIGHT_LEFT_OR_RIGHT, STRAIGHT_OR_LEFT, STRAIGHT_OR_RIGHT } from "./Intersection";
import "./random_route.css";
import { hasValue } from "../../../common/js/Utils";
import Spinner from "../../../common/component/Spinner";

const RandomDirectionPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [displaySpinner, setDisplaySpinner] = useState(false);
    const [direction, setDirection] = useState(null);
    const [time, setTime] = useState(0);
    const [isRunning, setIsRunning] = useState(false);

    useEffect(() => {
        let timer;
        if (isRunning) {
            timer = setInterval(() => {
                setTime((prevTime) => prevTime + 1);
            }, 1000);
        } else {
            clearInterval(timer);
        }
        return () => clearInterval(timer);
    }, [isRunning]);

    return (
        <div id="random-direction" className="main-page" >
            <main className="headless">
                <DirectionSelector
                    intersection={STRAIGHT_OR_LEFT}
                    setDirection={setDirection}
                    setDisplaySpinner={setDisplaySpinner}
                />

                <DirectionSelector
                    intersection={STRAIGHT_OR_RIGHT}
                    setDirection={setDirection}
                    setDisplaySpinner={setDisplaySpinner}
                />

                <DirectionSelector
                    intersection={STRAIGHT_LEFT_OR_RIGHT}
                    setDirection={setDirection}
                    setDisplaySpinner={setDisplaySpinner}
                />

                <DirectionSelector
                    intersection={LEFT_OR_RIGHT}
                    setDirection={setDirection}
                    setDisplaySpinner={setDisplaySpinner}
                />

                {hasValue(direction) &&
                    <div id="random-direction-result" className={direction}></div>
                }
            </main>

            <Footer
                leftButtons={getStopperButtons()}
                rightButtons={
                    <Button
                        id="home-button"
                        onclick={() => window.location.href = Constants.MODULES_PAGE}
                        label={localizationHandler.get("home")}
                    />
                } />

            {displaySpinner && <Spinner />}
        </div>
    );

    function getStopperButtons() {
        const result = [];

        if (isRunning) {
            result.push(
                <Button
                    key="pause"
                    label={localizationHandler.get("pause")}
                    onclick={() => setIsRunning(false)}
                />
            );
        } else {
            result.push(
                <Button
                    key="start"
                    label={localizationHandler.get("start")}
                    onclick={() => setIsRunning(true)}
                />
            );
        }

        result.push(
            <span key="elapsed">{formatTime(time)}</span>
        );

        result.push(
            <Button
                key="reset"
                label={localizationHandler.get("reset")}
                onclick={() => setTime(0)}
            />
        );

        return result;

        function formatTime(seconds) {
            const minutes = Math.floor(seconds / 60);
            const secs = seconds % 60;
            return `${String(minutes).padStart(2, "0")}:${String(secs).padStart(2, "0")}`;
        };
    }
}

export default RandomDirectionPage;