package org.hyperfit.hyperresource.controls;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import test.TestUtils;

public class TemplatedActionTest {



    @Test
    public void verifyNameRequired() {

        try {
            new TemplatedAction.Builder()
                .name(null)
                .href("href")
                .build();

            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("name cannot be null or empty"));
        }


        try {
            new TemplatedAction.Builder()
                .name("")
                .href("href")
                .build();

            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("name cannot be null or empty"));
        }


        try {
            new TemplatedAction.Builder()
                .name("    ")
                .href("href")
                .build();

            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("name cannot be null or empty"));
        }

    }

    @Test
    public void verifyHrefRequired() {

        try {
            new TemplatedAction.Builder()
                .name("name")
                .href(null)
                .build();

            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("href cannot be null or empty"));
        }


        try {
            new TemplatedAction.Builder()
                .name("name")
                .href("")
                .build();

            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("href cannot be null or empty"));
        }


        try {
            new TemplatedAction.Builder()
                .name("name")
                .href("    ")
                .build();

            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("href cannot be null or empty"));
        }


    }


    @Test
    public void testTemplatedActionBuilderNoFieldSets() {

        String fakeName = TestUtils.uniqueString();
        String fakeHref = TestUtils.uniqueString();


        TemplatedAction.Builder builder = new TemplatedAction.Builder()
            .name(fakeName)
            .href(fakeHref);


        TemplatedAction templatedAction = builder.build();

        assertEquals(fakeName, templatedAction.getName());
        assertEquals(fakeHref, templatedAction.getHref());
        assertThat(templatedAction.getFieldSets(), empty());

    }


    @Test
    public void testTemplatedActionBuilderRandomFieldSets() {

        int numberOfFieldSets = TestUtils.randomInt(10) + 1;


        String fakeName = TestUtils.uniqueString();
        String fakeHref = TestUtils.uniqueString();
        List<FieldSet> fieldSets = IntStream.range(1, numberOfFieldSets + 1)
            .mapToObj(
                i -> new FieldSet.Builder().name("field set " + i).build()
            )
            .collect(Collectors.toList());

        TemplatedAction.Builder builder = new TemplatedAction.Builder()
            .name(fakeName)
            .href(fakeHref);
        fieldSets.forEach(builder::addFieldSet);

        TemplatedAction templatedAction = builder.build();

        assertEquals(fakeName, templatedAction.getName());
        assertEquals(fakeHref, templatedAction.getHref());
        assertThat(templatedAction.getFieldSets(), hasSize(numberOfFieldSets));
        assertEquals(fieldSets, templatedAction.getFieldSets());

    }


}