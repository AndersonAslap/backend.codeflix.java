package com.fullcycle.admin.catalogo.application.category.update;

import com.fullcycle.admin.catalogo.application.category.create.CreateCategoryCommand;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryId;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateCategoryUseCaseTest {

    @InjectMocks
    private DefaultUpdateCategoryUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    // 1. Teste do caminho feliz
    // 2. Teste passando uma propiedade inválida (name)
    // 3. Teste atualizando uma categoria para inativa
    // 4. Teste simulando um erro genérico vindo do gateway
    // 5. Teste atualizar categoria passando ID inválido

    @Test
    public void givenAValidCommand_whenCallsUpdateCategory_shouldReturnCategoryId() {
        final var category = Category.newCategory("Film", null, true);
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = category.getId();

        final var command = UpdateCategoryCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive
        );

        when(categoryGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(category.clone()));

        when(categoryGateway.update(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final var output = useCase.execute(command).get();

        Assertions.assertNotNull(output);
        Assertions.assertNotNull(output.id());

        Mockito.verify(categoryGateway, times(1)).findById(eq(expectedId));

        Mockito.verify(categoryGateway, times(1)).update(argThat(
                updateCategory ->
                        Objects.equals(expectedName, updateCategory.getName())
                            && Objects.equals(expectedDescription, updateCategory.getDescription())
                            && Objects.equals(expectedIsActive, updateCategory.isActive())
                            && Objects.equals(expectedId, updateCategory.getId())
                            && Objects.equals(category.getCreatedAt(), updateCategory.getCreatedAt())
                            && category.getUpdatedAt().isBefore(updateCategory.getUpdatedAt())
                            && Objects.isNull(updateCategory.getDeletedAt())
        ));
    }

    @Test
    public void givenAInvalidName_whenCallsUpdateCategory_thenShouldReturnDomainException() {
        final var category = Category.newCategory("Film", null, true);

        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = category.getId();

        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        when(categoryGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(category.clone()));

        final var command =
                UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);

        final var notification = useCase.execute(command).getLeft();

        Assertions.assertEquals(expectedErrorCount, notification.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, notification.getErrors().get(0).message());

        Mockito.verify(categoryGateway, times(0)).create(any());
    }

    @Test
    public void givenAValidInactivateCommand_whenCallsUpdateCategory_shouldReturnInactiveCategoryId() {
        final var category = Category.newCategory("Film", null, true);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;
        final var expectedId = category.getId();

        final var command = UpdateCategoryCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive
        );

        when(categoryGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(category.clone()));

        when(categoryGateway.update(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Assertions.assertTrue(category.isActive());
        Assertions.assertNull(category.getDeletedAt());

        final var output = useCase.execute(command).get();

        Assertions.assertNotNull(output);
        Assertions.assertNotNull(output.id());

        Mockito.verify(categoryGateway, times(1)).findById(eq(expectedId));

        Mockito.verify(categoryGateway, times(1)).update(argThat(
                updateCategory ->
                        Objects.equals(expectedName, updateCategory.getName())
                                && Objects.equals(expectedDescription, updateCategory.getDescription())
                                && Objects.equals(expectedIsActive, updateCategory.isActive())
                                && Objects.equals(expectedId, updateCategory.getId())
                                && Objects.equals(category.getCreatedAt(), updateCategory.getCreatedAt())
                                && category.getUpdatedAt().isBefore(updateCategory.getUpdatedAt())
                                && Objects.nonNull(updateCategory.getDeletedAt())
        ));
    }

    @Test
    public void givenAValidCommand_whenGatewayThrowsRandomException_shouldReturnAException() {
        final var category = Category.newCategory("Film", null, true);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = category.getId();

        final var expectedErrorMessage = "Gateway error";
        final var expectedErrorCount = 1;

        final var command =
                UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);

        when(categoryGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(category.clone()));

        when(categoryGateway.update(any()))
                .thenThrow(new IllegalStateException(expectedErrorMessage));

        final var notification = useCase.execute(command).getLeft();

        Assertions.assertEquals(expectedErrorCount, notification.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, notification.getErrors().get(0).message());

        Mockito.verify(categoryGateway, times(1))
                .update(argThat((categoryUpdate -> {
                            return Objects.equals(expectedName, categoryUpdate.getName())
                                    && Objects.equals(expectedDescription, categoryUpdate.getDescription())
                                    && Objects.equals(expectedIsActive, categoryUpdate.isActive())
                                    && Objects.nonNull(categoryUpdate.getId())
                                    && Objects.nonNull(categoryUpdate.getCreatedAt())
                                    && Objects.nonNull(categoryUpdate.getUpdatedAt())
                                    && Objects.isNull(categoryUpdate.getDeletedAt());
                            }
                        )
                ));
    }

    @Test
    public void givenACommandWithInvalidId_whenCallUpdateCategory_shouldReturnNotFoundException() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = "123";

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "Category with ID 123 was not found";

        final var command =
                UpdateCategoryCommand.with(expectedId, expectedName, expectedDescription, expectedIsActive);

        when(categoryGateway.findById(eq(CategoryId.from(expectedId))))
                .thenReturn(Optional.empty());

        final var actualException =
                Assertions.assertThrows(DomainException.class, () -> useCase.execute(command));

        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        Mockito.verify(categoryGateway, times(1))
                .findById(eq(CategoryId.from(expectedId)));

        Mockito.verify(categoryGateway, times(0))
                .update(any());
    }
}
