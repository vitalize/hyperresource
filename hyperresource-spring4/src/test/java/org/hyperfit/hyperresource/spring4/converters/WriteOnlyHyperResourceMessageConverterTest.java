package org.hyperfit.hyperresource.spring4.converters;

import org.hyperfit.hyperresource.HyperResource;
import org.hyperfit.hyperresource.serializer.HyperResourceSerializer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServletServerHttpResponse;
import test.TestUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class WriteOnlyHyperResourceMessageConverterTest {

    @Mock
    ServletServerHttpResponse mockServletServerHttpResponse;

    @Mock
    HttpOutputMessage mockHttpOutputMessage;

    @Mock
    HttpInputMessage mockInput;

    @Mock
    OutputStream mockOutputStream;

    @Mock
    HyperResourceSerializer mockHyperResourceSerializer;

    @Mock
    HttpServletResponse mockHttpServletResponse;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void testBadConstructorParamsThrow(){
        try{
            new WriteOnlyHyperResourceMessageConverter(null);
            fail("expected exception not thrown");
        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("serializer can not be null"));
        }
    }

    @Test
    public void testGetSupportedMediaTypes(){
        String[] supportedTypes = new String[]{
            "application/" + TestUtils.uniqueString(),
            "application/" + TestUtils.uniqueString(),
        };


        when(mockHyperResourceSerializer.getContentTypes())
            .thenReturn(Arrays.asList(supportedTypes));

        WriteOnlyHyperResourceMessageConverter subject = new WriteOnlyHyperResourceMessageConverter(mockHyperResourceSerializer);

        assertThat(subject.getSupportedMediaTypes(), contains(
            MediaType.valueOf(supportedTypes[0]),
            MediaType.valueOf(supportedTypes[1])
        ));

    }

    @Test
    public void testCanReadReturnsFalse(){
        String contentType = "application/" + TestUtils.uniqueString();

        when(mockHyperResourceSerializer.getContentTypes())
            .thenReturn(Collections.singletonList(contentType));

        WriteOnlyHyperResourceMessageConverter subject = new WriteOnlyHyperResourceMessageConverter(mockHyperResourceSerializer);

        assertFalse(subject.canRead(null));
        assertFalse(subject.canRead(MediaType.valueOf(contentType)));

        assertFalse(subject.canRead(HyperResource.class, null));
        assertFalse(subject.canRead(HyperResource.class, MediaType.valueOf(contentType)));
    }


    @Test
    public void testCanWriteBasedOnContentType(){
        String contentType = "application/" + TestUtils.uniqueString();

        when(mockHyperResourceSerializer.getContentTypes())
            .thenReturn(Collections.singletonList(contentType));

        //This makes it so the type doesn't matter as long as it's a HyperResource
        when(mockHyperResourceSerializer.canWrite(any()))
            .thenReturn(true);

        WriteOnlyHyperResourceMessageConverter subject = new WriteOnlyHyperResourceMessageConverter(mockHyperResourceSerializer);


        assertTrue(subject.canWrite(HyperResource.class, null));
        assertTrue(subject.canWrite(HyperResource.class, MediaType.ALL));
        assertTrue(subject.canWrite(HyperResource.class, MediaType.valueOf(contentType)));
        assertFalse(subject.canWrite(HyperResource.class, new MediaType("text", "plain")));


        assertTrue(subject.canWrite(new HyperResource(){}.getClass(), null));
        assertTrue(subject.canWrite(new HyperResource(){}.getClass(), MediaType.ALL));
        assertTrue(subject.canWrite(new HyperResource(){}.getClass(), MediaType.valueOf(contentType)));
        assertFalse(subject.canWrite(new HyperResource(){}.getClass(), new MediaType("text", "plain")));

    }


    @Test
    public void testCanWriteBasedOnResourceClass(){
        String contentType = "application/" + TestUtils.uniqueString();

        Class<? extends HyperResource> someUniqueHyperResourceType = new HyperResource(){}.getClass();

        when(mockHyperResourceSerializer.getContentTypes())
            .thenReturn(Collections.singletonList(contentType));

        when(mockHyperResourceSerializer.canWrite(someUniqueHyperResourceType))
            .thenReturn(true);

        when(mockHyperResourceSerializer.canWrite(HyperResource.class))
            .thenReturn(false);

        WriteOnlyHyperResourceMessageConverter subject = new WriteOnlyHyperResourceMessageConverter(mockHyperResourceSerializer);


        assertFalse(subject.canWrite(HyperResource.class, null));
        assertFalse(subject.canWrite(HyperResource.class, MediaType.ALL));
        assertFalse(subject.canWrite(HyperResource.class, MediaType.valueOf(contentType)));


        assertFalse(subject.canWrite(Object.class, null));
        assertFalse(subject.canWrite(Object.class, MediaType.ALL));
        assertFalse(subject.canWrite(Object.class, MediaType.valueOf(contentType)));

        assertTrue(subject.canWrite(someUniqueHyperResourceType, null));
        assertTrue(subject.canWrite(someUniqueHyperResourceType, MediaType.ALL));
        assertTrue(subject.canWrite(someUniqueHyperResourceType, MediaType.valueOf(contentType)));


    }


    @Test
    public void testSupports(){
        String contentType = "application/" + TestUtils.uniqueString();

        when(mockHyperResourceSerializer.getContentTypes())
            .thenReturn(Collections.singletonList(contentType));

        when(mockHyperResourceSerializer.canWrite(any()))
            .thenReturn(true);

        WriteOnlyHyperResourceMessageConverter subject = new WriteOnlyHyperResourceMessageConverter(mockHyperResourceSerializer);

        assertFalse(subject.supports(Object.class));
        assertTrue(subject.supports(HyperResource.class));
        assertTrue(subject.supports(new HyperResource(){}.getClass()));
    }


    @Test
    public void testReadInternalThrows(){
        String contentType = "application/" + TestUtils.uniqueString();

        when(mockHyperResourceSerializer.getContentTypes())
            .thenReturn(Collections.singletonList(contentType));

        WriteOnlyHyperResourceMessageConverter subject = new WriteOnlyHyperResourceMessageConverter(mockHyperResourceSerializer);


        try{
            subject.readInternal(HyperResource.class, mockInput);
            fail("expected exception not thrown");
        } catch (Throwable e){
            assertThat(e, instanceOf(HttpMessageNotReadableException.class));
        }
    }

    @Test
    public void testWriteInternalNoLocale() throws IOException {
        String contentType = "application/" + TestUtils.uniqueString();

        when(mockHyperResourceSerializer.getContentTypes())
            .thenReturn(Collections.singletonList(contentType));

        WriteOnlyHyperResourceMessageConverter subject = new WriteOnlyHyperResourceMessageConverter(mockHyperResourceSerializer);

        HyperResource fakeHyperResource = new HyperResource() {};


        when(mockServletServerHttpResponse.getBody())
            .thenReturn(mockOutputStream);

        subject.writeInternal(fakeHyperResource, mockServletServerHttpResponse);

        verify(mockHyperResourceSerializer)
            .write(fakeHyperResource, null, mockOutputStream);

    }

    @Test
    public void testWriteInternalContentTypeHeaderPresent() throws IOException {
        String contentType = "application/" + TestUtils.uniqueString();

        when(mockHyperResourceSerializer.getContentTypes())
            .thenReturn(Collections.singletonList(contentType));

        WriteOnlyHyperResourceMessageConverter subject = new WriteOnlyHyperResourceMessageConverter(mockHyperResourceSerializer);

        HyperResource fakeHyperResource = new HyperResource() {};


        when(mockServletServerHttpResponse.getBody())
            .thenReturn(mockOutputStream);


        Locale ptBR = new Locale("pt", "BR");

        HttpHeaders fakeHeaders = new HttpHeaders();
        fakeHeaders.add(
            HttpHeaders.CONTENT_LANGUAGE,
            ptBR.toLanguageTag()
        );

        when(mockServletServerHttpResponse.getHeaders())
            .thenReturn(fakeHeaders);

        subject.writeInternal(fakeHyperResource, mockServletServerHttpResponse);

        verify(mockHyperResourceSerializer)
            .write(fakeHyperResource, ptBR, mockOutputStream);

    }

    @Test
    public void testWriteInternalContentTypeHeaderNotPresentOutputNotHttpServletResponse() throws IOException {
        String contentType = "application/" + TestUtils.uniqueString();

        when(mockHyperResourceSerializer.getContentTypes())
            .thenReturn(Collections.singletonList(contentType));

        WriteOnlyHyperResourceMessageConverter subject = new WriteOnlyHyperResourceMessageConverter(mockHyperResourceSerializer);

        HyperResource fakeHyperResource = new HyperResource() {};


        when(mockHttpOutputMessage.getBody())
            .thenReturn(mockOutputStream);


        subject.writeInternal(fakeHyperResource, mockHttpOutputMessage);

        verify(mockHyperResourceSerializer)
            .write(fakeHyperResource, null, mockOutputStream);

    }

    @Test
    public void testWriteInternalContentTypeHeaderNotPresentLocaleOnServletResponse() throws IOException {
        String contentType = "application/" + TestUtils.uniqueString();

        when(mockHyperResourceSerializer.getContentTypes())
            .thenReturn(Collections.singletonList(contentType));

        WriteOnlyHyperResourceMessageConverter subject = new WriteOnlyHyperResourceMessageConverter(mockHyperResourceSerializer);

        HyperResource fakeHyperResource = new HyperResource() {};


        when(mockServletServerHttpResponse.getBody())
            .thenReturn(mockOutputStream);


        Locale ptBR = new Locale("pt", "BR");

        when(mockServletServerHttpResponse.getServletResponse())
            .thenReturn(mockHttpServletResponse);

        when(mockHttpServletResponse.getLocale())
            .thenReturn(ptBR);

        subject.writeInternal(fakeHyperResource, mockServletServerHttpResponse);

        verify(mockHyperResourceSerializer)
            .write(fakeHyperResource, ptBR, mockOutputStream);

    }


    @Test
    public void testGetSerializer() {
        String contentType = "application/" + TestUtils.uniqueString();

        when(mockHyperResourceSerializer.getContentTypes())
            .thenReturn(Collections.singletonList(contentType));

        WriteOnlyHyperResourceMessageConverter subject = new WriteOnlyHyperResourceMessageConverter(
            mockHyperResourceSerializer
        );

        assertSame(
            mockHyperResourceSerializer,
            subject.getSerializer()
        );
    }
}
