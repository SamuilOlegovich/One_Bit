package com.samuilolegovich.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, METHOD, TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = com.samuilolegovich.validation.ChallengeQuestionValidation.class)
public @interface ChallengeQuestionConstraint {
    String message() default "Недействительный запрос контрольных вопросов.";

    Class<? extends Payload>[] payload() default {};

    Class<?>[] groups() default {};
}
