package com.bodybuilding.hyper.resource.converters;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;

import com.bodybuilding.hyper.resource.HyperResource;
import com.bodybuilding.hyper.resource.annotation.Rel;
import com.bodybuilding.hyper.resource.controls.Link;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;



public class HALJSONMessageConverterTest {

    MediaType mediaType = new MediaType("application", "hal+json");
    HALJSONMessageConverter writer = new HALJSONMessageConverter();

    @Mock
    HttpInputMessage mockInput;

    @Mock
    HttpOutputMessage mockOutput;


    ByteArrayOutputStream outputStream;



    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        outputStream = new ByteArrayOutputStream();

        when(mockOutput.getBody()).thenReturn(outputStream);
    }

    @Test
    public void testCanReadReturnsFalse(){
        assertFalse(writer.canRead(null));
        assertFalse(writer.canRead(mediaType));
    }


    @Test
    public void testSupports(){
        assertFalse(writer.supports(Object.class));
        assertTrue(writer.supports(HyperResource.class));
        assertTrue(writer.supports(new HyperResource() {
        }.getClass()));
    }


    @Test
    public void testReadInternalThrows(){
        try{
            writer.readInternal(HyperResource.class, mockInput);
            fail("expected exception not thrown");
        } catch (Throwable e){
            assertThat(e, instanceOf(HttpMessageNotReadableException.class));
        }
    }


    @Test
    public void testCanWrite(){

        assertFalse(writer.canWrite(HyperResource.class, new MediaType("text", "html")));

        assertTrue(writer.canWrite(HyperResource.class, mediaType));

    }


    @Test
    public void testWriteInternalSimpleResourceNoControls() throws IOException {
        HyperResource resource = new HyperResource(){
            public int getVal() {return 1; };
        };

        writer.writeInternal(resource, mockOutput);

        String expectedString = "{\"val\":1}";
        String actual = outputStream.toString();
        assertEquals(expectedString, actual);
    }
    
    @Test
    public void testWriteInternalSimpleResourceWithOneLinkControl() throws IOException {
    	 HyperResource resource = new HyperResource() {
    		 public Link getImage() {
    			 return new Link("bb:image", "some/url/to/image", "small", "PNG");
    		}
    	 };
    	 writer.writeInternal(resource, mockOutput);
         
         String expectedString  = Resources.toString(
        		 Resources.getResource("hal-serializer-tests/internalSimpleResourceWithOneLinkControl.json")
        		 , Charsets.UTF_8);
         
         String actual = outputStream.toString();
         assertEquals(expectedString, actual);
    }
    
    @Test
    public void testWriteInternalSimpleResourceWithTwoLinkControls() throws IOException, JSONException {
    	 HyperResource resource = new HyperResource() {

    		 public Link getImage() {
    			 return new Link("bb:image", "some/url/to/image", "small", "PNG");
    		 }

             public Link getSelf() {
                 return new Link("self", "some/url/to/resource");
             }
         };
    	 writer.writeInternal(resource, mockOutput);
         
         String expectedString  = Resources.toString(
        		 Resources.getResource("hal-serializer-tests/internalSimpleResourceWithTwoLinkControls.json")
        		 , Charsets.UTF_8);
         
         String actual = outputStream.toString();
        JSONAssert.assertEquals(expectedString, actual, false);
    }
    
    @Test
    public void testWriteInternalSimpleResourceWithNullLinkControl() throws IOException {
    	 HyperResource resource = new HyperResource() {
    		 public Link getLink() {
    			 return null;
    		 }
    	 };
    	 writer.writeInternal(resource, mockOutput);
         
         String expectedString  = "{}";
         
         String actual = outputStream.toString();
         assertEquals(expectedString, actual);
    }
 
    @Test
    public void testWriteInternalSimpleResourceWithLinkArray() throws IOException {
         HyperResource resource = new HyperResource() {
                public Link[] getProfile() { 
                    return new Link[] {
                        new Link("profile", "prof1"), new Link("profile", "prof2")
                    };
                }
         };
         writer.writeInternal(resource, mockOutput);
         
         String expectedString  = Resources.toString(
                 Resources.getResource("hal-serializer-tests/"
                         + "internalSimpleResourceWithLinkArrayControl.json")
                 , Charsets.UTF_8);
         
         String actual = outputStream.toString();
         assertEquals(expectedString, actual);
    }

    @Test
    public void testWriteInternalSimpleResourceWithLinkArrayNSimpleLink() throws IOException {
         HyperResource resource = new HyperResource() {
                public Link[] getProfile() { 
                    return new Link[] {
                        new Link("profile", "prof1"), new Link("profile", "prof2")
                    };
                }               
                public Link getSelf() {
                    return new Link("self", "some/url/to/resource");
               }
         };
         writer.writeInternal(resource, mockOutput);
         
         String expectedString  = Resources.toString(
                 Resources.getResource("hal-serializer-tests/"
                         + "internalSimpleResourceWithLinkArrayNSimpleLinkControl.json")
                 , Charsets.UTF_8);
         
         String actual = outputStream.toString();
         assertEquals(expectedString, actual);
    }
    
    @Test
    public void testWriteInternalSimpleResourceWithLinkArrayNull() throws IOException {
    	 HyperResource resource = new HyperResource() {
    	       	public Link[] getProfile() { 
    	       		return null;
	       		}
    	 };
    	 writer.writeInternal(resource, mockOutput);
         
         String expectedString  = "{}";         
         String actual = outputStream.toString();
         assertEquals(expectedString, actual);
    }
    
    @Test
    public void testWriteInternalSimpleResourceWithProfileLinkIsArray() throws IOException {
    	 HyperResource resource = new HyperResource() {
    	       	public Link getProfile() { 
    	       		return new Link("profile", "prof1");            		
	       		}
    	 };
    	 writer.writeInternal(resource, mockOutput);
         
         String expectedString  = Resources.toString(
        		 Resources.getResource("hal-serializer-tests/internalSimpleResourceWithProfileLinkIsArray.json")
        		 , Charsets.UTF_8);
         
         String actual = outputStream.toString();
         assertEquals(expectedString, actual);
    }
    
    @Test
    public void testWriteInternalSimpleResourceWithListProperty() throws IOException {
         HyperResource resource = new HyperResource() {
                public List<String> getList() { 
                    List<String> list = new ArrayList<String>();
                    list.add("foo1");
                    list.add("foo2");
                    return list;                    
                }
         };
         writer.writeInternal(resource, mockOutput);
         
         String expectedString  = "{\"list\":[\"foo1\",\"foo2\"]}";
         
         String actual = outputStream.toString();
         assertEquals(expectedString, actual);
    }

    @Test
    public void testWriteInternalResourceWithOneSubResource() throws IOException {
         HyperResource resource = new HyperResource() {         
               @Rel("bb:child")
               public HyperResource getResource() { 
                    return new HyperResource() {
                        public String getFoo() { return "foo"; }
                    };                                    
                }              
         };         
         writer.writeInternal(resource, mockOutput);         
         String expectedString  = "{\"_embedded\":{\"bb:child\":{\"foo\":\"foo\"}}}";         
         String actual = outputStream.toString();
         assertEquals(expectedString, actual);
    }
    
    @Test
    public void testWriteInternalResourceWithOneSubResourceNoRelAnnotation() throws IOException {
         HyperResource resource = new HyperResource() {            
               public HyperResource getResource() { 
                    return new HyperResource() {
                        public String getFoo() { return "foo"; }
                    };                                    
                }              
         };         
         writer.writeInternal(resource, mockOutput);         
         String expectedString  = "{\"_embedded\":{\"resource\":{\"foo\":\"foo\"}}}";         
         String actual = outputStream.toString();
         assertEquals(expectedString, actual);
    }
    
    @Test
    public void testWriteInternalResourceWithNullSubresource() throws IOException {
        HyperResource resource = new HyperResource() {            
            public HyperResource getResource() { 
                 return null;                            
             }              
        };
        writer.writeInternal(resource, mockOutput);         
        String expectedString  = "{}";         
        String actual = outputStream.toString();
        assertEquals(expectedString, actual);
    }
    
    @Test
    public void testWriteInternalResourceWithTwoSubResourcesWithSameRel() throws IOException {
         HyperResource resource = new HyperResource() {         
               @Rel("bb:children")
               public HyperResource getResource1() { 
                    return new HyperResource() {
                        public String getFoo() { return "foo"; }
                    };                                    
               }               
               @Rel("bb:children")
               public HyperResource getResource2() { 
                    return new HyperResource() {
                        public String getFoo() { return "foo"; }
                    };                                    
               }
         };         
         writer.writeInternal(resource, mockOutput);         
         String expectedString  = Resources.toString(
                 Resources.getResource("hal-serializer-tests"
                         + "/internalResourceWithTwoSubResourcesWithSameRel.json")
                 , Charsets.UTF_8);         
         String actual = outputStream.toString();
         assertEquals(expectedString, actual);
    }
    
    @Test
    public void testWriteInternalResourceWithSubresouceArray() throws IOException {
        HyperResource resource = new HyperResource() {         
            @Rel("bb:children")
            public HyperResource[] getResource() { 
                 HyperResource child1 = new HyperResource() {
                     public String getFoo() { return "foo"; }
                 };
                 HyperResource child2 = new HyperResource() {
                     public String getFoo() { return "foo"; }
                 };                  
                 return new HyperResource[] {child1, child2};
            }               
        };
        writer.writeInternal(resource, mockOutput);         
        String expectedString  = Resources.toString(
                Resources.getResource("hal-serializer-tests"
                        + "/internalResourceWithTwoSubResourcesWithSameRel.json")
                , Charsets.UTF_8);         
        String actual = outputStream.toString();
        assertEquals(expectedString, actual);
    }
    
    @Test
    public void testWriteInternalResourceWithNullSubresouceArray() throws IOException {
        HyperResource resource = new HyperResource() {
            
            @Rel("bb:children")
            public HyperResource[] getResource() { 
                 return null;                            
             }              
        };
        writer.writeInternal(resource, mockOutput);         
        String expectedString  = "{}";         
        String actual = outputStream.toString();
        assertEquals(expectedString, actual);
    }
    
    @Test
    public void testWriteInternalResourceWithOneSubResourceWithOneLink() throws IOException {
         HyperResource resource = new HyperResource() {         
           @Rel("bb:child")
           public HyperResource getResource() { 
                return new HyperResource() {
                    public Link getLink() { return new Link("rel", "some/url"); }
                };                                    
            }              
         };         
         writer.writeInternal(resource, mockOutput);         
         String expectedString  = Resources.toString(
                 Resources.getResource("hal-serializer-tests"
                         + "/internalResourceWithOneSubResourceWithOneLink.json")
                 , Charsets.UTF_8);              
         String actual = outputStream.toString();
         assertEquals(expectedString, actual);
    }
    
    @Test
    public void testWriteInternalResourceWithTwoDepthSubresources() throws IOException {
         HyperResource resource = new HyperResource() {
           @Rel("bb:child1")
           public HyperResource getResource() {
                return new HyperResource() {
                    public String getFoo() { return "foo"; }
                    
                    @Rel("bb:child2")
                    public HyperResource getResource() {
                        return new HyperResource() {
                            public String getFoo() { return "foo"; }  
                        };
                    }
                };                                    
            }              
         };         
         writer.writeInternal(resource, mockOutput);         
         String expectedString  = Resources.toString(
                 Resources.getResource("hal-serializer-tests"
                         + "/internalResourceWithTwoDepthSubresources.json")
                 , Charsets.UTF_8);                
         String actual = outputStream.toString();
         assertEquals(expectedString, actual);
    }
}
