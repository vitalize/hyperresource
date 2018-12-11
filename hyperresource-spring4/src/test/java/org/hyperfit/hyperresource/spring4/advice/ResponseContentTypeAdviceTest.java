package org.hyperfit.hyperresource.spring4.advice;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Locale;

import static org.mockito.Mockito.*;

public class ResponseContentTypeAdviceTest {

    @Mock
    HttpServletResponse mockResponse;

    @Mock
    HttpServletRequest mockRequest;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSetContentLanguage(){
        //TODO: randomize this
        Locale fakeLocale = new Locale("pt", "BR");

        ResponseContentTypeAdvice subject = new ResponseContentTypeAdvice();

        when(mockRequest.getLocale())
            .thenReturn(fakeLocale);


        subject.setContentLanguageOnResponse(
            mockRequest,
            mockResponse
        );

        verify(mockResponse).setHeader(
            HttpHeaders.CONTENT_LANGUAGE,
            fakeLocale.toLanguageTag()
        );

        verifyNoMoreInteractions(mockResponse);


    }
}
