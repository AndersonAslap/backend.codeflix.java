package com.fullcycle.admin.catalogo.domain.category;

import com.fullcycle.admin.catalogo.domain.Identifier;

import java.util.Objects;
import java.util.UUID;

public class CategoryId extends Identifier {

    private final String value;

    private CategoryId(final String value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public static CategoryId unique() {
        return CategoryId.from(UUID.randomUUID());
    }

    public static CategoryId from(final String id) {
        return new CategoryId(id);
    }

    public static CategoryId from(final UUID id) {
        return new CategoryId(id.toString().toLowerCase());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CategoryId that = (CategoryId) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getValue());
    }
}
