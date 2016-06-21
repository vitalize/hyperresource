package com.bodybuilding.hyper.resource.serializers.html.handlebars;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import com.bodybuilding.hyper.resource.HyperResource;
import com.bodybuilding.hyper.resource.serializer.html.handlebars.HTMLHandlebarsSerializer;
import com.github.jknack.handlebars.Template;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpInputMessage;

import com.github.jknack.handlebars.Handlebars;
import test.TestUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

/**
 * Tests the HTMLHandlebarsSerializer
 * This is a lot easier than the tests for HALJSONJacksonSerializer since
 * almost all the logic is passed over to handlebars
 *
 * If we start developing handlebars extensions this class will start to grow
 */
public class HTMLHandlebarsSerializerTest {


    @Mock
    Handlebars mockHandlebars;


    @Mock
    HttpInputMessage mockInput;

    @Mock
    OutputStream mockOutputStream;

    @Mock
    Template mockTemplate;

    HTMLHandlebarsSerializer subject;


    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        subject = new HTMLHandlebarsSerializer(mockHandlebars);
    }


    @Test
    public void testNullConstructor(){
        try{
            new HTMLHandlebarsSerializer(null);
            fail("expected exception not thrown");
        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("handlebars can not be null"));
        }
    }

    @Test
    public void testGetContentTypes(){
        assertThat(subject.getContentTypes(), Matchers.contains("text/html"));

    }


    class FakeHyperResource implements HyperResource {

    }

    @Test
    public void testWriteNullOutput() throws IOException {
        //This is really here to test writer before it gets sets and get through that finally block for coverage
        //mostly just test coverage vanity for now

        FakeHyperResource fakeResource = new FakeHyperResource();

        when(mockHandlebars.compile("FakeHyperResource"))
            .thenReturn(mockTemplate);

        try{
            subject.write(fakeResource, null);
            fail("expected exception not thrown");
        }catch(NullPointerException e){
            //TODO: not really sure what to test...i could test the stack trace contains Java.io.Writer
        }

    }


    @Test
    public void testEnsureStreamIsClosedOnException() throws IOException {
        //This is really here to test writer before it gets sets and get through that finally block for coverage
        //mostly just test coverage vanity for now

        FakeHyperResource fakeResource = new FakeHyperResource();

        when(mockHandlebars.compile("FakeHyperResource"))
            .thenReturn(mockTemplate);

        RuntimeException fakeException = new RuntimeException();

        doThrow(fakeException)
            .when(mockTemplate).apply(same(fakeResource), Mockito.any(Writer.class));

        try{
            subject.write(fakeResource, mockOutputStream);
            fail("expected exception not thrown");
        }catch(Exception e){
            assertSame(fakeException, e);
        }

        verify(mockOutputStream).close();

    }

    @Test
    public void testWrite() throws IOException {

        FakeHyperResource fakeResource = new FakeHyperResource();
        ByteArrayOutputStream fakeOutputStream = new ByteArrayOutputStream();

        when(mockHandlebars.compile("FakeHyperResource"))
            .thenReturn(mockTemplate);

        final String fakeResult = TestUtils.randomString();

        doAnswer(
            i -> {
                ((Writer) i.getArguments()[1]).write(fakeResult);
                return null;
            }
        )
            .when(mockTemplate).apply(same(fakeResource), Mockito.any(Writer.class));

        subject.write(fakeResource, fakeOutputStream);

        assertEquals("The result written to writer must make it to the passed in output stream", fakeResult, fakeOutputStream.toString());


    }



}