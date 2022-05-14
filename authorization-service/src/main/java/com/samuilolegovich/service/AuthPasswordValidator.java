package com.samuilolegovich.service;

import org.passay.RuleResult;

public interface AuthPasswordValidator {
    RuleResult validate(String password, String username);
}
