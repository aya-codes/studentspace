Status: In development (Currently MVP stage)

Overview:
This is an ongoing MSc Computer Science project (as part of a dissertation) exploring how to design an anonymous chat platform for students that balances privacy with basic safety measures. 
The focus is on creating a simple, minimal platform that avoids long-term data storage and does not require user accounts.

Aims
- Create a functioning prototype that allows anonymous, topic-based one-on-one chat sessions
- Limit data collection and storage to the bare minimum
- Explore how design choices can reduce the risk of misuse without compromising user anonymity

Current Features
- Anonymous matching based on selected topic
- One-on-one chat rooms that expire after 30 minutes
- REST API with Java and Spring Boot (GET/POST endpoints)
- In-memory storage only; messages are deleted after sessions end
- Unit tests written using JUnit
- Frontend using Vue.js

Stack
- Backend: Java, Spring Boot
- Frontend: Vue.js (WIP)
- Testing: JUnit
- Version Control: Git, GitHub

Notes
- The project is still in development. Functionality and architecture may change.
- No persistent database is used. All data is handled in memory and cleared after each session.

