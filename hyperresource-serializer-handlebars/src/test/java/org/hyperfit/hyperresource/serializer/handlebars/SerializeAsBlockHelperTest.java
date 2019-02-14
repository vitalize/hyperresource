package org.hyperfit.hyperresource.serializer.handlebars;

import com.github.jknack.handlebars.*;
import org.hyperfit.hyperresource.HyperResource;
import org.hyperfit.hyperresource.serializer.HyperResourceSerializer;
import org.hyperfit.hyperresource.serializer.haljson.jackson2.HALJSONJacksonSerializer;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.util.*;

import static org.hyperfit.hyperresource.serializer.handlebars.SerializeAsBlockHelper.HELPER_MARKUP_TAG_NAME;
import static org.junit.Assert.*;
import static test.TestUtils.randomInt;
import static test.TestUtils.uniqueString;

public class SerializeAsBlockHelperTest {

    private static final String[][] JSON_ESCAPE_MAP = new String[][]{
            {"</", "<\\/" },
            {"<!-", "<\\!-" }
    };

    @Test
    public void testEmptyArray() throws IOException {

        Handlebars handlebars = new Handlebars();

        handlebars.registerHelper(HELPER_MARKUP_TAG_NAME, new SerializeAsBlockHelper(new WrappedSerializer(new HALJSONJacksonSerializer(), null)));

        Template template = handlebars.compileInline("{{serializeAs \"application/hal+json\" arrayOfResources}}");

        CharSequence result = template.apply(new DummyComplexHyperResource().withArrayOfResources(new DummyHyperResource[]{}));

        assertEquals("[]", result);
    }

    @Test
    public void testNoSerializers() throws IOException {

        Handlebars handlebars = new Handlebars();

        handlebars.registerHelper(HELPER_MARKUP_TAG_NAME, new SerializeAsBlockHelper());

        Template template = handlebars.compileInline("{{serializeAs \"application/hal+json\" arrayOfResources}}");

        try{
            template.apply(new DummyComplexHyperResource());
            fail("expected exception not thrown");
        } catch(Exception e){
            assertTrue(e.getMessage().contains("application/hal+json is not supported by SerializeAsBlockHelper"));
        }
    }

    @Test
    public void testNullJSON() throws IOException {

        Handlebars handlebars = new Handlebars();

        handlebars.registerHelper(HELPER_MARKUP_TAG_NAME, new SerializeAsBlockHelper(new WrappedSerializer(new HALJSONJacksonSerializer(), JSON_ESCAPE_MAP)));

        Template template = handlebars.compileInline("{{serializeAs \"application/hal+json\" arrayOfResources}}");

        CharSequence result = template.apply(new DummyComplexHyperResource());

        assertEquals("", result);
    }

    @Test
    public void testNullForHtml() throws IOException {

        Handlebars handlebars = new Handlebars();

        handlebars.registerHelper(HELPER_MARKUP_TAG_NAME, new SerializeAsBlockHelper(new HandlebarsSerializer(handlebars, "text/html")));

        Template template = handlebars.compileInline("{{serializeAs \"text/html\" someField}}");

        CharSequence result = template.apply(new DummyComplexHyperResource());

        assertEquals("", result);
    }

    @Test
    public void testCanWriteHandlebarsSerializer() throws IOException {

        HyperResourceSerializer serializer = new WrappedSerializer(new HandlebarsSerializer(new Handlebars(), "text/html"), null);

        assertFalse(serializer.canWrite(HyperResource.class));

        assertTrue(serializer.canWrite(new DummyHyperResource().getClass()));

    }


    @Test
    public void testHtmlSerializer() throws IOException {

        Handlebars handlebars = new Handlebars();

        SerializeAsBlockHelper helper = new SerializeAsBlockHelper(new HandlebarsSerializer(handlebars, "text/html"));

        String stringValue = uniqueString();

        Object[] params = new Object[]{new DummyHyperResource().withStringField(stringValue  + "<!-- comments -->")};

        Object result = helper.apply("text/html", new Options(handlebars, HELPER_MARKUP_TAG_NAME, null, null, null, null, params, null, Collections.emptyList()));

        assertTrue(result.toString().contains(stringValue));
    }

    @Test
    public void testJsonSerializer() throws IOException {

        Handlebars handlebars = new Handlebars();

        SerializeAsBlockHelper helper = new SerializeAsBlockHelper(new WrappedSerializer(new HALJSONJacksonSerializer(), JSON_ESCAPE_MAP));

        String stringValue = uniqueString();

        Object[] params = new Object[]{new DummyHyperResource().withStringField(stringValue + "</")};

        Object result = helper.apply("application/hal+json", new Options(handlebars, HELPER_MARKUP_TAG_NAME, null, null, null, null, params, null, Collections.emptyList()));

        assertTrue(result.toString().contains(stringValue));

        assertTrue(result.toString().contains("<\\/"));
        assertFalse(result.toString().contains("</"));
    }



