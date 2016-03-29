package com.bodybuilding.hyper.resource.controls;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public abstract class Field<T> {
    
    private final String name;
    private final T value;
    
    protected Field(String name, T value) {
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return name;
    }
    
    public T getValue() {
        return value;
    }

}