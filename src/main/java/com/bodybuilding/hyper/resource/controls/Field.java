package com.bodybuilding.hyper.resource.controls;

import lombok.ToString;
import org.springframework.util.StringUtils;


@ToString
public abstract class Field<T> {
    
    private final String name;
    private final T value;
    
    protected Field(String name, T value) {

        if(!StringUtils.hasText(name)){
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        this.name = name;

        //TODO: decide if a fields value can/should ever be null?
        this.value = value;
    }
    
    public String getName() {
        return name;
    }
    
    public T getValue() {
        return value;
    }


    //TODO: decide if we like this as enum..it means that nobody could add a new field type..which may be what we want..not sure
    abstract public Type getType();

    public enum Type {
        HIDDEN
    }
}