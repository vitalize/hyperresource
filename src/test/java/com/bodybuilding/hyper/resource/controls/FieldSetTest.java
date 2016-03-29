package com.bodybuilding.hyper.resource.controls;

import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.bodybuilding.commerce.cart.TestHelpers;
import com.bodybuilding.hyper.resource.controls.FieldSet.Builder;

public class FieldSetTest {

    @Test
    @Ignore
    public void iterate() {
        for(int i = 0; i < 100; i++) {
            testFieldSet();
        }
    }
    
    @Test
    public void testFieldSet() {
        
        String name = UUID.randomUUID().toString();
        List<HiddenField> fields = TestHelpers.mockFields();
        
        Builder builder = new FieldSet.Builder().name(name);
        
        fields.forEach(field -> builder.addField(field));
        
        FieldSet fieldSet = builder.build();
        
        Assert.assertEquals(name, fieldSet.getName());
        Assert.assertEquals(fields.size(), fieldSet.getFields().size());
        Assert.assertEquals(fields, fieldSet.getFields());
        
    }
    
}