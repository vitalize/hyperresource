package com.bodybuilding.hyper.resource.controls;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Test;

import com.bodybuilding.commerce.cart.TestHelpers;
import com.bodybuilding.hyper.resource.controls.FieldSet.Builder;
import test.TestUtils;

public class FieldSetTest {

    
    @Test
    public void testFieldSet() {
        
        String name = TestUtils.randomString();

        List<Field> fields = Arrays.asList(

        );
        
        Builder builder = new FieldSet.Builder().name(name);
        
        fields.forEach(field -> builder.addField(field));
        
        FieldSet fieldSet = builder.build();
        
        assertEquals(name, fieldSet.getName());
        assertEquals(fields.size(), fieldSet.getFields().size());
        assertEquals(fields, fieldSet.getFields());
        
    }
    
}