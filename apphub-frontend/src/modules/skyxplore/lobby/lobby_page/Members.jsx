import React, { useEffect, useState } from "react";
import MapStream from "../../../../common/js/collection/MapStream";
import Stream from "../../../../common/js/collection/Stream";
import Endpoints from "../../../../common/js/dao/dao";
import WebSocketEventName from "../../../../common/js/ws/WebSocketEventName";
import Member from "./members/Member";
import PanelTitle from "./PanelTitle";

const Members = ({ localizationHandler, alliances, isHost, lastEvent, lobbyType }) => {
    const [members, setMembers] = useState({});

    useEffect(() => loadMembers(), []);
    useEffect(() => handleEvent(), [lastEvent]);


    const handleEvent = () => {
        if (lastEvent === null) {
            return;
        }

        if (lastEvent.eventName === WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED) {
            console.log("Lobby Member modified", lastEvent.payload);
            const newMember = lastEvent.payload;

            const copy = new MapStream(members)
                .clone()
                .add(newMember.userId, newMember)
                .toObject();

            setMembers(copy);
        } else if (lastEvent.eventName === WebSocketEventName.SKYXPLORE_LOBBY_EXIT) {
            const copy = new MapStream(members)
                .filter(userId => userId !== lastEvent.payload.userId)
                .toObject();

            setMembers(copy);
        } else if (lastEvent.eventName === WebSocketEventName.SKYXPLORE_LOBBY_ALLIANCE_CREATED) {
            const newMember = lastEvent.payload.lobbyMember;

            if (newMember === null) {
                return;
            }

            const copy = new MapStream(members)
                .clone()
                .add(newMember.userId, newMember)
                .toObject();

            setMembers(copy);
        }
    }

    //Load
    const loadMembers = () => {
        const fetch = async () => {
            const result = await Endpoints.SKYXPLORE_LOBBY_GET_MEMBERS.createRequest()
                .send();
            const memberMap = new Stream(result)
                .toMap((lobbyMember) => lobbyMember.userId);

            setMembers(memberMap);
        }
        fetch();
    }

    const getMembers = () => {
        return new MapStream(members)
            .map((userId, lobbyMember) =>
                <Member
                    key={lobbyMember.userId}
                    lobbyMember={lobbyMember}
                    localizationHandler={localizationHandler}
                    alliances={alliances}
                    isHost={isHost}
                    lobbyType={lobbyType}
                />
            )
            .toList();
    }

    return (
        <div id="skyxplore-lobby-members" className="skyxplore-lobby-panel">
            <PanelTitle label={localizationHandler.get("members")} />

            <div className="skyxplore-lobby-panel-content">
                {getMembers()}
            </div>
        </div>
    );
}

export default Members;