package org.hyperfit.hyperresource.controls;

import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static org.hyperfit.hyperresource.Preconditions.notEmpty;

@ToString
public abstract class Field<T> {

    private final String name;
    private final T value;

    protected Field(String name, T value) {

        this.name = notEmpty(name, "name");

        //TODO: decide if a fields value can/should ever be null?
        this.value = value;
    }

    @NotNull
    //TODO: find a NOT EMPTY STRING validation rule
    @Size(min = 1)
    public String getName() {
        return name;
    }


    public T getValue() {
        return value;
    }


    //TODO: decide if we like this as enum..it means that nobody could add a new field type..which may be what we want..not sure
    @NotNull
    abstract public Type getType();

    public enum Type {
        HIDDEN
    }
}