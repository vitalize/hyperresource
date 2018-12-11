package org.hyperfit.hyperresource.spring4.advice;


import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Set Content-Language header for response. This header will be used in MessageConverter 
 *  to get information about request's locale.
 * Responses from view resolvers have the locale auto set, but MessageConverters do not see
 * https://jira.spring.io/browse/SPR-14802
 *
 * Note: a simple advice was picked over ResponseBodyAdvice and Spring Interceptors to keep the dependencies limits to spring-web module and not require web-mvc
 * Servlet Filter was not used as it felt heavy handed and not spring specific
 */
@ControllerAdvice
public class ResponseContentTypeAdvice {

    //HACK: using ModelAttribute is a hacky but ensures this gets called for every request...not sure how else to do this
    @ModelAttribute
    public void setContentLanguageOnResponse(
        HttpServletRequest request,
        HttpServletResponse response
    ){
        response.setHeader(
            HttpHeaders.CONTENT_LANGUAGE,
            request.getLocale().toLanguageTag()
        );
    }
}
