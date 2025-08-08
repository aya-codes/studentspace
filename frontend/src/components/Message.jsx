import React from 'react';
import dayjs from 'dayjs';

function Message({ sender, text, timestamp, isOwn }) {
    const time = dayjs(timestamp).format('HH:mm');

    return (
        <div className={`d-flex ${isOwn ? 'justify-content-end' : 'justify-content-start'} mb-2`}>
            <div className={`p-2 rounded shadow-sm text-black`}
                style={{
                    maxWidth: '60%', minWidth: '40%', wordBreak: "break-word",
                    backgroundColor: isOwn ? 'lightgreen' : 'lightblue',
                }}
            >
                <div className="small fw-bold">{sender}</div>
                <div>{text}</div>
                <div className="text-end small">{time}</div>
            </div>
        </div>
    );
}

export default Message;
