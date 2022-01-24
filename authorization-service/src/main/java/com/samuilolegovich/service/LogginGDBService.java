package com.samuilolegovich.service;

import org.slf4j.event.Level;

public interface LogginGDBService {
    void logDbMessage(String message, String operationType, Level level);
}
