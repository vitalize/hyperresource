package org.hyperfit.hyperresource.controls;


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
            assertThat(e.getMessage(), containsString("name cannot be null or empty"));
        }


        try {
            new HiddenField<Object>("", "href");

            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("name cannot be null or empty"));
        }


        try {
            new HiddenField<Object>("   ", "href");

            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("name cannot be null or empty"));
        }

    }

    @Test
    public void verifyValueRequired() {

        try {
            new HiddenField<String>("name", null);
            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("value cannot be null"));
        }


        //empty strings are ok for value
        new HiddenField<String>("name", "");
        new HiddenField<String>("name", "    ");


    }

    @Test
    public void testType(){
        assertEquals(Field.Type.HIDDEN, new HiddenField<String>("name", "").getType());
        assertEquals(Field.Type.HIDDEN, new HiddenField<Object>("name", new Object()).getType());

    }

    @Test
    public void testField() {

        String name = TestUtils.uniqueString();
        String value = TestUtils.uniqueString();

        Field<String> field = new HiddenField<>(name, value);

        assertEquals(name, field.getName());
        assertTrue(field instanceof HiddenField);
        assertEquals(value, field.getValue());

    }

}