import React, { useEffect, useState } from "react";
import Stream from "../../../../common/js/collection/Stream";
import Endpoints from "../../../../common/js/dao/dao";
import PanelTitle from "./PanelTitle";
import "./friends/friends.css"
import Button from "../../../../common/component/input/Button";
import WebSocketEventName from "../../../../common/hook/ws/WebSocketEventName";

const Friends = ({ localizationHandler, lastEvent }) => {
    const [friends, setFriends] = useState([]);

    useEffect(() => loadFriends(), []);
    useEffect(() => handleEvent(), [lastEvent]);

    const loadFriends = () => {
        const fetch = async () => {
            const result = await Endpoints.SKYXPLORE_LOBBY_GET_ACTIVE_FRIENDS.createRequest()
                .send();
            setFriends(result);
        }
        fetch();
    }

    const handleEvent = () => {
        if (lastEvent === null) {
            return;
        }

        if (lastEvent.eventName === WebSocketEventName.SKYXPLORE_LOBBY_USER_ONLINE) {
            const copy = friends.slice();
            copy.push(lastEvent.payload);
            setFriends(copy);
        } else if (lastEvent.eventName === WebSocketEventName.SKYXPLORE_LOBBY_USER_OFFLINE) {
            const copy = new Stream(friends)
                .filter(friend => friend.friendId !== lastEvent.payload.friendId)
                .toList();
            setFriends(copy);
        }
    }

    const inviteFriend = (friendId) => {
        Endpoints.SKYXPLORE_INVITE_TO_LOBBY.createRequest(null, { friendId: friendId })
            .send();
    }

    const getFriends = () => {
        if (friends.length == 0) {
            return (
                <h4 id="skyxplore-lobby-no-active-friend">
                    {localizationHandler.get("no-active-friend")}
                </h4>
            );
        }

        return new Stream(friends)
            .sorted((a, b) => a.friendName.localeCompare(b.friendName))
            .map(friend =>
                <h4
                    key={friend.friendId}
                    className="skyxplore-lobby-active-friend"
                >
                    <span>{friend.friendName}</span>
                    <Button
                        className="skyxplore-lobby-active-friend-invite-button"
                        label="+"
                        onclick={() => inviteFriend(friend.friendId)}
                    />
                </h4>)
            .toList();
    }

    return (
        <div id="skyxplore-lobby-friends" className="skyxplore-lobby-panel">
            <PanelTitle label={localizationHandler.get("friends")} />
            <div
                id="skyxplore-lobby-friends-list"
                className="skyxplore-lobby-panel-content"
            >
                {getFriends()}
            </div>
        </div>
    );
}

export default Friends;