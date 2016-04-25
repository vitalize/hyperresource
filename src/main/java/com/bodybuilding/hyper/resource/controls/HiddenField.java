package com.bodybuilding.hyper.resource.controls;

import lombok.ToString;

@ToString(callSuper=true)
public class HiddenField<T> extends Field<T> {
    
    public HiddenField(String name, T value) {
        super(name, value);

        //Empty values are ok (but unlikely) however null is not useful for hidden fields as far as i can tell
        if(value == null){
            throw new IllegalArgumentException("value cannot be null");
        }
    }

    @Override
    public Type getType() {
        return Type.HIDDEN;
    }

}