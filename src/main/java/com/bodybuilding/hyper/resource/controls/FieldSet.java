package com.bodybuilding.hyper.resource.controls;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class FieldSet {

    private final String name;
    private final List<HiddenField> fields;
    
    private FieldSet(Builder builder) {
        this.name = builder.name;
        this.fields = builder.fields;
    }
    
    public String getName() {
        return name;
    }

    public List<HiddenField> getFields() {
        return fields;
    }
    
    public static class Builder {
        
        private String name;
        private List<HiddenField> fields = new ArrayList<HiddenField>();
        
        public FieldSet build() {
            return new FieldSet(this);
        }
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder addField(HiddenField field) {
            this.fields.add(field);
            return this;
        }
        
    }

}