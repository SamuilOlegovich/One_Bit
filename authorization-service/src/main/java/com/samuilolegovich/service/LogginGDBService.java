package com.samuilolegovich.service;

import org.slf4j.event.Level;

public interface LoggingDBService {
    void logDbMessageWithCustomUsername(String message, String operationType, String authUser, Level level);

    void logDbMessage(String message, String operationType, Level level);

}
