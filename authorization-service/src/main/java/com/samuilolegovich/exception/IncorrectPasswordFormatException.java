package com.samuilolegovich.exception;

import lombok.Getter;
import org.passay.RuleResult;

public class IncorrectPasswordFormatException extends RuntimeException {
    @Getter
    private final RuleResult ruleResult;

    public IncorrectPasswordFormatException(RuleResult ruleResult) {
        super("Weak password");
        this.ruleResult = ruleResult;
    }
}
