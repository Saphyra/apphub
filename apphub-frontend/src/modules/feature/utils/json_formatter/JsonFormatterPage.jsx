import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./json_formatter_localization.json";
import "./json_formatter.css";
import { useEffect, useState } from "react";
import sessionChecker from "common/js/SessionChecker";
import NotificationService from "common/js/notification/NotificationService";
import Header from "common/component/Header";
import Textarea from "common/component/input/Textarea";
import Button from "common/component/input/Button";
import JsonSyntaxHighlight from "common/component/json_syntax_highlight/JsonSyntaxHighlight";
import Footer from "common/component/Footer";
import { ToastContainer } from "react-toastify";
import { MODULES_PAGE } from "modules/etc/modules/ModulesEndpoints";

const JsonFormatterPage = ({ }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("page-title");

    const [input, setInput] = useState("");
    const [output, setOutput] = useState("");

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    const format = () => {
        setOutput(input);
    }

    return (
        <div id="json-formatter" className="main-page">
            <Header label={localizationHandler.get("title")} />

            <main>
                <Textarea
                    id="json-formatter-input"
                    onchangeCallback={setInput}
                    placeholder={localizationHandler.get("input")}
                    value={input}
                />

                <div id="json-formatter-commands">
                    <Button
                        id="json-formatter-format-button"
                        label={localizationHandler.get("format")}
                        onclick={format}
                    />
                </div>

                <JsonSyntaxHighlight
                    id="json-formatter-output"
                    jsonString={output}
                />
            </main>

            <Footer rightButtons={
                <Button
                    id="home-button"
                    onclick={() => window.location.href = MODULES_PAGE}
                    label={localizationHandler.get("home")}
                />
            } />

            <ToastContainer />
        </div>
    );
}

export default JsonFormatterPage;