package com.project.parkrental.security;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;

public class CustomValidationListener implements PreInsertEventListener {

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        try {
            // Hibernate에서 엔티티에 대한 유효성 검사를 수행할 때 발생
            System.out.println("Pre-insert validation for: " + event.getEntity());
            return false;
        } catch (ConstraintViolationException e) {
            // 유효성 검사 실패 시 ConstraintViolation 로그 출력
            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                System.out.println("Validation failed: " + violation.getMessage());
            }
            return true;
        }
    }
}
