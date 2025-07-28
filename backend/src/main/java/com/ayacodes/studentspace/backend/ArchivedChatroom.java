package com.ayacodes.studentspace.backend;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public record ArchivedChatroom (
        String roomId,
        Topic topic,
        Duration maxTimeOpen,
        Instant chatStartedAt,
        Instant chatEndedAt,
        int finalMessageCount,
        boolean closedByUser,
        boolean reportSubmitted,
        Optional<String> reportReason){}