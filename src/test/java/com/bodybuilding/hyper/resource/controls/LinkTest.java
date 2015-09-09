package com.bodybuilding.hyper.resource.controls;

import org.junit.Test;
import static org.junit.Assert.*;

public class LinkTest {

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyHref() {
       new Link("rel", "");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNullHref() {
       new Link("rel", null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyRel() {
       new Link("", "href");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNullRel() {
       new Link(null, "href");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFullLinkEmptyHref() {
       new Link("rel", "", "name", "type");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFullLinkNullHref() {
       new Link("rel", null, "name", "type");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFullLinkEmptyRel() {
       new Link("", "href", "name", "type");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFullLinkNullRel() {
       new Link(null, "href", "name", "type");
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
    
    @Test
    public void testEquals() {
        String rel = "rel";
        String href = "href";
        String name = "name";
        String type = "type";
        
        Link left = new Link(rel, href);
        Link right = new Link(rel, href);
        assertEquals(left, left);
        assertEquals(left, right);
        
        left = new Link(rel, href, name, type);
        right = new Link(rel, href, name, type);
        
        assertEquals(left, left);
        assertEquals(left, right);        
    }
}
