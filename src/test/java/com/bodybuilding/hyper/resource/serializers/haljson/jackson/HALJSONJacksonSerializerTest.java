package com.bodybuilding.hyper.resource.serializers.haljson.jackson;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.bodybuilding.commerce.cart.CartApplication;
import com.bodybuilding.hyper.resource.serializer.haljson.jackson.HALJSONJacksonSerializer;
import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.skyscreamer.jsonassert.JSONAssert;

import com.bodybuilding.hyper.resource.HyperResource;
import com.bodybuilding.hyper.resource.annotation.Rel;
import com.bodybuilding.hyper.resource.controls.Link;


public class HALJSONJacksonSerializerTest {

    static String readResourceAsString(String resource){
        return new Scanner(CartApplication.class.getClassLoader().getResourceAsStream(resource), "UTF-8").useDelimiter("\\A").next();

    }

    HALJSONJacksonSerializer writer = new HALJSONJacksonSerializer();


    ByteArrayOutputStream outputStream;


    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        outputStream = new ByteArrayOutputStream();
        
    }

    @Test
    public void testGetContentTypes(){
        assertThat(writer.getContentTypes(), Matchers.contains("application/hal+json"));

    }

    @Test
    public void testCanWrite(){

        assertTrue("any HyperResource can be serialized as hal+json", writer.canWrite(HyperResource.class));
        assertTrue("any HyperResource can be serialized as hal+json", writer.canWrite(new HyperResource(){}.getClass()));
    }
    


    @Test
    public void testWriteSimpleResourceNoControls() throws IOException {
        HyperResource resource = new HyperResource() {
            public int getVal() {
                return 1;
            }

            ;
        };

        writer.write(resource, outputStream);

        String expectedString = "{\"val\":1}";
        String actual = outputStream.toString();
        assertEquals(expectedString, actual);
    }

    @Test
    public void testWriteSimpleResourceWithOneLinkControl() throws IOException {
        HyperResource resource = new HyperResource() {
            public Link getImage() {
                return new Link("bb:image", "some/url/to/image", "small", "PNG");
            }
        };
        writer.write(resource, outputStream);

        String expectedString = readResourceAsString("hal-serializer-tests/internalSimpleResourceWithOneLinkControl.json");

        String actual = outputStream.toString();
        assertEquals(expectedString, actual);
    }

    @Test
    public void testWriteSimpleResourceWithTwoLinkControls() throws IOException, JSONException {
        HyperResource resource = new HyperResource() {

            public Link getImage() {
                return new Link("bb:image", "some/url/to/image", "small", "PNG");
            }

            public Link getSelf() {
                return new Link("self", "some/url/to/resource");
            }
        };
        writer.write(resource, outputStream);

        String expectedString = readResourceAsString("hal-serializer-tests/internalSimpleResourceWithTwoLinkControls.json");

        String actual = outputStream.toString();
        JSONAssert.assertEquals(expectedString, actual, false);
    }

    @Test
    public void testWriteSimpleResourceWithNullLinkControl() throws IOException, JSONException {
        HyperResource resource = new HyperResource() {
            public Link getLink() {
                return null;
            }
        };
        writer.write(resource, outputStream);

        String expectedString = "{}";

        String actual = outputStream.toString();
        JSONAssert.assertEquals(expectedString, actual, false);
    }

    @Test
    public void testWriteSimpleResourceWithLinkArray() throws IOException, JSONException {
        HyperResource resource = new HyperResource() {
            public Link[] getProfile() {
                return new Link[]{
                    new Link("profile", "prof1"), new Link("profile", "prof2")
                };
            }
        };
        writer.write(resource, outputStream);

        String expectedString = readResourceAsString("hal-serializer-tests/internalSimpleResourceWithLinkArrayControl.json");

        String actual = outputStream.toString();
        JSONAssert.assertEquals(expectedString, actual, false);
    }

    @Test
    public void testWriteSimpleResourceWithLinkArrayNSimpleLink() throws IOException, JSONException {
        HyperResource resource = new HyperResource() {
            public Link[] getProfile() {
                return new Link[]{
                    new Link("profile", "prof1"), new Link("profile", "prof2")
                };
            }

            public Link getSelf() {
                return new Link("self", "some/url/to/resource");
            }
        };
        writer.write(resource, outputStream);

        String expectedString = readResourceAsString("hal-serializer-tests/internalSimpleResourceWithLinkArrayNSimpleLinkControl.json");

        String actual = outputStream.toString();
        JSONAssert.assertEquals(expectedString, actual, false);
    }

    @Test
    public void testWriteSimpleResourceWithLinkArrayNull() throws IOException {
        HyperResource resource = new HyperResource() {
            public Link[] getProfile() {
                return null;
            }
        };
        writer.write(resource, outputStream);

        String expectedString = "{}";
        String actual = outputStream.toString();
        assertEquals(expectedString, actual);
    }

    @Test
    public void testWriteSimpleResourceWithProfileLinkIsArray() throws IOException, JSONException {
        HyperResource resource = new HyperResource() {
            public Link getProfile() {
                return new Link("profile", "prof1");
            }
        };
        writer.write(resource, outputStream);

        String expectedString = readResourceAsString("hal-serializer-tests/internalSimpleResourceWithProfileLinkIsArray.json");

        String actual = outputStream.toString();
        JSONAssert.assertEquals(expectedString, actual, false);
    }

    @Test
    public void testWriteSimpleResourceWithListProperty() throws IOException, JSONException {
        HyperResource resource = new HyperResource() {
            public List<String> getList() {
                List<String> list = new ArrayList<String>();
                list.add("foo1");
                list.add("foo2");
                return list;
            }
        };
        writer.write(resource, outputStream);

        String expectedString = "{\"list\":[\"foo1\",\"foo2\"]}";

        String actual = outputStream.toString();
        JSONAssert.assertEquals(expectedString, actual, false);
    }

    @Test
    public void testWriteResourceWithOneSubResource() throws IOException {
        HyperResource resource = new HyperResource() {
            @Rel("bb:child")
            public HyperResource getResource() {
                return new HyperResource() {
                    public String getFoo() {
                        return "foo";
                    }
                };
            }
        };
        writer.write(resource, outputStream);
        String expectedString = "{\"_embedded\":{\"bb:child\":{\"foo\":\"foo\"}}}";
        String actual = outputStream.toString();
        assertEquals(expectedString, actual);
    }

    @Test
    public void testWriteResourceWithOneSubResourceNoRelAnnotation() throws IOException {
        HyperResource resource = new HyperResource() {
            public HyperResource getResource() {
                return new HyperResource() {
                    public String getFoo() {
                        return "foo";
                    }
                };
            }
        };
        writer.write(resource, outputStream);
        String expectedString = "{\"_embedded\":{\"resource\":{\"foo\":\"foo\"}}}";
        String actual = outputStream.toString();
        assertEquals(expectedString, actual);
    }

    @Test
    public void testWriteResourceWithNullSubresource() throws IOException {
        HyperResource resource = new HyperResource() {
            public HyperResource getResource() {
                return null;
            }
        };
        writer.write(resource, outputStream);
        String expectedString = "{}";
        String actual = outputStream.toString();
        assertEquals(expectedString, actual);
    }

    @Test
    public void testWriteResourceWithTwoSubResourcesWithSameRel() throws IOException, JSONException {
        HyperResource resource = new HyperResource() {
            @Rel("bb:children")
            public HyperResource getResource1() {
                return new HyperResource() {
                    public String getFoo() {
                        return "foo";
                    }
                };
            }

            @Rel("bb:children")
            public HyperResource getResource2() {
                return new HyperResource() {
                    public String getFoo() {
                        return "foo";
                    }
                };
            }
        };
        writer.write(resource, outputStream);
        String expectedString = readResourceAsString("hal-serializer-tests/internalResourceWithTwoSubResourcesWithSameRel.json");
        String actual = outputStream.toString();
        JSONAssert.assertEquals(expectedString, actual, false);
    }

    @Test
    public void testWriteResourceWithSubresouceArray() throws IOException, JSONException {
        HyperResource resource = new HyperResource() {
            @Rel("bb:children")
            public HyperResource[] getResource() {
                HyperResource child1 = new HyperResource() {
                    public String getFoo() {
                        return "foo";
                    }
                };
                HyperResource child2 = new HyperResource() {
                    public String getFoo() {
                        return "foo";
                    }
                };
                return new HyperResource[]{child1, child2};
            }
        };
        writer.write(resource, outputStream);
        String expectedString = readResourceAsString("hal-serializer-tests/internalResourceWithTwoSubResourcesWithSameRel.json");
        String actual = outputStream.toString();
        JSONAssert.assertEquals(expectedString, actual, false);
    }

    @Test
    public void testWriteResourceWithNullSubresouceArray() throws IOException, JSONException {
        HyperResource resource = new HyperResource() {

            @Rel("bb:children")
            public HyperResource[] getResource() {
                return null;
            }
        };
        writer.write(resource, outputStream);
        String expectedString = "{}";
        String actual = outputStream.toString();
        JSONAssert.assertEquals(expectedString, actual, false);
    }

    @Test
    public void testWriteResourceWithOneSubResourceWithOneLink() throws IOException {
        HyperResource resource = new HyperResource() {
            @Rel("bb:child")
            public HyperResource getResource() {
                return new HyperResource() {
                    public Link getLink() {
                        return new Link("rel", "some/url");
                    }
                };
            }
        };
        writer.write(resource, outputStream);
        String expectedString = readResourceAsString("hal-serializer-tests/internalResourceWithOneSubResourceWithOneLink.json");
        String actual = outputStream.toString();
        assertEquals(expectedString, actual);
    }

    @Test
    public void testWriteResourceWithTwoDepthSubresources() throws IOException, JSONException {
        HyperResource resource = new HyperResource() {
            @Rel("bb:child1")
            public HyperResource getResource() {
                return new HyperResource() {
                    public String getFoo() {
                        return "foo";
                    }

                    @Rel("bb:child2")
                    public HyperResource getResource() {
                        return new HyperResource() {
                            public String getFoo() {
                                return "foo";
                            }
                        };
                    }
                };
            }
        };
        writer.write(resource, outputStream);
        String expectedString = readResourceAsString("hal-serializer-tests/internalResourceWithTwoDepthSubresources.json");
        String actual = outputStream.toString();
        JSONAssert.assertEquals(expectedString, actual, false);
    }
    
}
