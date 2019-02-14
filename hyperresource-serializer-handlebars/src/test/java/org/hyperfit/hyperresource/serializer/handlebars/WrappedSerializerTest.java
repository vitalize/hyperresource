package org.hyperfit.hyperresource.serializer.handlebars;

import org.hamcrest.Matchers;
import org.hyperfit.hyperresource.HyperResource;
import org.hyperfit.hyperresource.serializer.HyperResourceSerializer;
import org.hyperfit.hyperresource.serializer.haljson.jackson2.HALJSONJacksonSerializer;
import org.junit.Test;
import test.TestUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;


public class WrappedSerializerTest {


    private static final String[][] JSON_ESCAPE_MAP = new String[][]{
            {"</", "<\\/" },
            {"<!-", "<\\!-" }
    };

    @Test
    public void testCanWrite() throws IOException {

        HyperResourceSerializer serializer = new WrappedSerializer(new HALJSONJacksonSerializer(), null);

        assertTrue(serializer.canWrite(HyperResource.class));

        assertTrue(serializer.canWrite(new HyperResource(){}.getClass()));

    }

    @Test
    public void testWrite() throws IOException {


        HyperResourceSerializer serializer = new WrappedSerializer(new HALJSONJacksonSerializer(), null);


        ByteArrayOutputStream fakeOutputStream = new ByteArrayOutputStream();


        final String fakeString = TestUtils.uniqueString();


        serializer.write(new DummyHyperResource(fakeString), null, fakeOutputStream);

        assertTrue(
                "The result written to writer must make it to the passed in output stream",
                fakeOutputStream.toString().contains(fakeString)
        );

    }


    @Test
    public void testRequiredConstructorParams(){
        try{
            new WrappedSerializer(null, JSON_ESCAPE_MAP);
            fail("expected exception not thrown");
        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("A serializer is required."));
        }
    }

    @Test
    public void testGetContentTypes(){

        HyperResourceSerializer serializer = new WrappedSerializer(new HALJSONJacksonSerializer(), null);
        assertThat(serializer.getContentTypes(), Matchers.contains(HALJSONJacksonSerializer.CONTENT_TYPE_HAL_JSON));

    }


    private class DummyHyperResource implements HyperResource {

        private final String stringField;

        public DummyHyperResource(String value){
            this.stringField = value;
        }
        public String getStringField() {
            return stringField;
        }

    }

}