    @Test
    public void testUnsupportedResource() throws IOException {

        Handlebars handlebars = new Handlebars();

        handlebars.registerHelper(HELPER_MARKUP_TAG_NAME, new SerializeAsBlockHelper(new WrappedSerializer(new HALJSONJacksonSerializer(), JSON_ESCAPE_MAP)));

        Template template = handlebars.compileInline("{{serializeAs \"application/hal+json\" value}}");

        try{
            CharSequence result = template.apply(new Dummy("testValue"));
            fail("expected exception not thrown");
        } catch(Exception e){
            assertTrue(e.getMessage().contains("SerializeAsBlockHelper does not support class: String"));
        }

    }


    @Test
    public void testUnsupportedContentType() throws IOException {

        Handlebars handlebars = new Handlebars();

        handlebars.registerHelper(HELPER_MARKUP_TAG_NAME, new SerializeAsBlockHelper(new WrappedSerializer(new HALJSONJacksonSerializer(), JSON_ESCAPE_MAP)));

        Template template = handlebars.compileInline("{{serializeAs \"text/html\" value}}");

        try{
            CharSequence result = template.apply(new Dummy("testValue"));
            fail("expected exception not thrown");
        } catch(Exception e){
            assertTrue(e.getMessage().contains("text/html is not supported by SerializeAsBlockHelper"));
        }

    }

    @Test
    public void testArrayWithNotValidSymbols() throws IOException {

        Handlebars handlebars = new Handlebars();

        handlebars.registerHelper(HELPER_MARKUP_TAG_NAME, new SerializeAsBlockHelper(new WrappedSerializer(new HALJSONJacksonSerializer(), JSON_ESCAPE_MAP)));

        Template template = handlebars.compileInline("{{serializeAs \"application/hal+json\" arrayOfResources}}");

        String result = template.apply( new DummyComplexHyperResource().withArrayOfResources(
                new DummyHyperResource().withStringArrayField(uniqueString() + "</", uniqueString() + "</")
                        .withStringField(uniqueString()+ "<!-"),
                new DummyHyperResource().withStringArrayField(uniqueString() + "</", uniqueString() + "</")
                        .withStringField(uniqueString()+ "<!-").withIntegerField(randomInt()))
        );


        assertTrue(result.contains("<\\/"));
        assertFalse(result.contains("</"));
        assertTrue(result.contains("<\\!-"));
        assertFalse(result.contains("<!-"));

        ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
        assertNotNull(result);
        try {
            Object res = engine.eval(result);
            assertNotNull(res);
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testResourceWithNotValidSymbols() throws IOException {

        Handlebars handlebars = new Handlebars();

        handlebars.registerHelper(HELPER_MARKUP_TAG_NAME, new SerializeAsBlockHelper(new WrappedSerializer(new HALJSONJacksonSerializer(), JSON_ESCAPE_MAP)));

        Template template = handlebars.compileInline("{{serializeAs \"application/hal+json\" resource}}");

        String result = template.apply( new DummyComplexHyperResource().withResource(new DummyHyperResource()
                                            .withStringArrayField(uniqueString() + "</", uniqueString() + "</")
                                            .withStringField(uniqueString()+ "<!-")
        ));

        assertTrue(result.contains("<\\/"));
        assertFalse(result.contains("</"));
        assertTrue(result.contains("<\\!-"));
        assertFalse(result.contains("<!-"));

        ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
        assertNotNull(result);

        result = "JSON.parse('" + result + "')";

        try {
            // validate if it's valid js object/expression
            Object res = engine.eval(result);
            assertNotNull(res);
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testHtmlContentType() throws IOException {

        Handlebars handlebars = new Handlebars();

        handlebars.registerHelper(HELPER_MARKUP_TAG_NAME, new SerializeAsBlockHelper(new WrappedSerializer(new HALJSONJacksonSerializer(), JSON_ESCAPE_MAP), new HandlebarsSerializer(handlebars, "text/html")));

        Template template = handlebars.compileInline("{{serializeAs \"text/html\" resource}}");

        String someString = uniqueString();

        String result = template.apply( new DummyComplexHyperResource().withResource(
                new DummyHyperResource().withStringField(someString))
        );

        assertTrue(result.contains("<div>"));
        assertTrue(result.contains(someString));


    }


    private static class DummyHyperResource implements HyperResource{


        private String stringField;

        private Integer integerField;

        private String[] stringArrayField;

        public DummyHyperResource withStringField(String stringField) {
            this.stringField = stringField;
            return this;
        }

        public DummyHyperResource withIntegerField(Integer integerField) {
            this.integerField = integerField;
            return this;
        }

        public DummyHyperResource withStringArrayField(String... stringArrayField) {
            this.stringArrayField = stringArrayField;
            return this;
        }

        public String getStringField() {
            return stringField;
        }

        public Integer getIntegerField() {
            return integerField;
        }

        public String[] getStringArrayField() {
            return stringArrayField;
        }
    }

    private static class DummyComplexHyperResource implements HyperResource{


        private HyperResource resource;

        private HyperResource[] arrayOfResources;

        public DummyComplexHyperResource withResource(HyperResource resource) {
            this.resource = resource;
            return this;
        }

        public DummyComplexHyperResource withArrayOfResources(HyperResource... arrayOfResources) {
            this.arrayOfResources = arrayOfResources;
            return this;
        }

        public HyperResource getResource() {
            return resource;
        }

        public HyperResource[] getArrayOfResources() {
            return arrayOfResources;
        }
    }

    private static class Dummy {

        private final String value;

        public Dummy(String value){
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }

}
