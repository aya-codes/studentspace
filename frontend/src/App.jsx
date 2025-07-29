import React, {useEffect, useState} from 'react';
import StartScreen from "./screens/StartScreen.jsx";
import ChatScreen from "./screens/ChatScreen.jsx";
import EndScreen from "./screens/EndScreen.jsx";
import WaitingScreen from "./screens/WaitingScreen.jsx";

function App() {
    const [screen, setScreen] = useState("start");
    const [nicknameOwn, setNicknameOwn] = useState("");
    const [nicknameOther, setNicknameOther] = useState("");
    const [topic, setTopic] = useState("");
    const [roomId, setRoomId] = useState(null);
    const [expiresAt, setExpiresAt] = useState('');

    const backend = import.meta.env.VITE_API_URL;

    const handleStartChat = async (nickname, topic) => {
        console.log("API URL:", `${backend}/start`);
        try {
            const res = await fetch('${backend}/start' + {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username: nickname, topic })
            });
            console.log("Starting chat...");
            if (!res.ok) return alert("Error: " + (await res.text()));
            const { roomId, status } = await res.json();
            setRoomId(roomId);
            setNicknameOwn(nickname);
            setTopic(topic);


            if (status === "ok") {
                setScreen("chat");
            } else if (status === "waiting") {
                setScreen("waiting");
                pollRoomStatus(roomId, () => {
                    // double check before entering
                    fetch(`${backend}/status/${roomId}`).then(res => {
                        if (res.status === 410 || res.status === 404) {
                            setScreen("end");
                        } else {
                            setScreen("chat");
                        }
                    });
                });
            }

        } catch (err) {
            console.error(err);
            alert("Failed to start chat");
        }
    };

    const fetchExpiry = async () => {
        try {
            const response = await fetch(`${backend}/chat/${roomId}/expiry`);
            console.log("Expiry API raw response:", response); // Debug

            if (response.status === 410 || response.status === 404) {
                await handleEndChat();
                return;
            }
            const data = await response.json();
            console.log("Expiry API parsed data:", data); // Debug

            if (data.status === "waiting") {
                console.log("Still waiting for match");
                return;
            }
            if (data.expiry) {
                console.log("Setting expiry to:", data.expiry);
                setExpiresAt(Number(data.expiry));
            } else {
                console.warn("No expiry field in response!");
            }
        } catch (err) {
            console.error("Failed to fetch expiry:", err);
        }
    };


    useEffect(() => {
        if (!roomId || screen !== 'chat') return;

        //const interval = setInterval(fetchExpiry, 5000);  // check every 5s
        fetchExpiry(); // immediate fetch on mount

        //return () => clearInterval(interval);
    }, [roomId, screen]);


    const pollRoomStatus = (roomId, onReady) => {
        const poll = setInterval(async () => {
            try {
                const res = await fetch(`${backend}/status/${roomId}`);
                const { status } = await res.json();
                if (status === "ok") {
                    clearInterval(poll);
                    onReady();
                }
            } catch (err) {
                console.error("Polling error:", err);
            }
        }, 2000);
    };

    const handleEndChat = async () => {
        if (!roomId || screen !== 'chat') return;
        try {
            const response = await fetch(`${backend}/chat/${roomId}/end`);
            setScreen("end");
        } catch (err) {
            console.error(err);
            alert("Error ending chat.");
        }
    };

    const handleReport = async () => {
        const reason = window.prompt("Enter a reason for reporting:");
        if (!reason) return;
        try {
            const response = await fetch(`${backend}/chat/${roomId}/report`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(reason),
            });
            alert("Reported and ended chat.");
            setScreen("end");
        } catch (err) {
            console.error(err);
            alert("Error submitting report.");
        }
    };

    useEffect(() => {
        if (!roomId || screen !== 'chat') return;
        const interval = setInterval(async () => {
            try {
                const response = await fetch(`${backend}/status/${roomId}`);
                //Expired or closed room
                if (response.status === 410 || response.status === 404) {
                    await handleEndChat();
                    clearInterval(interval);
                }
            } catch (err) {
                console.error("Status check error:", err);
                await handleEndChat();
            }
        }, 5000);

        return () => clearInterval(interval);
    }, [roomId]);


    const handleReset = () => {
        setNicknameOwn("");
        setNicknameOther("");
        setTopic("");
        setRoomId(null);
        setScreen("start");
    }

    return (
        <>
            {screen === "start" && <StartScreen onStartChat={handleStartChat} />}
            {screen === "waiting" && <WaitingScreen />}
            {screen === "chat" && <ChatScreen
                roomId={roomId} nicknameOwn={nicknameOwn}
                topic={topic} expiresAt={expiresAt}
                onEndChat={handleEndChat} onReport={handleReport}
                chatActive={screen === 'chat'}/>}
            {screen === "end" && <EndScreen onStartAgain={handleReset}/>}
        </>
    );
}

export default App;
