import React, { useEffect, useState } from "react";
import Button from "../../../../common/component/input/Button";
import InputField from "../../../../common/component/input/InputField";
import Stream from "../../../../common/js/collection/Stream";
import "./contacts/contacts.css"
import ContactsListItem from "./contacts/ContactsListItem";
import ConfirmationDialog from "../../../../common/component/confirmation_dialog/ConfirmationDialog";
import WebSocketEventName from "../../../../common/hook/ws/WebSocketEventName";
import { SKYXPLORE_ACCEPT_FRIEND_REQUEST, SKYXPLORE_ADD_FRIEND, SKYXPLORE_CANCEL_FRIEND_REQUEST, SKYXPLORE_GET_FRIENDS, SKYXPLORE_GET_INCOMING_FRIEND_REQUEST, SKYXPLORE_GET_SENT_FRIEND_REQUEST, SKYXPLORE_REMOVE_FRIEND, SKYXPLORE_SEARCH_FOR_FRIENDS } from "../../../../common/js/dao/endpoints/skyxplore/SkyXploreDataEndpoints";
import useConnectToWebSocket from "../../../../common/hook/ws/WebSocketFacade";
import WebSocketEndpoint from "../../../../common/hook/ws/WebSocketEndpoint";

const Contacts = ({ localizationHandler }) => {
    const [friendQuery, setFriendQuery] = useState("");
    const [shouldDisplaySearchResult, setShouldDisplaySearchResult] = useState(false);
    const [friendCandidates, setFriendCandidates] = useState(null);
    const [incomingFriendRequests, setIncomingFriendRequests] = useState([]);
    const [sentFriendRequests, setSentFriendRequests] = useState([]);
    const [friendships, setFriendships] = useState([]);
    const [removeFriendConfirmationDialogSettings, setRemoveFriendConfirmationDialogSettings] = useState({ shouldDisplay: false });

    useConnectToWebSocket(
        WebSocketEndpoint.SKYXPLORE_MAIN_MENU,
        event => processEvent(event)
    )

    useEffect(() => setShouldDisplaySearchResult(friendQuery.length > 2), [friendQuery]);
    useEffect(() => loadFriendCandidates(), [shouldDisplaySearchResult, friendQuery]);
    useEffect(() => loadIncomingFriendRequests(), []);
    useEffect(() => loadSentFriendRequests(), []);
    useEffect(() => loadFriendships(), []);

    const processEvent = (event) => {
        let list = null;
        switch (event.eventName) {
            case WebSocketEventName.SKYXPLORE_MAIN_MENU_FRIEND_REQUEST_SENT:
                list = incomingFriendRequests.splice();
                list.push(event.payload);
                setIncomingFriendRequests(list);
                break;
            case WebSocketEventName.SKYXPLORE_MAIN_MENU_FRIEND_REQUEST_DELETED:
                removeFriendRequest(event.payload);
                break;
            case WebSocketEventName.SKYXPLORE_MAIN_MENU_FRIEND_REQUEST_ACCEPTED:
                removeFriendRequest(event.payload.friendRequestId);
                list = friendships.splice();
                list.push(event.payload.friendship);
                setFriendships(list);
                break;
            case WebSocketEventName.SKYXPLORE_MAIN_MENU_FRIENDSHIP_DELETED: {
                removeFriendship(event.payload);
                break;
            }
        }
    }

    //Loaders
    const loadFriendCandidates = () => {
        const timeout = setTimeout(
            () => {
                const fetch = async () => {
                    const result = await SKYXPLORE_SEARCH_FOR_FRIENDS.createRequest({ value: friendQuery })
                        .send();

                    setFriendCandidates(result);
                }

                shouldDisplaySearchResult && fetch();
            },
            1000
        );

        setFriendCandidates(null);

        return () => clearTimeout(timeout);
    }

    const loadIncomingFriendRequests = () => {
        const fetch = async () => {
            const result = await SKYXPLORE_GET_INCOMING_FRIEND_REQUEST.createRequest()
                .send();
            setIncomingFriendRequests(result);
        }
        fetch();
    }

    const loadSentFriendRequests = () => {
        const fetch = async () => {
            const result = await SKYXPLORE_GET_SENT_FRIEND_REQUEST.createRequest()
                .send();
            setSentFriendRequests(result);
        }
        fetch();
    }

    const loadFriendships = () => {
        const fetch = async () => {
            const result = await SKYXPLORE_GET_FRIENDS.createRequest()
                .send();
            setFriendships(result);
        }
        fetch();
    }

    //Operations
    const sendFriendRequest = async (characterId) => {
        const newSentFriendRequest = await SKYXPLORE_ADD_FRIEND.createRequest({ value: characterId })
            .send();

        sentFriendRequests.push(newSentFriendRequest);

        setSentFriendRequests(sentFriendRequests);
        setFriendQuery("");
    }

    const cancelFriendRequest = async (friendRequestId) => {
        await SKYXPLORE_CANCEL_FRIEND_REQUEST.createRequest(null, { friendRequestId: friendRequestId })
            .send();
        removeFriendRequest(friendRequestId)
    }

    const acceptFriendRequest = async (friendRequestId) => {
        const newFriendship = await SKYXPLORE_ACCEPT_FRIEND_REQUEST.createRequest(null, { friendRequestId: friendRequestId })
            .send();

        friendships.push(newFriendship);
        setFriendships(friendships);
        removeFriendRequest(friendRequestId)
    }

    const askCancelFriendship = (friendship) => {
        setRemoveFriendConfirmationDialogSettings({
            shouldDisplay: true,
            friendship: friendship
        });
    }

    const cancelFriendship = async (friendshipId) => {
        await SKYXPLORE_REMOVE_FRIEND.createRequest(null, { friendshipId: friendshipId })
            .send();
        removeFriendship(friendshipId);
        setRemoveFriendConfirmationDialogSettings({ shouldDisplay: false });
    }

    const removeFriendship = (friendshipId) => {
        const newFriendships = new Stream(friendships)
            .filter(friendship => friendship.friendshipId !== friendshipId)
            .toList();
        setFriendships(newFriendships);
    }

    const removeFriendRequest = (friendRequestId) => {
        const newSentFriendRequests = new Stream(sentFriendRequests)
            .filter(friendRequest => friendRequest.friendRequestId !== friendRequestId)
            .toList();
        setSentFriendRequests(newSentFriendRequests);

        const newIncomingFruendRequests = new Stream(incomingFriendRequests)
            .filter(friendRequest => friendRequest.friendRequestId !== friendRequestId)
            .toList();
        setIncomingFriendRequests(newIncomingFruendRequests);
    }

    //Processing
    const getSearchResult = () => {
        if (friendCandidates === null) {
            return (
                <div id="skyxplore-character-searching">
                    {localizationHandler.get("searching")}
                </div>
            );
        }

        if (friendCandidates.length === 0) {
            return (
                <div id="skyxplore-character-not-found">
                    {localizationHandler.get("character-not-found")}
                </div>
            );
        }

        return (
            <div id="skyxplore-friend-candidates">
                {
                    new Stream(friendCandidates)
                        .map(friendCandidate =>
                            <Button
                                key={friendCandidate.id}
                                className="skyxplore-friend-candidate"
                                label={friendCandidate.name}
                                onclick={() => { sendFriendRequest(friendCandidate.id) }}
                            />
                        )
                        .toList()
                }
            </div>
        );
    }

    const getIncomingFriendRequests = () => {
        if (incomingFriendRequests.length === 0) {
            return <div className="skyxplore-contacts-list-empty-list">{localizationHandler.get("empty")}</div>;
        }

        return new Stream(incomingFriendRequests)
            .map(friendRequest => (
                <ContactsListItem
                    key={friendRequest.friendRequestId}
                    label={friendRequest.senderName}
                    buttons={[
                        <Button
                            key="accept"
                            className="skyxplore-accept-friend-request-button"
                            onclick={() => { acceptFriendRequest(friendRequest.friendRequestId) }}
                            label={localizationHandler.get("accept")}
                        />,
                        <Button
                            key="cancel"
                            className="skyxplore-cancel-friend-request-button"
                            onclick={() => { cancelFriendRequest(friendRequest.friendRequestId) }}
                            label={localizationHandler.get("cancel")}
                        />
                    ]}
                />
            ))
            .toList();
    }

    const getSentFriendRequests = () => {
        if (sentFriendRequests.length === 0) {
            return <div className="skyxplore-contacts-list-empty-list">{localizationHandler.get("empty")}</div>;
        }

        return new Stream(sentFriendRequests)
            .map(friendRequest => (
                <ContactsListItem
                    key={friendRequest.friendRequestId}
                    label={friendRequest.friendName}
                    buttons={[
                        <Button
                            key="cancel"
                            className="skyxplore-cancel-friend-request-button"
                            onclick={() => { cancelFriendRequest(friendRequest.friendRequestId) }}
                            label={localizationHandler.get("cancel")}
                        />
                    ]}
                />
            ))
            .toList();
    }

    const getFriends = () => {
        if (friendships.length === 0) {
            return <div className="skyxplore-contacts-list-empty-list">{localizationHandler.get("empty")}</div>;
        }

        return new Stream(friendships)
            .map(friendship => (
                <ContactsListItem
                    key={friendship.friendshipId}
                    label={friendship.friendName}
                    buttons={[
                        <Button
                            key="cancel"
                            className="skyxplore-cancel-friendship-button"
                            onclick={() => { askCancelFriendship(friendship) }}
                            label={localizationHandler.get("remove")}
                        />
                    ]}
                />
            ))
            .toList();
    }

    return (
        <div id="skyxplore-contacts">
            <div id="skyxplore-contacts-header">
                <h2>{localizationHandler.get("contacts")}</h2>

                <div id="skyxplore-contacts-search">
                    <InputField
                        id="skyxplore-contacts-search-input"
                        type="text"
                        placeholder={localizationHandler.get("search-friends")}
                        onchangeCallback={setFriendQuery}
                        value={friendQuery}
                    />

                    {shouldDisplaySearchResult && getSearchResult()}
                </div>
            </div>

            <div id="skyxplore-contacts-list">
                <fieldset
                    id="skyxplore-main-menu-incoming-friend-requests"
                    className="skyxplore-contacts-list-group"
                >
                    <legend className="skyxplore-contacts-list-header">{localizationHandler.get("incoming-friend-requests")}</legend>
                    {getIncomingFriendRequests()}
                </fieldset>

                <fieldset
                    id="skyxplore-main-menu-friends"
                    className="skyxplore-contacts-list-group"
                >
                    <legend className="skyxplore-contacts-list-header">{localizationHandler.get("friends")}</legend>
                    {getFriends()}
                </fieldset>

                <fieldset
                    id="skyxplore-main-menu-sent-friend-requests"
                    className="skyxplore-contacts-list-group"
                >
                    <legend className="skyxplore-contacts-list-header">{localizationHandler.get("sent-friend-requests")}</legend>
                    {getSentFriendRequests()}
                </fieldset>
            </div>

            {removeFriendConfirmationDialogSettings.shouldDisplay &&
                <ConfirmationDialog
                    id="skyxplore-remove-friend"
                    title={localizationHandler.get("confirm-remove-friend")}
                    content={localizationHandler.get("confirm-remove-friend-cotnent", { friendName: removeFriendConfirmationDialogSettings.friendship.friendName })}
                    choices={[
                        <Button
                            key="remove"
                            id="skyxplore-remove-friend-button"
                            label={localizationHandler.get("remove")}
                            onclick={() => cancelFriendship(removeFriendConfirmationDialogSettings.friendship.friendshipId)}
                        />,
                        <Button
                            key="cancel"
                            id="skyxplore-remove-friend-button"
                            label={localizationHandler.get("cancel")}
                            onclick={() => setRemoveFriendConfirmationDialogSettings({ shouldDisplay: false })}
                        />
                    ]}
                />
            }
        </div>
    );
}

export default Contacts;