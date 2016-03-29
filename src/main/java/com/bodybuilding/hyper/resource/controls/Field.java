package com.bodybuilding.hyper.resource.controls;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Field<T> {
    
    private final FieldType type;
    private final String name;
    private final T value;
    
    private Field(Builder<T> builder) {
        this.type = builder.type;
        this.name = builder.name;
        this.value = builder.value;
    }
    
    public FieldType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }
    
    public static enum FieldType {
        HIDDEN,
        ;
    }

    public static class Builder<T> {
        
        private FieldType type;
        private String name;
        private T value;
        
        public Field<T> build() {
            return new Field<T>(this);
        }
        
        public Builder<T> type(FieldType type) {
            this.type = type;
            return this;
        }
        
        public Builder<T> name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder<T> value(T value) {
            this.value = value;
            return this;
        }
        
    }

}