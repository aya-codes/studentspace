import React from 'react';

function WaitingScreen({ onCancelWait }) {
    return (
        <div className="d-flex align-items-center justify-content-center vh-100">
            <div className="text-center border p-5 rounded shadow bg-light">
                <h1 className="mb-4 fs-1">Waiting for another user to join</h1>
                <p> This may take some time </p>
                <button onClick={onCancelWait}>Cancel</button>
            </div>
        </div>
    );
}

export default WaitingScreen;
