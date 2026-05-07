import MapStream from "common/js/collection/MapStream";
import { useEffect, useState } from "react";
import MessageInput from "./message_input/MessageInput";
import Messages from "./messages/Messages";
import RoomSelector from "./room_selector/RoomSelector";
import ChatRoomCreator from "./room_creator/ChatRoomCreator";
import "./chat.css";

const Chat = ({
    displayChat,
    setHasUnreadMessage,
    userId,
    sendMessage,
    messages,
    setMessageStatus,
    unreadMessages,
    currentChatRoom,
    setCurrentChatRoom,
    chatRooms,
    setChatRooms
}) => {
    const [displayRoomCreator, setDisplayRoomCreator] = useState(false);

    useEffect(() => updateUnreadMessage(), [messages]);
    useEffect(() => updateHasUnreadMessage(), [unreadMessages]);
    useEffect(() => setMessageStatus(currentChatRoom, false), [currentChatRoom]);
    useEffect(() => setDisplayRoomCreator(false), [displayChat]);
    useEffect(() => setMessageStatus(currentChatRoom, false), [displayChat]);

    const updateUnreadMessage = () => {

    }

    const updateHasUnreadMessage = () => {
        const value = new MapStream(unreadMessages)
            .toListStream()
            .anyMatch(unread => unread);
        setHasUnreadMessage(value);
    }

    if (displayChat) {
        if (!displayRoomCreator) {
            return (
                <div id="skyxplore-game-chat">
                    <MessageInput
                        sendMessage={sendMessage}
                        currentChatRoom={currentChatRoom}
                    />

                    <Messages
                        messages={messages[currentChatRoom]}
                        userId={userId}
                    />

                    <RoomSelector
                        currentChatRoom={currentChatRoom}
                        setCurrentChatRoom={setCurrentChatRoom}
                        unreadMessages={unreadMessages}
                        setDisplayRoomCreator={setDisplayRoomCreator}
                        chatRooms={chatRooms}
                        setChatRooms={setChatRooms}
                    />
                </div>
            );
        } else {
            return <ChatRoomCreator
                setDisplayRoomCreator={setDisplayRoomCreator}
            />
        }
    }
}

export default Chat;