package org.hyperfit.hyperresource.spring4.controllers;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @ResponseBody
    @RequestMapping("/testHandlebarsTemplateWorksWithController/{one}/{two}")
    public TwoVariableHyperResource testHandlebarsTemplateWorksWithController(
        @PathVariable String one,
        @PathVariable String two
    ) {
        return new TwoVariableHyperResource(one, two);
    }

    @ResponseBody
    @RequestMapping("/testHandlebarsTemplateDoesNotExist")
    public NoTemplateHyperResource testHandlebarsTemplateDoesNotExist() {
        return new NoTemplateHyperResource();
    }

    @RequestMapping("/testRuntimeExceptionInController")
    public HttpEntity<NoTemplateHyperResource> testRuntimeExceptionInController() {
        throw new RuntimeException("testRuntimeExceptionInController");
    }

    @RequestMapping("/testExceptionInControllerWrappedByDefaultHandlerExceptionResolver")
    public HttpEntity<NoTemplateHyperResource> testExceptionInControllerWrappedByDefaultHandlerExceptionResolver()
        throws HttpMediaTypeNotSupportedException {
        throw new HttpMediaTypeNotSupportedException("testExceptionInControllerWrappedByDefaultHandlerExceptionResolver");
    }

}