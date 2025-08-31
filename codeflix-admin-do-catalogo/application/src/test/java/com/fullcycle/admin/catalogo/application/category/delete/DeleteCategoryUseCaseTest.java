package com.fullcycle.admin.catalogo.application.category.delete;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteCategoryUseCaseTest {

    @InjectMocks
    private DefaultDeleteCategoryUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @BeforeEach
    public void cleanUp() {
        Mockito.reset(categoryGateway);
    }

    @Test
    public void givenAValidId_whenCallsDeleteCategory_shouldBeOk() {
        final var category = Category.newCategory("Filme", "A categoria mais assistida", true);
        final var expectedId = category.getId();

        doNothing()
                .when(categoryGateway).deleteById(expectedId);

        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        Mockito.verify(categoryGateway, times(1))
                .deleteById(expectedId);
    }

    @Test
    public void givenAInvalidId_whenCallsDeleteCategory_shouldBeOk() {
        final var expectedId = CategoryId.from("123");

        doNothing()
                .when(categoryGateway).deleteById(expectedId);

        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        Mockito.verify(categoryGateway, times(1))
                .deleteById(expectedId);
    }

    @Test
    public void givenAValidId_whenGatewayThrowsException_shouldReturnException() {
        final var category = Category.newCategory("Filme", "A categoria mais assistida", true);
        final var expectedId = category.getId();

        doThrow(new IllegalStateException("Gateway error"))
                .when(categoryGateway).deleteById(expectedId);

        Assertions.assertThrows(IllegalStateException.class, () -> useCase.execute(expectedId.getValue()));

        Mockito.verify(categoryGateway, times(1))
                .deleteById(expectedId);
    }
}
