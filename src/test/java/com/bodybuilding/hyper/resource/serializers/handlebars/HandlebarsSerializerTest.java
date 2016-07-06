package com.bodybuilding.hyper.resource.serializers.handlebars;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicBoolean;

import com.bodybuilding.hyper.resource.HyperResource;
import com.bodybuilding.hyper.resource.serializer.handlebars.HandlebarsSerializer;
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
public class HandlebarsSerializerTest {


    @Mock
    Handlebars mockHandlebars;


    @Mock
    HttpInputMessage mockInput;

    @Mock
    OutputStream mockOutputStream;

    @Mock
    Template mockTemplate;

    HandlebarsSerializer subject;


    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        subject = new HandlebarsSerializer(mockHandlebars, "something/nothing");
    }

    @Test
    public void testCanWrite(){

        //assertTrue(, subject.canWrite(HyperResource.class));
        //assertTrue(, subject.canWrite(new HyperResource(){}.getClass()));
    }


    @Test
    public void testRequiredConstructorParams(){
        try{
            new HandlebarsSerializer(null);
            fail("expected exception not thrown");
        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("handlebars can not be null"));
        }


        try{
            new HandlebarsSerializer(mockHandlebars);
            fail("expected exception not thrown");
        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("handledContentTypes can not be null or empty"));
        }

        try{
            new HandlebarsSerializer(mockHandlebars, new String[0]);
            fail("expected exception not thrown");
        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("handledContentTypes can not be null or empty"));
        }
    }

    @Test
    public void testGetContentTypes(){
        assertThat(subject.getContentTypes(), Matchers.contains("something/nothing"));

        String newType = TestUtils.randomString();
        subject = new HandlebarsSerializer(mockHandlebars, newType);

        assertThat(subject.getContentTypes(), Matchers.contains(newType));

        String newType2 = TestUtils.randomString();
        subject = new HandlebarsSerializer(mockHandlebars, newType2, newType);

        assertThat(subject.getContentTypes(), Matchers.contains(newType2, newType));
    }


    class FakeHyperResource implements HyperResource {

    }



    @Test
    public void testEnsureStreamIsNotClosedOnException() throws IOException {
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


        verify(mockOutputStream, never()).close();

    }

    @Test
    public void testWrite() throws IOException {

        AtomicBoolean flushCalled = new AtomicBoolean(false);


        FakeHyperResource fakeResource = new FakeHyperResource();
        ByteArrayOutputStream fakeOutputStream = new ByteArrayOutputStream(){

            @Override
            public void close() throws IOException {
                fail("close should not be called");
            }


            @Override
            public void flush() throws IOException {
                flushCalled.set(true);
            }
        };

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

        assertTrue("flush must be called", flushCalled.get());

    }



}