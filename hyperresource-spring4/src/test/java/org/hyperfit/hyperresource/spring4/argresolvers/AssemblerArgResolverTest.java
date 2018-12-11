package org.hyperfit.hyperresource.spring4.argresolvers;

import org.hyperfit.hyperresource.HyperResource;
import org.hyperfit.hyperresource.assemblers.AssemblerRegistry;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;
import java.util.function.BiPredicate;
import java.util.function.Function;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class AssemblerArgResolverTest {


    class Resource1 implements HyperResource {

    }


    class Entity1 {

    }


    interface ClassWithTestMethods {
        void methodWithoutFunc(String stuff);
        void methodWithFuncNotParameterized(Function assembler);
        <T> void methodWithFuncReturningType(Function<Entity1, T> assembler);
        void methodWithFuncReturningResource(Function<Entity1, Resource1> assembler);
        void methodWithFuncReturningNonResource(Function<Resource1, Entity1> assembler);
        void methodWithBiPredicate(BiPredicate<Entity1, Resource1> assembler);
    }

    @Mock
    AssemblerRegistry mockRegistry;


    AssemblerArgResolver subject;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        this.subject = new AssemblerArgResolver(mockRegistry);
    }


    @Test
    public void testAssemblerDoesNotSupportsParamNotFunction() throws NoSuchMethodException {
        Method methodToBeCalled = ClassWithTestMethods.class.getMethod("methodWithoutFunc", String.class);
        MethodParameter methodParameter = new MethodParameter(methodToBeCalled, 0);

        assertFalse(
            this.subject.supportsParameter(methodParameter)
        );

    }


    @Test
    public void testAssemblerDoesNotSupportsFuncWithoutParams() throws NoSuchMethodException {
        Method methodToBeCalled = ClassWithTestMethods.class.getMethod("methodWithFuncReturningType", Function.class);
        MethodParameter methodParameter = new MethodParameter(methodToBeCalled, 0);

        assertFalse(
            this.subject.supportsParameter(methodParameter)
        );

    }


    @Test
    public void testAssemblerDoesNotSupportsFuncWithReturnTypeArray() throws NoSuchMethodException {
        Method methodToBeCalled = ClassWithTestMethods.class.getMethod("methodWithFuncNotParameterized", Function.class);
        MethodParameter methodParameter = new MethodParameter(methodToBeCalled, 0);

        assertFalse(
            this.subject.supportsParameter(methodParameter)
        );

    }

    @Test
    public void testAssemblerSupportsFuncReturningResource() throws NoSuchMethodException {
        Method methodToBeCalled = ClassWithTestMethods.class.getMethod("methodWithFuncReturningResource", Function.class);
        MethodParameter methodParameter = new MethodParameter(methodToBeCalled, 0);

        assertTrue(this.subject.supportsParameter(methodParameter));

    }


    @Test
    public void testAssemblerDoesNotSupportFuncNotReturningResource() throws NoSuchMethodException {
        Method methodToBeCalled = ClassWithTestMethods.class.getMethod("methodWithFuncReturningNonResource", Function.class);
        MethodParameter methodParameter = new MethodParameter(methodToBeCalled, 0);

        assertFalse(this.subject.supportsParameter(methodParameter));

    }

    @Test
    public void testAssemblerDoesNotSupportMethodBiPredicate() throws NoSuchMethodException {
        Method methodToBeCalled = ClassWithTestMethods.class.getMethod("methodWithBiPredicate", BiPredicate.class);
        MethodParameter methodParameter = new MethodParameter(methodToBeCalled, 0);

        assertFalse(this.subject.supportsParameter(methodParameter));

    }


    @Test
    public void testResolveFuncReturningResource() throws Exception {
        Method methodToBeCalled = ClassWithTestMethods.class.getMethod("methodWithFuncReturningResource", Function.class);
        MethodParameter methodParameter = new MethodParameter(methodToBeCalled, 0);

        Function<Entity1, Resource1> fakeAssembler = (e) -> new Resource1();

        when(mockRegistry.findAssembler(Resource1.class, Entity1.class))
            .thenReturn(fakeAssembler);

        assertSame(fakeAssembler, this.subject.resolveArgument(methodParameter, null, null, null));

    }


    @Test
    public void testResolveFuncNotReturningResource() throws Exception {
        //Is this even a valid test...it'd never pass supports..so why test it?
        Method methodToBeCalled = ClassWithTestMethods.class.getMethod("methodWithFuncReturningNonResource", Function.class);
        MethodParameter methodParameter = new MethodParameter(methodToBeCalled, 0);

        assertNull(this.subject.resolveArgument(methodParameter, null, null, null));

    }


    @Test
    public void testResolveFuncNotParameterized() throws Exception {
        //Is this even a valid test...it'd never pass supports..so why test it?
        Method methodToBeCalled = ClassWithTestMethods.class.getMethod("methodWithFuncNotParameterized", Function.class);
        MethodParameter methodParameter = new MethodParameter(methodToBeCalled, 0);

        assertNull(this.subject.resolveArgument(methodParameter, null, null, null));

    }



}
