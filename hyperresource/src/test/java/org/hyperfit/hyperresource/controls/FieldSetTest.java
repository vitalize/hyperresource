package org.hyperfit.hyperresource.controls;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import test.TestUtils;

public class FieldSetTest {

    //There's nothing that's rquired on a field set right now...so no verifyXXXRequired like tests are present currently


    @Test
    public void testFieldSetBuilderNoFields() {

        String fakeName = TestUtils.uniqueString();

        FieldSet fieldSet = new FieldSet.Builder()
            .name(fakeName)
            .build();

        assertEquals(fakeName, fieldSet.getName());
        assertThat(fieldSet.getFields(), empty());

    }



    @Test
    public void testFieldSetBuilderSomeFields() {

        int numberOfFields = TestUtils.randomInt(10) + 1;


        String fakeName = TestUtils.uniqueString();
        List<Field> fields = IntStream.range(1, numberOfFields + 1)
            .mapToObj(
                i -> new HiddenField<>("field " + i, i)
            )
            .collect(Collectors.toList());



        FieldSet.Builder builder = new FieldSet.Builder().name(fakeName);

        fields.forEach(field -> builder.addField(field));

        FieldSet fieldSet = builder.build();

        assertEquals(fakeName, fieldSet.getName());
        assertThat(fieldSet.getFields(), hasSize(numberOfFields));
        assertEquals(fields, fieldSet.getFields());

    }

}