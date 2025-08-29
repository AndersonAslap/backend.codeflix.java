package com.fullcycle.admin.catalogo.domain.validation.handler;

import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.validation.ValidationHandler;

import java.util.ArrayList;
import java.util.List;

public class NotificationValidationHandler implements ValidationHandler {

    private final List<Error> errors;

    private NotificationValidationHandler(final List<Error> errors) {
        this.errors = errors;
    }

    public static NotificationValidationHandler create() {
        return new NotificationValidationHandler(new ArrayList<>());
    }

    public static NotificationValidationHandler create(final Error error) {
        return new NotificationValidationHandler(new ArrayList<>()).append(error);
    }

    public static NotificationValidationHandler create(final Throwable t) {
        return create(new Error(t.getMessage()));
    }

    @Override
    public NotificationValidationHandler append(final Error error) {
        this.errors.add(error);
        return this;
    }

    @Override
    public NotificationValidationHandler append(final ValidationHandler handler) {
        this.errors.addAll(handler.getErrors());
        return this;
    }

    @Override
    public NotificationValidationHandler validate(final Validation validation) {
        try {
            validation.validate();
        } catch (final DomainException ex) {
            this.errors.addAll(ex.getErrors());
        } catch (final Throwable t) {
            this.errors.add(new Error(t.getMessage()));
        }

        return this;
    }

    @Override
    public List<Error> getErrors() {
        return this.errors;
    }
}
