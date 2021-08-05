package com.samuilolegovich.service;

import org.slf4j.event.Level;

public interface LoggingDBService {
    void logDbMessage(String message, String operationType, Level level);
}
