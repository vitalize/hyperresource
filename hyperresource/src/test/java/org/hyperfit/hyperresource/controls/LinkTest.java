package org.hyperfit.hyperresource.controls;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class LinkTest {

    @Test
    public void verifyHrefRequired() {
        try {
            new Link("rel", null);

            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("href cannot be null or empty"));
        }


        try {
            new Link("rel", "");

            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("href cannot be null or empty"));
        }


        try {
            new Link("rel", "   ");

            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("href cannot be null or empty"));
        }


        //now the other constructors
        try {
            new Link("rel", null, null);

            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("href cannot be null or empty"));
        }


        try {
            new Link("rel", null, null, null);

            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("href cannot be null or empty"));
        }


        try {
            new Link("rel", "", null, null);

            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("href cannot be null or empty"));
        }


        try {
            new Link("rel", "   ", null, null);

            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("href cannot be null or empty"));
        }


    }


    @Test
    public void verifyRelRequired() {
        try {
            new Link(null, "href");

            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("rel cannot be null or empty"));
        }


        try {
            new Link("", "href");

            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("rel cannot be null or empty"));
        }


        try {
            new Link("    ", "href");

            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("rel cannot be null or empty"));
        }


        //now the other constructor
        try {
            new Link(null, "href", null);

            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("rel cannot be null or empty"));
        }


        try {
            new Link(null, "href", null, null);

            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("rel cannot be null or empty"));
        }


        try {
            new Link("", "href", null, null);

            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("rel cannot be null or empty"));
        }


        try {
            new Link("    ", "href", null, null);

            fail("expected exception not thrown");

        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("rel cannot be null or empty"));
        }


    }



    @Test
    public void testConstructSimpleLink() {
        Link link = new Link("rel", "href");
        assertEquals("rel", link.getRel());
        assertEquals("href", link.getHref());
        assertNull(link.getName());
        assertNull(link.getType());
    }

    @Test
    public void testConstructFullLink() {
        Link link = new Link("rel", "href", "name", "type");
        assertEquals("rel", link.getRel());
        assertEquals("href", link.getHref());
        assertEquals("name", link.getName());
        assertEquals("type", link.getType());
    }


}
