package com.bodybuilding.hyper.resource.controls;

import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.bodybuilding.commerce.cart.TestHelpers;
import com.bodybuilding.hyper.resource.controls.TemplatedAction.Builder;

public class TemplatedActionTest {

    @Test
    @Ignore
    public void iterate() {
        for(int i = 0; i < 100; i++) {
            testTemplatedAction();
        }
    }
    
    @Test
    public void testTemplatedAction() {
        
        String name = UUID.randomUUID().toString();
        List<FieldSet> fieldSets = TestHelpers.mockFieldSets();
        String href = UUID.randomUUID().toString();
        
        Builder builder = new TemplatedAction.Builder().name(name).href(href);
        fieldSets.forEach(fieldSet -> builder.addFieldSet(fieldSet));
        
        TemplatedAction templatedAction = builder.build();
        
        Assert.assertEquals(name, templatedAction.getName());
        Assert.assertEquals(href, templatedAction.getHref());
        Assert.assertEquals(fieldSets.size(), templatedAction.getFieldSets().size());
        Assert.assertEquals(fieldSets, templatedAction.getFieldSets());
        
    }
    
    @Test
    public void testTemplatedActionNameBuiltWithRequired() {
        
        String name = UUID.randomUUID().toString();
        String href = UUID.randomUUID().toString();
        
        new TemplatedAction.Builder().name(name).href(href).build();
        
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testTemplatedActionNameRequired() {
        
        String name = null;
        String href = UUID.randomUUID().toString();
        
        new TemplatedAction.Builder().name(name).href(href).build();
        
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testTemplatedActionHrefRequired() {
        
        String name = UUID.randomUUID().toString();
        String href = null;
        
        new TemplatedAction.Builder().name(name).href(href).build();
        
    }
    

    
}