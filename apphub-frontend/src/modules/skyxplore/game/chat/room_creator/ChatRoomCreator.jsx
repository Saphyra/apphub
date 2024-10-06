import React, { useEffect, useState } from "react";
import "./chat_room_creator.css";
import localizationData from "./chat_room_creator_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import Button from "../../../../../common/component/input/Button";
import InputField from "../../../../../common/component/input/InputField";
import Stream from "../../../../../common/js/collection/Stream";
import NotificationService from "../../../../../common/js/notification/NotificationService";
import { isBlank } from "../../../../../common/js/Utils";
import { SKYXPLORE_GAME_CREATE_CHAT_ROOM, SKYXPLORE_GAME_GET_PLAYERS } from "../../../../../common/js/dao/endpoints/skyxplore/SkyXploreGameEndpoints";

const ChatRoomCreator = ({ setDisplayRoomCreator }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const [roomId, setRoomId] = useState("");
    const [players, setPlayers] = useState([]);
    const [invitedPlayers, setInvitedPlayers] = useState([]);

    useEffect(() => loadPlayers(), []);

    const loadPlayers = () => {
        const fetch = async () => {
            const response = await SKYXPLORE_GAME_GET_PLAYERS.createRequest(null, null, { excludeSelf: true })
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
                className="skyxplore-game-chat-room-creator-player"
                label={player.name}
                onclick={() => invite(player)}
            />)
            .toList();
    }

    const getInvitedPlayers = () => {
        return new Stream(invitedPlayers)
            .map(player => <Button
                key={player.id}
                className="skyxplore-game-chat-room-creator-player"
                label={player.name}
                onclick={() => uninvite(player)}
            />)
            .toList();
    }

    const getPlayers = () => {
        if (players.length === 0) {
            return (
                <tr>
                    <td
                        colSpan={2}
                        id="skyxplore-game-chat-room-creator-no-available-player"
                    >
                        {localizationHandler.get("no-available-player")}
                    </td>
                </tr>
            );
        } else {
            return (
                <tr>
                    <td id="skyxplore-game-chat-room-creator-available-players">{getAvailablePlayers()}</td>
                    <td id="skyxplore-game-chat-room-creator-invited-players">{getInvitedPlayers()}</td>
                </tr>
            );
        }
    }

    const create = async () => {
        if (isBlank(roomId)) {
            NotificationService.showError(localizationHandler.get("room-name-blank"));
            return;
        }

        const members = new Stream(invitedPlayers)
            .map(player => player.id)
            .toList();

        const payload = {
            roomTitle: roomId,
            members: members
        }

        await SKYXPLORE_GAME_CREATE_CHAT_ROOM.createRequest(payload)
            .send();

        setDisplayRoomCreator(false);
    }

    return (
        <div id="skyxplore-game-chat-room-creator">
            <div id="skyxplore-game-chat-room-creator-title">
                {localizationHandler.get("title")}

                <Button
                    id="skyxplore-game-chat-room-creator-close-button"
                    label="X"
                    onclick={() => setDisplayRoomCreator(false)}
                />
            </div>

            <InputField
                id="skyxplore-game-chat-room-creator-name-input"
                type="text"
                onchangeCallback={setRoomId}
                value={roomId}
                placeholder={localizationHandler.get("room-name")}
            />

            <div id="skyxplore-game-chat-room-creator-players">
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

            <div id="skyxplore-game-chat-room-save-button-wrapper">
                <Button
                    id="skyxplore-game-chat-room-save-button"
                    label={localizationHandler.get("create")}
                    onclick={create}
                />
            </div>
        </div>
    );
}

export default ChatRoomCreator;