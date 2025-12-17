package io.github.mahjoubech.smartlogiv2.validation.annotation;

import io.github.mahjoubech.smartlogiv2.validation.validator.RegisterRequestValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RegisterRequestValidator.class)
public @interface ValidRegisterRequest {
    String message() default "Données invalides selon le rôle";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}