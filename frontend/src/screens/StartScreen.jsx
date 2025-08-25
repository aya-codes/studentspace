import React, { useState } from 'react';
import {ChangeEvent} from "react";

function StartScreen({ onStartChat }) {

    const [nickname, setNickname] = useState('');
    const [topic, setTopic] = useState('');
    const handleSubmit = (e) => {
        e.preventDefault();
        onStartChat(nickname, topic);
    }

    return (
    <div className="d-flex align-items-center justify-content-center vh-100">
            <form onSubmit={handleSubmit} className="border p-4 rounded shadow bg-light"
                  style={{ minWidth: '800px' }}>
                <h1 className="text-center mb-4"> Let's Chat </h1>
                <div className="mb-3">
                    <label htmlFor="nickname" className="form-label">Choose your nickname:</label>
                    <input type="text" id="nickname" name="nickname" required maxLength="15"
                           className="form-control"
                           onChange={(e) => setNickname(e.target.value)} />
                </div>

                <div className="mb-4">
                    <label htmlFor="topic-select" className="form-label">Choose your chat topic:</label>
                    <select onChange={(e) => setTopic(e.target.value)}
                            required name="topic" id="topic-select" className="form-select">
                        <option value=""></option>
                        <option value="FRIENDS">Friends</option>
                        <option value="WORK">Work</option>
                        <option value="STRESS">Stress</option>
                        <option value="RELATIONSHIPS">Relationships</option>
                        <option value="HOBBIES">Hobbies</option>
                        <option value="MOVIES">Movies</option>
                        <option value="SHOWS">Shows</option>
                        <option value="MUSIC">Music</option>
                        <option value="CODING">Coding</option>
                        <option value="POLITICS">Politics</option>
                        <option value="RELIGION">Religion</option>
                        <option value="PHILOSOPHY">Philosophy</option>
                        <option value="MOTIVATION">Motivation</option>
                        <option value="PRODUCTIVITY">Productivity</option>
                        <option value="SELFHELP">Self Help</option>
                    </select>
                </div>

                <button type="submit" className="btn btn-primary w-100"> Start Chat</button>
            </form>
    </div>
    );
}

export default StartScreen;
