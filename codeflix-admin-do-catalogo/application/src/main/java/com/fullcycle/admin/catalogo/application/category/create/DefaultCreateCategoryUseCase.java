package com.fullcycle.admin.catalogo.application.category.create;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryId;
import com.fullcycle.admin.catalogo.domain.validation.handler.NotificationValidationHandler;
import io.vavr.API;
import io.vavr.control.Either;

import java.util.Objects;

import static io.vavr.API.Left;
import static io.vavr.API.Try;

public class DefaultCreateCategoryUseCase extends CreateCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultCreateCategoryUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Either<NotificationValidationHandler, CreateCategoryOutput> execute(final CreateCategoryCommand command) {
        final var notification = NotificationValidationHandler.create();
        final var category = Category.newCategory(command.name(), command.description(), command.isActive());
        category.validate(notification);

        return notification.hasError() ? Left(notification) : create(category);
    }

    private Either<NotificationValidationHandler, CreateCategoryOutput> create(final Category category) {
        return Try(() -> this.categoryGateway.create(category))
                .toEither()
                .bimap(NotificationValidationHandler::create, CreateCategoryOutput::from);
    }
}
