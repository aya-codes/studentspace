import React, { useState, useEffect } from 'react';

function Timer({ expiresAt, onExpire }) {
    const [timeLeft, setTimeLeft] = useState(0);

    useEffect(() => {
        if (!expiresAt) {
            setTimeLeft(0);
            return;
        }

        const update = () => {
            const diff = expiresAt - Date.now();
            setTimeLeft(diff > 0 ? diff : 0);
        };

        update(); // initial call

        const interval = setInterval(update, 1000);
        return () => clearInterval(interval);
    }, [expiresAt]);

    const minutes = Math.floor(timeLeft / 60000);
    const seconds = Math.floor((timeLeft % 60000) / 1000);

    const pad = (n) => n.toString().padStart(2, "0");

    return (
        <div>
            {pad(minutes)}:{pad(seconds)}
        </div>
    );
}

export default Timer;