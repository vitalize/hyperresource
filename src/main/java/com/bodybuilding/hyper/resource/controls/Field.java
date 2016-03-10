package com.bodybuilding.hyper.resource.controls;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Field {
    
    private final FieldType type;
    private final String name;
    private final Object value;
    
    private Field(Builder builder) {
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

    public Object getValue() {
        return value;
    }
    
    public static enum FieldType {
        HIDDEN,
        ;
    }

    public static class Builder {
        
        private FieldType type;
        private String name;
        private Object value;
        
        public Field build() {
            return new Field(this);
        }
        
        public Builder type(FieldType type) {
            this.type = type;
            return this;
        }
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder value(Object value) {
            this.value = value;
            return this;
        }
        
    }

}