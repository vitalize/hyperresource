package com.bodybuilding.hyper.resource.controls;

import java.util.ArrayList;
import java.util.Collection;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class FieldSet {

    private final String name;
    private final Collection<Field> fields;
    
    private FieldSet(Builder builder) {
        this.name = builder.name;
        this.fields = builder.fields;
    }
    
    public String getName() {
        return name;
    }

    public Collection<Field> getFields() {
        return fields;
    }
    
    public static class Builder {
        
        private String name;
        private Collection<Field> fields = new ArrayList<Field>();
        
        public FieldSet build() {
            return new FieldSet(this);
        }
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder addField(Field field) {
            this.fields.add(field);
            return this;
        }
        
    }

}