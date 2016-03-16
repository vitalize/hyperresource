package com.bodybuilding.hyper.resource.controls;

import java.util.Random;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.bodybuilding.hyper.resource.controls.Field.FieldType;

public class FieldTest {

    @Test
    @Ignore
    public void iterate() {
        for(int i = 0; i < 100; i++) {
            testField();
        }
    }
    
    @Test
    public void testField() {
        
        String name = UUID.randomUUID().toString();
        FieldType type = FieldType.values()[new Random().nextInt(FieldType.values().length)];
        Object value = UUID.randomUUID().toString();
        
        Field field = new Field.Builder().name(name).type(type).value(value).build();
        
        Assert.assertEquals(name, field.getName());
        Assert.assertEquals(type, field.getType());
        Assert.assertEquals(value, field.getValue());
        
    }
    
}