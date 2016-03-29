package com.bodybuilding.hyper.resource.controls;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper=true)
@ToString(callSuper=true)
public class HiddenField<T> extends Field<T> {
    
    private HiddenField(Builder<T> builder) {
        super(builder.name, builder.value);
    }
    
    public static class Builder<T> {
        
        private String name;
        private T value;
        
        public HiddenField<T> build() {
            return new HiddenField<T>(this);
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