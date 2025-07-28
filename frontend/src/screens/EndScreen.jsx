import React from 'react';

function EndScreen({ onStartAgain }) {
    return (
        <div className="d-flex align-items-center justify-content-center vh-100">
            <div className="text-center border p-5 rounded shadow bg-light">
                <h1 className="mb-4 fs-1">This chat has ended</h1>
                <button onClick={onStartAgain} className="btn btn-primary">
                    Start Again
                </button>
            </div>
        </div>
    );
}

export default EndScreen;
