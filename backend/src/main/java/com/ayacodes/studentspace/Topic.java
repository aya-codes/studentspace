package com.ayacodes.studentspace;

public enum Topic {
    FRIENDS,
    WORK,
    STRESS,
    RELATIONSHIPS,
    HOBBIES,
    MOVIES,
    SHOWS,
    MUSIC,
    POLITICS,
    RELIGION,
    PHILOSOPHY,
    MOTIVATION,
    PRODUCTIVITY,
    SELFHELP,
    CODING


    /* for the evaluation, I used the following topics:
    ACTIVITY4, //STRICT: flags grey area. API set to toxicity >= 0.4
    ACTIVITY8, //GENTLE: flags only high toxicity. API set to toxicity >= 0.8
    ACTIVITY10 //CONTROL: does not flag anything. API set to toxicity > 1.0, which does not exist
     */
}