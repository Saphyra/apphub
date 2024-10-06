import React, { useState } from "react";
import "./ban_search.css";
import localizationData from "./ban_search_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import InputField from "../../../../../common/component/input/InputField";
import Button from "../../../../../common/component/input/Button";
import NotificationService from "../../../../../common/js/notification/NotificationService";
import useHasFocus from "../../../../../common/hook/UseHasFocus";
import { useUpdateEffect } from "react-use";
import { ACCOUNT_BAN_SEARCH } from "../../../../../common/js/dao/endpoints/UserEndpoints";

const BanSearch = ({ setUsers }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [query, setQuery] = useState("");

    const isInFocus = useHasFocus();
    useUpdateEffect(() => {
        if (isInFocus && query.length >= 3) {
            search();
        }
    }, [isInFocus]);

    const searchIfEnter = (e) => {
        if (e.which === 13) {
            search();
        }
    }

    const search = () => {
        const fetch = async () => {
            if (query.length < 3) {
                NotificationService.showError(localizationHandler.get("query-too-short"));
                return;
            }

            const response = await ACCOUNT_BAN_SEARCH.createRequest({ value: query }, {}, { includeMarkedForDeletion: true, includeSelf: true })
                .send();

            setUsers(response);
        }
        fetch();
    }

    return (
        <div id="ban-search">
            <InputField
                id="ban-search-input"
                value={query}
                onchangeCallback={setQuery}
                placeholder={localizationHandler.get("search-input")}
                onkeyupCallback={searchIfEnter}
            />

            <Button
                id="ban-search-button"
                label={localizationHandler.get("search")}
                onclick={search}
            />
        </div>
    );
}

export default BanSearch;