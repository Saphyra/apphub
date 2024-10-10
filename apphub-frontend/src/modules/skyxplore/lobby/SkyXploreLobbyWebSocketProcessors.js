import MessageType from "./lobby_page/chat/MessageType";
import { hasValue } from "../../../common/js/Utils";
import MapStream from "../../../common/js/collection/MapStream";
import Constants from "../../../common/js/Constants";
import Stream from "../../../common/js/collection/Stream";

export const processAllianceCreatedEvent = (payload, alliances, setAlliances, ais, setAis, players, setPlayers) => {
    const newAlliacnes = new Stream(alliances)
        .add(payload.alliance)
        .toList();
    setAlliances(newAlliacnes);

    const newAi = payload.ai;
    if (hasValue(newAi)) {
        const newAis = new Stream(ais)
            .filter(ai => ai.userId !== newAi.userId)
            .add(newAi)
            .toList();

        setAis(newAis);
    }

    const newPlayer = payload.player;
    if (hasValue(newPlayer)) {
        const copy = new MapStream(players)
            .clone()
            .add(newPlayer.userId, newPlayer)
            .toObject();

        setPlayers(copy);
    }
}

export const processChatSendMessageEvent = (payload, ownUserId, messages, setMessages) => {
    payload.type = payload.senderId === ownUserId ? MessageType.OWN_MESSAGE : MessageType.INCOMING_MESSAGE;

    const copy = new Stream(messages)
        .add(payload)
        .toList();

    setMessages(copy);
}

export const processExitEvent = (payload, localizationHandler, messages, setMessages, players, setPlayers) => {
    if (payload.host) {
        window.location.href = Constants.SKYXPLORE_MAIN_MENU_PAGE;
        return;
    }

    if (!payload.onlyInvited) {
        const message = {
            type: MessageType.SYSTEM_MESSAGE,
            message: localizationHandler.get("player-left-lobby", { name: payload.characterName }),
            createdAt: payload.createdAt
        }

        const newMessages = new Stream(messages)
            .add(message)
            .toList();

        setMessages(newMessages);
    }

    const newPlayers = new MapStream(players)
        .filter(userId => userId !== payload.userId)
        .toObject();

    setPlayers(newPlayers);
}

export const processPlayerConnectedEvent = (payload, localizationHandler, messages, setMessages) => {
    const message = {
        type: MessageType.SYSTEM_MESSAGE,
        message: localizationHandler.get("player-connected", { name: payload.characterName }),
        createdAt: payload.createdAt
    }

    const copy = new Stream(messages)
        .add(message)
        .toList();

    setMessages(copy);
}

export const processPlayerDisconnectedEvent = (payload, localizationHandler, messages, setMessages) => {
    const message = {
        type: MessageType.SYSTEM_MESSAGE,
        message: localizationHandler.get("player-disconnected", { name: payload.characterName }),
        createdAt: payload.createdAt
    }

    const copy = new Stream(messages)
        .add(message)
        .toList();

    setMessages(copy);
}

export const processAiModifiedEvent = (newAi, ais, setAis) => {
    const copy = new Stream(ais)
        .filter(ai => ai.userId !== newAi.userId)
        .add(newAi)
        .toList();

    setAis(copy);
}

export const processAiRemovedEvent = (userId, ais, setAis) => {
    const copy = new Stream(ais)
        .filter(ai => ai.userId !== userId)
        .toList();

    setAis(copy);
}

export const processUserOnlineEvent = (friend, friends, setFriends) => {
    const copy = friends.slice();
    copy.push(friend);
    setFriends(copy);
}

export const processUserOfflineEvent = (friendId, friends, setFriends) => {
    const copy = new Stream(friends)
        .filter(friend => friend.friendId !== friendId)
        .toList();
    setFriends(copy);
}

export const processPlayerModifiedEvent = (newPlayer, players, setPlayers) => {
    const copy = new MapStream(players)
        .clone()
        .add(newPlayer.userId, newPlayer)
        .toObject();

    setPlayers(copy);
}