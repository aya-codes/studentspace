package com.ayacodes.studentspace;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public record ArchivedChatroom (
        String roomId,
        Topic topic,
        Duration maxTimeOpen,
        Instant chatStartedAt,
        Instant chatEndedAt,
        List<Message> messages,
        int finalMessageCount,
        boolean closedByUser,
        boolean reportSubmitted,
        Optional<String> reportReason){}