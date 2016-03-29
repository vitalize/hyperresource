package com.bodybuilding.hyper.resource.controls;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class HiddenFieldTest {

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
        Object value = UUID.randomUUID().toString();
        
        Field field = new HiddenField.Builder().name(name).value(value).build();
        
        Assert.assertEquals(name, field.getName());
        Assert.assertTrue(field instanceof HiddenField);
        Assert.assertEquals(value, field.getValue());
        
    }
    
}