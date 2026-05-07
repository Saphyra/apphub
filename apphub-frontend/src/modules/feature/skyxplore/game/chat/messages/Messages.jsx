import Stream from "common/js/collection/Stream";
import Message from "./message/Message";
import "./messages.css";

const Messages = ({ messages, userId }) => {
    const getMessages = () => {
        return new Stream(messages)
            .reverse()
            .map((message, index) => <Message
                key={index}
                message={message}
                userId={userId}
            />)
            .toList();
    }

    return (
        <div id="skyxplore-game-chat-messages">
            {getMessages()}
        </div>
    );
}

export default Messages;