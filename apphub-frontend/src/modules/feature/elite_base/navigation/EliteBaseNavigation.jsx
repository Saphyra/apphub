import React, { useState } from "react";
import "./elite_base_navigation.css";
import menu from "./menu.json";
import menuLocalizationData from "./menu_localization.json";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import EliteBasePage from "../EliteBasePage";
import Stream from "../../../../common/js/collection/Stream";
import { addAndSet, isArrayEmpty, removeAndSet } from "../../../../common/js/Utils";
import Button from "../../../../common/component/input/Button";

const EliteBaseNavigation = ({ openedPage, setOpenedPage }) => {
    const [openedCategories, setOpenedCategories] = useState(sessionStorage.eliteBaseMenuOpenedItems ? JSON.parse(sessionStorage.eliteBaseMenuOpenedItems) : [EliteBasePage.INDEX]);

    const updateOpenedCategories = (newValue) => {
        sessionStorage.eliteBaseMenuOpenedItems = JSON.stringify(newValue);
        setOpenedCategories(newValue);
    }

    return (
        <div id="elite-base-navigation">
            {getMenu()}
        </div>
    )

    function getMenu() {
        return new Stream(menu)
            .map(menuItem =>
                <MenuItem
                    key={menuItem.id}
                    data={menuItem}
                    openedPage={openedPage}
                    setOpenedPage={setOpenedPage}
                    openedCategories={openedCategories}
                    setOpenedCategories={updateOpenedCategories}
                />
            )
            .toList();
    }
}

export default EliteBaseNavigation;

const MenuItem = ({ data, openedPage, setOpenedPage, openedCategories, setOpenedCategories }) => {
    const menuLocalizationHandler = new LocalizationHandler(menuLocalizationData);

    return (
        <div className="elite-base-navigation-menu-item">
            <div
                className={"button" + (openedPage == data.id ? " opened" : " asd") + (data.clickable ? "" : " disabled")}
                onClick={openPage}
            >
                {getToggleButton()}
                <span className="menu-item-label">{menuLocalizationHandler.get(data.id)}</span>
            </div>
            {isOpened() && getItems()}
        </div>
    );

    function getToggleButton() {
        if (isArrayEmpty(data.items)) {
            return null;
        }

        if (isOpened()) {
            return <Button
                label={"^"}
                onclick={closeItem}
            />
        } else {
            return <Button
                label={"v"}
                onclick={openItem}
            />

        }

        function closeItem(e) {
            e.stopPropagation();
            removeAndSet(openedCategories, (i) => i === data.id, setOpenedCategories)
        }

        function openItem(e) {
            e.stopPropagation();
            addAndSet(openedCategories, data.id, setOpenedCategories)
        }
    }

    function isOpened() {
        return openedCategories.indexOf(data.id) >= 0;
    }

    function getItems() {
        return new Stream(data.items)
            .map(item =>
                <MenuItem
                    key={item.id}
                    data={item}
                    openedPage={openedPage}
                    setOpenedPage={setOpenedPage}
                    openedCategories={openedCategories}
                    setOpenedCategories={setOpenedCategories}
                />
            )
            .toList();
    }

    function openPage() {
        if (data.clickable) {
            console.log("Opening " + data.id);

            setOpenedPage(data.id);
        }
    }
}