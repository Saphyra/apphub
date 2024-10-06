import React from "react";
import "./json_syntax_highlight.css";
import localizationData from "./json_syntax_highlight_localization.json";
import LocalizationHandler from "../../js/LocalizationHandler";
import { isJsonString } from "../../js/Utils";

const JsonSyntaxHighlight = ({ id, jsonString }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const syntaxHighlight = () => {
        const jsonObj = JSON.parse(jsonString);
        return JSON.stringify(jsonObj, undefined, 4)
            .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
            .replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
                var cls = 'json-number';
                if (/^"/.test(match)) {
                    if (/:$/.test(match)) {
                        cls = 'json-key';
                    } else {
                        cls = 'json-string';
                    }
                } else if (/true|false/.test(match)) {
                    cls = 'json-boolean';
                } else if (/null/.test(match)) {
                    cls = 'json-null';
                }
                return '<span class="' + cls + '">' + match + '</span>';
            });
    }

    if (!isJsonString(jsonString)) {
        return (
            <pre
                className="selectable"
                id={id}
            >
                {localizationHandler.get("not-a-valid-json")}
            </pre>
        )
    }

    return (
        <pre
            className="selectable"
            id={id}
            dangerouslySetInnerHTML={{
                __html: syntaxHighlight()
            }}
        />
    )
}

export default JsonSyntaxHighlight;