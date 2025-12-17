package io.github.mahjoubech.smartlogiv2.validation.validator;

import io.github.mahjoubech.smartlogiv2.dto.request.RegisterRequest;
import io.github.mahjoubech.smartlogiv2.model.enums.Roles;
import io.github.mahjoubech.smartlogiv2.validation.annotation.ValidRegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RegisterRequestValidator implements ConstraintValidator<ValidRegisterRequest, RegisterRequest> {
    @Override
    public boolean isValid(RegisterRequest req , ConstraintValidatorContext context ) {
        if(req.getRole() == null) return true;
        if(req.getRole() == Roles.CLIENT){
            if(req.getAdresse() == null || req.getAdresse().isBlank()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Adresse obligatoire pour CLIENT")
                        .addPropertyNode("adresse").addConstraintViolation();
                return false;
            };
        }
        if (req.getRole() == Roles.LIVREUR) {
            boolean valid = true;

            if (req.getVehicule() == null || req.getVehicule().isBlank()) {
                context.buildConstraintViolationWithTemplate("Véhicule obligatoire pour LIVREUR")
                        .addPropertyNode("vehicule")
                        .addConstraintViolation();
                valid = false;
            }

            if (req.getZoneAssigned() == null || req.getZoneAssigned().isBlank()) {
                context.buildConstraintViolationWithTemplate("Zone obligatoire pour LIVREUR")
                        .addPropertyNode("zoneAssigned")
                        .addConstraintViolation();
                valid = false;
            }

            return valid;
        }
        return  true ;
    }
}
