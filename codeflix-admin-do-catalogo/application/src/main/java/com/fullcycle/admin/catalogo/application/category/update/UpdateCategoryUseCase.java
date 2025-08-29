package com.fullcycle.admin.catalogo.application.category.update;

import com.fullcycle.admin.catalogo.application.UseCase;
import com.fullcycle.admin.catalogo.domain.validation.handler.NotificationValidationHandler;
import io.vavr.control.Either;

public abstract class UpdateCategoryUseCase
        extends UseCase<UpdateCategoryCommand, Either<NotificationValidationHandler, UpdateCategoryOutput>> {
}
