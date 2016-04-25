package com.bodybuilding.hyper.resource.controls;


import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import test.TestUtils;

public class HiddenFieldTest {

    @Test
    public void verifyNameRequired() {

        try {
            new HiddenField<Object>(null, "href");
            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("name"));
        }


        try {
            new HiddenField<Object>("", "href");

            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("name"));
        }


        try {
            new HiddenField<Object>("   ", "href");

            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("name"));
        }

    }

    @Test
    public void verifyValueRequired() {

        try {
            new HiddenField<String>("name", null);
            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("value"));
        }


        //empty strings are ok for value
        new HiddenField<String>("name", "");
        new HiddenField<String>("name", "    ");


    }

    
    @Test
    public void testField() {
        
        String name = TestUtils.randomString();
        String value = TestUtils.randomString();
        
        Field<String> field = new HiddenField<>(name, value);

        assertEquals(name, field.getName());
        assertTrue(field instanceof HiddenField);
        assertEquals(value, field.getValue());
        
    }
    
}