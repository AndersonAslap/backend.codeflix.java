package com.fullcycle.admin.catalogo.application.category.update;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryId;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.validation.handler.NotificationValidationHandler;
import io.vavr.control.Either;

import java.util.Objects;
import java.util.function.Supplier;

import static io.vavr.API.Left;
import static io.vavr.API.Try;

public class DefaultUpdateCategoryUseCase extends UpdateCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultUpdateCategoryUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Either<NotificationValidationHandler, UpdateCategoryOutput> execute(final UpdateCategoryCommand command) {
        final var categoryId = CategoryId.from(command.id());

        final var category = this.categoryGateway.findById(categoryId)
                .orElseThrow(notFound(categoryId));

        final var notification = NotificationValidationHandler.create();

        category
                .update(command.name(), command.description(), command.isActive())
                .validate(notification);

        return notification.hasError() ? Left(notification) : update(category);
    }

    private Either<NotificationValidationHandler, UpdateCategoryOutput> update(final Category category) {
        return Try(() -> this.categoryGateway.update(category))
                .toEither()
                .bimap(NotificationValidationHandler::create, UpdateCategoryOutput::from);
    }

    private Supplier<DomainException> notFound(final CategoryId categoryId) {
        return () -> DomainException.with(
                new Error("Category with ID %s was not found".formatted(categoryId.getValue()))
        );
    }
}
