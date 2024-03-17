import React, { useState } from "react";
import "./ban_user_role.css"
import localizationData from "./ban_user_role_localization.json";
import roleLocalizationData from "../../../role_localization.json";
import PreLabeledInputField from "../../../../../common/component/input/PreLabeledInputField";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import SelectInput, { SelectOption } from "../../../../../common/component/input/SelectInput";
import roles from "../../../roles.json";
import Stream from "../../../../../common/js/collection/Stream";
import PostLabeledInputField from "../../../../../common/component/input/PostLabeledInputField";
import InputField from "../../../../../common/component/input/InputField";
import Textarea from "../../../../../common/component/input/Textarea";
import Button from "../../../../../common/component/input/Button";
import NotificationService from "../../../../../common/js/notification/NotificationService";
import Utils from "../../../../../common/js/Utils";
import Endpoints from "../../../../../common/js/dao/dao";

const BanUserRole = ({ userData, setUserData }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const roleLocalizationHandler = new LocalizationHandler(roleLocalizationData);

    const [roleToBan, setRoleToBan] = useState("");
    const [permanent, setPermanent] = useState(false);
    const [duration, setDuration] = useState(1);
    const [chronoUnit, setChronoUnit] = useState("");
    const [reason, setReason] = useState("");
    const [password, setPassword] = useState("");

    const ban = async () => {
        if (roleToBan.length === 0) {
            NotificationService.showError(localizationHandler.get("select-role"));
            return;
        }

        if (!permanent && duration < 1) {
            NotificationService.showError(localizationHandler.get("duration-too-short"));
            return;
        }

        if (!permanent && chronoUnit.length === 0) {
            NotificationService.showError(localizationHandler.get("select-chrono-unit"));
            return;
        }

        if (Utils.isBlank(reason)) {
            NotificationService.showError(localizationHandler.get("reason-empty"));
            return;
        }

        if (password.length === 0) {
            NotificationService.showError(localizationHandler.get("password-empty"));
            return;
        }

        setPassword("");

        const payload = {
            bannedUserId: userData.userId,
            bannedRole: roleToBan,
            permanent: permanent,
            duration: duration,
            chronoUnit: chronoUnit,
            reason: reason,
            password: password
        }

        const response = await Endpoints.ACCOUNT_BAN_USER.createRequest(payload)
            .send();

        setRoleToBan("");
        setPermanent(false);
        setDuration(1);
        setChronoUnit("");
        setReason("");
        
        setUserData(response);
    }

    const getBannableRoles = () => {
        const bannedRoles = new Stream(userData.bans)
            .map(ban => ban.bannedRole)
            .toList();

        return new Stream(roles)
            .filter(role => bannedRoles.indexOf(role) < 0)
            .sorted((a, b) => roleLocalizationHandler.get(a).localeCompare(roleLocalizationHandler.get(b)))
            .map(role => new SelectOption(roleLocalizationHandler.get(role), role))
            .reverse()
            .add(new SelectOption(localizationHandler.get("select-role"), ""))
            .reverse()
            .toList();
    }

    return (
        <div id="ban-user-role">
            <div>
                <PreLabeledInputField
                    id="ban-user-role-to-ban"
                    labelId="ban-user-role-to-ban-label"
                    label={localizationHandler.get("role-to-ban") + ":"}
                    input={<SelectInput
                        id="ban-user-role-to-ban-select"
                        value={roleToBan}
                        onchangeCallback={setRoleToBan}
                        options={getBannableRoles()}
                    />}
                />

                <PostLabeledInputField
                    id="ban-user-is-permanent"
                    labelId="ban-user-is-permanent-label"
                    label={localizationHandler.get("permanent")}
                    input={<InputField
                        type="checkbox"
                        id="ban-user-is-permanent-input"
                        checked={permanent}
                        onchangeCallback={setPermanent}
                    />}
                />

                {!permanent &&
                    <span id="ban-user-banned-until">
                        &nbsp;
                        <span>{localizationHandler.get("banned-until")}</span>
                        <span>: </span>
                        <InputField
                            id="ban-user-duration"
                            type="number"
                            min="1"
                            value={duration}
                            onchangeCallback={setDuration}
                        />

                        <SelectInput
                            id="ban-user-banned-until-input"
                            value={chronoUnit}
                            onchangeCallback={setChronoUnit}
                            options={[
                                new SelectOption(localizationHandler.get("select"), ""),
                                new SelectOption(localizationHandler.get("minutes"), "MINUTES"),
                                new SelectOption(localizationHandler.get("hours"), "HOURS"),
                                new SelectOption(localizationHandler.get("days"), "DAYS"),
                                new SelectOption(localizationHandler.get("weeks"), "WEEKS"),
                                new SelectOption(localizationHandler.get("months"), "MONTHS")
                            ]}
                        />
                    </span>
                }
            </div>

            <div>
                <Textarea
                    id="ban-user-reason"
                    onchangeCallback={setReason}
                    value={reason}
                    placeholder={localizationHandler.get("reason")}
                />
            </div>

            <div>
                <InputField
                    id="ban-user-ban-password"
                    type="password"
                    value={password}
                    onchangeCallback={setPassword}
                    placeholder={localizationHandler.get("password")}
                />
            </div>

            <div>
                <Button
                    id="ban-user-ban"
                    label={localizationHandler.get("ban")}
                    onclick={ban}
                />
            </div>
        </div>
    );
}

export default BanUserRole;