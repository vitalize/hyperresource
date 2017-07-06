package org.hyperfit.hyperresource.spring4.argresolvers;

import org.hyperfit.hyperresource.HyperResource;
import org.hyperfit.hyperresource.assemblers.AssemblerRegistry;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Function;

public class AssemblerArgResolver implements HandlerMethodArgumentResolver {

    private final AssemblerRegistry registry;

    public AssemblerArgResolver(AssemblerRegistry registry){
        this.registry = registry;
    }


    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        if( methodParameter.getParameterType().equals(Function.class) ){
            Type type = methodParameter.getGenericParameterType();
            if(type instanceof ParameterizedType){
                Type returnType = ((ParameterizedType) type).getActualTypeArguments()[1];
                if(returnType instanceof Class) {
                    return HyperResource.class.isAssignableFrom((Class)returnType);
                }
            }
        }

        return false;

    }

    @Override
    public Object resolveArgument(
        MethodParameter methodParameter,
        ModelAndViewContainer modelAndViewContainer,
        NativeWebRequest nativeWebRequest,
        WebDataBinderFactory webDataBinderFactory
    ) throws Exception {
        Type type = methodParameter.getGenericParameterType();
        if(type instanceof ParameterizedType){
            Type[] typeArgs = ((ParameterizedType) type).getActualTypeArguments();


            Type inputType = typeArgs[0];
            Type returnType = typeArgs[1];

            //TODO: should i do this check in supports?
            //TODO: uh this class casting is wrong for sure...but what should i do?
            //TODO: pass in a filtering strategy that returns only assemblers that satisfy some filtering condition
            return registry.findAssembler((Class)returnType, (Class)inputType);

        }


        //TODO: or should i throw..we get null pointer exceptions in controllers if don't throw..but maybe it'd be better to do the work up in supports and it would seem more natural?
        //I added a Func<Object, Object> and got a weird error org.springframework.beans.BeanInstantiationException: Failed to instantiate [java.util.function.Function]: Specified class is an interface
        //So maybe a custom error here ain't such a bad idea
        return null;
    }
}
