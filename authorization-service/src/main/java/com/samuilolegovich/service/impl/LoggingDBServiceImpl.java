package com.samuilolegovich.service.impl;

import com.samuilolegovich.service.LoggingDBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;

public class LoggingDBServiceImpl implements LoggingDBService {
    private static final Logger authDBLogger = LoggerFactory.getLogger("dbLogger");

    private static final String FIVE_PARAMS_PATTER = "{} {} {} {} {}";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    private static final String LOCALHOST_IPV4 = "127.0.0.1";



    private static HttpServletRequest getCurrentHttpRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }

        return null;
    }



    @Override
    public void logDbMessage(String message, String operationType, Level level) {
        switch (level) {
            default:
            case INFO:
                authDBLogger.info(FIVE_PARAMS_PATTER, message, SecurityContextHolder.getContext().getAuthentication().getName(),
                        getClientIp(), operationType, LocalDateTime.now());
                break;
            case WARN:
                authDBLogger.warn(FIVE_PARAMS_PATTER, message, SecurityContextHolder.getContext().getAuthentication().getName(),
                        getClientIp(), operationType, LocalDateTime.now());
                break;
        }
    }



    @Override
    public void logDbMessageWithCustomUsername(String message, String operationType, String authUser, Level level) {
        switch (level) {
            default:
            case INFO:
                authDBLogger.info(FIVE_PARAMS_PATTER, message, authUser,
                        getClientIp(), operationType, LocalDateTime.now());
                break;
            case WARN:
                authDBLogger.warn(FIVE_PARAMS_PATTER, message, authUser,
                        getClientIp(), operationType, LocalDateTime.now());
                break;
        }
    }



    private String getClientIp() {
        HttpServletRequest request = getCurrentHttpRequest();
        String ipAddress;

        if (!isNull(request)) {
            ipAddress = request.getHeader("X-Forwarded-For");
            if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }

            if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }

            if (StringUtils.isEmpty(ipAddress) || LOCALHOST_IPV6.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (LOCALHOST_IPV4.equals(ipAddress) || LOCALHOST_IPV6.equals(ipAddress)) {
                    ipAddress = getIpFromInetAddress();
                }
            }
        } else { ipAddress = getIpFromInetAddress(); }

        if (!StringUtils.isEmpty(ipAddress)
                && ipAddress.length() > 15
                && ipAddress.contains(",")) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
        }

        return ipAddress;
    }



    private String getIpFromInetAddress() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) { e.printStackTrace(); }
        return "";
    }
}
