import React, { useEffect, useState } from "react";
import localizationData from "./base64_localization.json";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import sessionChecker from "../../../common/js/SessionChecker";
import NotificationService from "../../../common/js/notification/NotificationService";
import Header from "../../../common/component/Header";
import Footer from "../../../common/component/Footer";
import { ToastContainer } from "react-toastify";
import Button from "../../../common/component/input/Button";
import Constants from "../../../common/js/Constants";
import Textarea from "../../../common/component/input/Textarea";
import { Base64 } from "js-base64";
import "./base64.css";

const Base64Page = ({ }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("page-title");

    const [input, setInput] = useState("");
    const [output, setOutput] = useState("");

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    const encode = () => {
        setOutput(Base64.encode(input));
    }

    const decode = () => {
        setOutput(Base64.decode(input));
    }

    return (
        <div id="base64" className="main-page">
            <Header label={localizationHandler.get("title")} />

            <main>
                <Textarea
                    id="base64-input"
                    onchangeCallback={setInput}
                    placeholder={localizationHandler.get("input")}
                    value={input}
                />

                <div id="base64-commands">
                    <Button
                        id="base64-encode-button"
                        label={localizationHandler.get("encode")}
                        onclick={encode}
                    />

                    <Button
                        id="base64-decode-button"
                        label={localizationHandler.get("decode")}
                        onclick={decode}
                    />
                </div>

                <Textarea
                    id="base64-output"
                    onchangeCallback={setInput}
                    placeholder={localizationHandler.get("output")}
                    value={output}
                    disabled={true}
                />
            </main>

            <Footer rightButtons={
                <Button
                    id="home-button"
                    onclick={() => window.location.href = Constants.MODULES_PAGE}
                    label={localizationHandler.get("home")}
                />
            } />

            <ToastContainer />
        </div>
    );
}

export default Base64Page;