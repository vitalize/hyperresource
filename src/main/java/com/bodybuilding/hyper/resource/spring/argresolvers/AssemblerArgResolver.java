package com.bodybuilding.hyper.resource.spring.argresolvers;

import com.bodybuilding.hyper.resource.HyperResource;
import com.bodybuilding.hyper.resource.assemblers.AssemblerRegistry;
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
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        Type type = methodParameter.getGenericParameterType();
        if(type instanceof ParameterizedType){
            Type[] typeArgs = ((ParameterizedType) type).getActualTypeArguments();


            Type inputType = typeArgs[0];
            Type returnType = typeArgs[1];

            //TODO: should i do this check in supports?
            //TODO: uh this class casting is wrong for sure...but what should i do?
            return registry.findAssembler((Class)returnType, (Class)inputType);

        }


        //TODO: or should i throw?
        return null;
    }
}
