import React, {useEffect, useRef} from 'react';
import Message from './Message.jsx';

function Chatbox({ messages, newMessage, onNewMessage, onSend, currentUser }) {
    const bottomRef = useRef(null);
    useEffect(() => {
        bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages]);


    return (
        <>
            <div className="border rounded p-3 mb-3"
                 style={{
                     height: '75vh', minWidth: '600px', overflowY: 'auto',
                     display: 'flex', flexDirection: 'column'
                 }}>
                {messages.map((message, index) => (
                    <Message
                        key={index}
                        sender={message.sender}
                        text={message.text}
                        timestamp={message.timestamp}
                        isOwn={message.sender === currentUser}
                    />
                ))}
                <div ref={bottomRef}/>
            </div>

            <form onSubmit={onSend} className="d-flex gap-2">
                <input
                    type="text" className="form-control" value={newMessage}
                    onChange={(e) => onNewMessage(e.target.value)}
                    placeholder="Type your message" required
                />
                <button type="submit" className="btn btn-primary">Send</button>
            </form>
        </>
    );
}

export default Chatbox;
