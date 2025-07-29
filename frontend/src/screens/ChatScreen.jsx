import React, {useEffect, useState} from 'react';
import Chatbox from '../components/Chatbox.jsx';
import Timer from '../components/Timer.jsx';

function ChatScreen({ roomId, nicknameOwn, topic, expiresAt, onEndChat, onReport , chatActive }) {
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');

    const backend = import.meta.env.VITE_API_URL;

    useEffect(() => {
        if (!roomId || !chatActive) return;
        let intervalId;

        async function fetchMessages() {
            try {
                const response = await fetch(`${backend}/chat/${roomId}`);
                if (response.status === 410 || response.status === 404) {
                    onEndChat();
                    clearInterval(intervalId);
                    return;
                }
                if (!response.ok) return;

                const data = await response.json();
                const messagesString = data.messages || "";
                const messagesArray = messagesString
                    .split("\n")
                    .filter(Boolean)
                    .map(msg => {
                        const [sender, ...textParts] = msg.split(":");
                        return { sender, text: textParts.join(":").trim() };
                    });

                setMessages(messagesArray);
            } catch (err) {
                console.error("Fetch messages error:", err);
            }
        }

        intervalId = setInterval(fetchMessages, 3000);
        return () => clearInterval(intervalId);  // ✅ now clears the right ID
    }, [roomId, chatActive]);



    const pollRoomStatus = () => {
        // double check before entering
        if (!roomId || !chatActive) return;
        fetch(`${backend}/status/${roomId}`).then(res => {
            if (res.status === 410 || res.status === 404) {
                onEndChat();
            }
        })
    };

    const handleSend = async (e) => {
        pollRoomStatus();
        if (!roomId || !chatActive) return;
        e.preventDefault();
        if (!newMessage.trim()) return;
        const messageObject = {
            sender: nicknameOwn,
            body: newMessage
        };
        try {
            if (!roomId || !chatActive) return;
            const response = await fetch(`${backend}/chat/${roomId}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(messageObject)
            });

            const data = await response.json();

            if (!response.ok) {
                alert("Error: " + (data.message || "Failed to send"));
                return;
            }
            setNewMessage('');
            setMessages(prev => [...prev, messageObject]);
        } catch (err) {
            console.error(err);
        }
    };


    return (
        <div className="container py-4">
            <Timer expiresAt={expiresAt} onExpire={onEndChat} />
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h1 className="mb-0 fs-4">Chat Room: {topic}</h1>
                <button onClick={onReport} className="btn btn-danger justify-content-end">
                    Report & End
                </button>
                <button onClick={onEndChat} className="btn btn-outline-danger">
                    End
                </button>
            </div>

            <Chatbox
                messages={messages}
                newMessage={newMessage}
                onNewMessage={setNewMessage}
                onSend={handleSend}
                currentUser={nicknameOwn}
            />
        </div>
    );
}

export default ChatScreen;
