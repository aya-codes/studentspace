package com.ayacodes.studentspace;

import java.time.Duration;
import java.time.Instant;

public record ArchivedChatroom (
    String roomId,
    Topic topic,
    Duration maxTimeOpen,
    Instant chatStartedAt,
    Instant chatEndedAt,
    int finalMessageCount,
    boolean closedByUser){}