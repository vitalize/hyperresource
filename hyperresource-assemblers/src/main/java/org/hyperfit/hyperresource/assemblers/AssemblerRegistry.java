package org.hyperfit.hyperresource.assemblers;

import org.hyperfit.hyperresource.HyperResource;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;


//TODO: what do we do when an assembler needs 3 params or more.  IE (entity, csrf, & sub assembler) or (entity, subAss1, subAss2)?

public class AssemblerRegistry {

    class BiReg<R extends HyperResource, E, E2, R2 extends HyperResource>{
        final BiFunction<E, Function<E2, R2>, R> func;
        final Class<R2> subReturnType;
        final Class<E2> subParamType;

        public BiReg(BiFunction<E, Function<E2, R2>, R> func, Class<R2> subReturnType, Class<E2> subParamType) {
            this.func = func;
            this.subReturnType = subReturnType;
            this.subParamType = subParamType;
        }
    }

    Map<Type, Map<Type, Function>> funcsByReturnTypeByParamType = new HashMap<>();
    Map<Type, Map<Type, BiReg>> bifuncsByReturnTypeByParamType = new HashMap<>();

    //TODO: is there a way to use reflection to get returnType and paramType?
    //Seems like lambda's lose all typing info :) proposal http://mail.openjdk.java.net/pipermail/compiler-dev/2015-January/009220.html
    //hack at http://stackoverflow.com/a/25613179/442773 , but...man that ones even to scary for me
    public <R extends HyperResource,E> AssemblerRegistry register(Function<E, R> assembler, Class<R> returnType, Class<E> paramType){
        funcsByReturnTypeByParamType
            .computeIfAbsent(returnType, (t) -> new HashMap<>())
            .put(paramType, assembler);

        return this;
    }

    //TODO: is there a way to use reflection to get returnType and paramType?
    public <R extends HyperResource, E, E2, R2 extends HyperResource> AssemblerRegistry register(BiFunction<E, Function<E2, R2>, R> assembler, Class<R> returnType, Class<E> paramType, Class<R2> subReturnType, Class<E2> subParamType){
        bifuncsByReturnTypeByParamType
            .computeIfAbsent(returnType, (t) -> new HashMap<>())
            .put(paramType, new BiReg<>(assembler, subReturnType, subParamType));

        return this;
    }


    public <R extends HyperResource, E, E2, R2 extends HyperResource> Function<E,R> findAssembler(Class<R> returnType, Class<E> paramType){
        //Go looking for the simple answer paramType -> returnType
        Function simpleAnswer = funcsByReturnTypeByParamType
            .computeIfAbsent(returnType, (t) -> new HashMap<>())
            .get(paramType);

        if(simpleAnswer != null){
            return simpleAnswer;
        }


        //dang, ok go look for a more complicated answer that requires a sub assembler
        BiReg<R,E,E2,R2> biReg = findBiAssembler(returnType, paramType);

        if(biReg == null){
            return null;
        }

        //BiFUnction needs a sub assembler...here we go recursive trying to find it (which may find only a BiFunction which will recurse)
        Function<E2,R2> subAssembler = this.findAssembler(biReg.subReturnType, biReg.subParamType);

        if(subAssembler == null){
            //if we can't find a matching sub assembler, we can't curry the requested assembler
            return null;
        }

        return (E entity) ->  biReg.func.apply(entity, subAssembler);

    }




    protected <R extends HyperResource, E, E2, R2 extends HyperResource> BiReg<R, E, E2, R2> findBiAssembler(Class<R> returnType, Class<E> paramType) {
        //Go looking for the simple answer paramType -> returnType
        return bifuncsByReturnTypeByParamType
            .computeIfAbsent(returnType, (t) -> new HashMap<>())
            .get(paramType);

    }
}
