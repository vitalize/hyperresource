package com.bodybuilding.hyper.resource.controls;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.util.StringUtils;

@EqualsAndHashCode
@ToString
public abstract class Field<T> {
    
    private final String name;
    private final T value;
    
    protected Field(String name, T value) {

        if(!StringUtils.hasText(name)){
            throw new IllegalArgumentException("name for field cannot be null or empty");
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

}