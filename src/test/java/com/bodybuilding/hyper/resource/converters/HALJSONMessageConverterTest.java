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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;

import com.bodybuilding.hyper.resource.HyperResource;
import com.bodybuilding.hyper.resource.controls.Link;


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

         String expectedString = "{\"_links\":{\"bb:image\":{\"href\":\"some/url/to/image\","
         		+ "\"name\":\"small\",\"type\":\"PNG\"}}}";
         String actual = outputStream.toString();
         assertEquals(expectedString, actual);
    }
    
    //@Test
    public void testWriteInternalSimpleResourceWithLinkControls() throws IOException {
        HyperResource resource = new HyperResource(){            	
            	public int getVal() { return 1; }
            	public String getName() { return "Gabo"; }            	
            	public Link getSelf() { return new Link("self", "www"); }
            	public Link getSomeLink() { return new Link("some", "www"); }
            	public String getLastName() { return "Solano"; }
            	public Link getNullLink() {return null;}
            	public Link getSomeLink2() { return new Link("some", "www2"); }
            	
            	public Link[] getProfile() { return new Link[] {
            			new Link("profile", "prof1"), new Link("profile", "prof2")
            			}; }
            	
        };

        writer.writeInternal(resource, mockOutput);

        String expectedString = "{\"val\":1}";
        String actual = outputStream.toString();
        assertEquals(expectedString, actual);
    }
}
