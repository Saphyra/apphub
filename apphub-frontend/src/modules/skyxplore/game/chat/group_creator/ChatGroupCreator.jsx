import React, { useEffect, useState } from "react";
import "./chat_group_creator.css";
import localizationData from "./chat_group_creator_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import Button from "../../../../../common/component/input/Button";
import InputField from "../../../../../common/component/input/InputField";
import Endpoints from "../../../../../common/js/dao/dao";
import Stream from "../../../../../common/js/collection/Stream";
import NotificationService from "../../../../../common/js/notification/NotificationService";
import Utils from "../../../../../common/js/Utils";

const ChatGroupCreator = ({ setDisplayGroupCreator }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const [roomId, setRoomId] = useState("");
    const [players, setPlayers] = useState([]);
    const [invitedPlayers, setInvitedPlayers] = useState([]);

    useEffect(() => loadPlayers(), []);

    const loadPlayers = () => {
        const fetch = async () => {
            const response = await Endpoints.SKYXPLORE_GAME_GET_PLAYERS.createRequest(null, null, { excludeSelf: true })
                .send();
            setPlayers(response);
        }
        fetch();
    }

    const invite = (player) => {
        const copy = new Stream(invitedPlayers)
            .add(player)
            .toList();
        setInvitedPlayers(copy);
    }

    const uninvite = (player) => {
        const copy = new Stream(invitedPlayers)
            .remove(p => p === player)
            .toList();
        setInvitedPlayers(copy);
    }

    const getAvailablePlayers = () => {
        return new Stream(players)
            .filter(player => invitedPlayers.indexOf(player) < 0)
            .map(player => <Button
                key={player.id}
                className="skyxplore-game-chat-group-creator-player"
                label={player.name}
                onclick={() => invite(player)}
            />)
            .toList();
    }

    const getInvitedPlayers = () => {
        return new Stream(invitedPlayers)
            .map(player => <Button
                key={player.id}
                className="skyxplore-game-chat-group-creator-player"
                label={player.name}
                onclick={() => uninvite(player)}
            />)
            .toList();
    }

    const getPlayers = () => {
        if (players.length == 0) {
            return (
                <tr>
                    <td
                        colSpan={2}
                        id="skyxplore-game-chat-group-creator-no-available-player"
                    >
                        {localizationHandler.get("no-available-player")}
                    </td>
                </tr>
            );
        } else {
            return (
                <tr>
                    <td id="skyxplore-game-chat-group-creator-available-players">{getAvailablePlayers()}</td>
                    <td id="skyxplore-game-chat-group-creator-invited-players">{getInvitedPlayers()}</td>
                </tr>
            );
        }
    }

    const create = async () => {
        if (Utils.isBlank(roomId)) {
            NotificationService.showError(localizationHandler.get("room-id-blank"));
            return;
        }

        const members = new Stream(invitedPlayers)
            .map(player => player.id)
            .toList();

        const payload = {
            roomTitle: roomId,
            members: members
        }

        await Endpoints.SKYXPLORE_GAME_CREATE_CHAT_ROOM.createRequest(payload)
            .send();

        setDisplayGroupCreator(false);
    }

    return (
        <div id="skyxplore-game-chat-group-creator">
            <div id="skyxplore-game-chat-group-creator-title">
                {localizationHandler.get("title")}

                <Button
                    id="skyxplore-game-chat-group-creator-close-button"
                    label="X"
                    onclick={() => setDisplayGroupCreator(false)}
                />
            </div>

            <InputField
                id="skyxplore-game-chat-group-creator-id-input"
                type="text"
                onchangeCallback={setRoomId}
                value={roomId}
                placeholder={localizationHandler.get("room-id")}
            />

            <div id="skyxplore-game-chat-group-creator-players">
                <table className="formatted-table">
                    <thead>
                        <tr>
                            <th>{localizationHandler.get("available")}</th>
                            <th>{localizationHandler.get("invited")}</th>
                        </tr>
                    </thead>

                    <tbody>
                        {getPlayers()}
                    </tbody>
                </table>
            </div>

            <div id="skyxplore-game-chat-group-create-button-wrapper">
                <Button
                    id="skyxplore-game-chat-group-create-button"
                    label={localizationHandler.get("create")}
                    onclick={create}
                />
            </div>
        </div>
    );
}

export default ChatGroupCreator;