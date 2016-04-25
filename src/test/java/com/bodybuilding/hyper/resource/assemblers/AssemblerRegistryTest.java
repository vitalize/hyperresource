package com.bodybuilding.hyper.resource.assemblers;


import com.bodybuilding.hyper.resource.HyperResource;
import org.junit.Test;

import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.Assert.*;

public class AssemblerRegistryTest {


    static class Resource1 implements HyperResource {
        final String val;

        public Resource1(String val) {
            this.val = val;
        }
    }

    static class Resource2 implements HyperResource {
        final String val;

        public Resource2(String val) {
            this.val = val;
        }
    }

    static class Resource3 implements HyperResource {
        final String val;

        public Resource3(String val) {
            this.val = val;
        }
    }

    static class Entity1 {
        final String val;

        public Entity1(String val) {
            this.val = val;
        }
    }

    static class Entity2 {
        final String val;

        public Entity2(String val) {
            this.val = val;
        }
    }

    static class Entity3 {
        final String val;

        public Entity3(String val) {
            this.val = val;
        }
    }



    @Test
    public void testFindingExactMatchOneParam(){

        Function<Entity1, Resource1> assembler = (Entity1 e) -> new Resource1("");


        AssemblerRegistry subject = new AssemblerRegistry();

        subject.register(assembler, Resource1.class, Entity1.class);


        Function<Entity1, Resource1> actual = subject.findAssembler(Resource1.class, Entity1.class);

        assertSame(assembler, actual);
    }


    @Test
    public void testNotFindingExactMatchOneParam(){

        AssemblerRegistry subject = new AssemblerRegistry();

        Function<Entity1, Resource1> actual = subject.findAssembler(Resource1.class, Entity1.class);

        assertNull(actual);


        //throw a matching return type in there
        subject.register((Entity2 e) -> new Resource1("r1 from e2: " + e.val), Resource1.class, Entity2.class);

        Function<Entity1, Resource1> actual2 = subject.findAssembler(Resource1.class, Entity1.class);

        assertNull(actual2);

    }



    @Test
    public void testFindingAssemblerWith1SubAssembler(){

        //A simple assembler
        Function<Entity2, Resource2> subAssembler = (Entity2 e) -> new Resource2("r2 from e2: " + e.val);

        //An assembler that needs another assembler (matching teh sig of the one before
        BiFunction<Entity1, Function<Entity2, Resource2>, Resource1> assembler = (Entity1 e, Function<Entity2, Resource2> subassembler) -> new Resource1("r1 from e1: " + e.val + " and " + subassembler.apply(new Entity2("sub entity")).val);



        AssemblerRegistry subject = new AssemblerRegistry();

        subject.register(subAssembler, Resource2.class, Entity2.class);
        subject.register(assembler, Resource1.class, Entity1.class, Resource2.class, Entity2.class);


        Resource1 actual = subject.findAssembler(Resource1.class, Entity1.class).apply(new Entity1("source"));

        assertEquals("r1 from e1: source and r2 from e2: sub entity", actual.val);



    }

    @Test
    public void testFindingAssemblerWith1SubAssemblerButSubNotFound(){

        //An assembler that needs another assembler (that we won't register)
        BiFunction<Entity1, Function<Entity2, Resource2>, Resource1> assembler = (Entity1 e, Function<Entity2, Resource2> subassembler) -> new Resource1("r1 from e1: " + e.val + " and " + subassembler.apply(new Entity2("sub entity")).val);


        AssemblerRegistry subject = new AssemblerRegistry();

        subject.register(assembler, Resource1.class, Entity1.class, Resource2.class, Entity2.class);


        assertNull(subject.findAssembler(Resource1.class, Entity1.class));

    }


    @Test
    public void testFindingAssemblerWith1SubAssemblerWith1SubAssembler(){

        //A simple assembler
        Function<Entity3, Resource3> subAssembler2 = (Entity3 e) -> new Resource3("r3 from e3: " + e.val);

        //An assembler that needs another assembler (matching teh sig of the one before
        BiFunction<Entity2, Function<Entity3, Resource3>, Resource2> subAssembler1 = (Entity2 e, Function<Entity3, Resource3> subassembler) -> new Resource2("r2 from e2: " + e.val + " and " + subassembler.apply(new Entity3("sub entity3")).val);

        //An assembler that needs another assembler (matching teh sig of the one before if it was curried
        BiFunction<Entity1, Function<Entity2, Resource2>, Resource1> assembler = (Entity1 e, Function<Entity2, Resource2> subassembler) -> new Resource1("r1 from e1: " + e.val + " and " + subassembler.apply(new Entity2("sub entity2")).val);



        AssemblerRegistry subject = new AssemblerRegistry();

        subject.register(assembler, Resource1.class, Entity1.class, Resource2.class, Entity2.class);
        subject.register(subAssembler1, Resource2.class, Entity2.class, Resource3.class, Entity3.class);
        subject.register(subAssembler2, Resource3.class, Entity3.class);


        //Test full chain
        Resource1 actual = subject.findAssembler(Resource1.class, Entity1.class).apply(new Entity1("source"));

        assertEquals("r1 from e1: source and r2 from e2: sub entity2 and r3 from e3: sub entity3", actual.val);

        //Test skipping into the middle
        Resource2 actual2 = subject.findAssembler(Resource2.class, Entity2.class).apply(new Entity2("source"));

        assertEquals("r2 from e2: source and r3 from e3: sub entity3", actual2.val);

    }


    @Test
    public void testFindingAssemblerRequiredSubAssemblersThatAreMissing(){

        //A simple assembler
        Function<Entity3, Resource3> subAssembler2 = (Entity3 e) -> new Resource3("r3 from e3: " + e.val);

        //An assembler that needs another assembler (matching teh sig of the one before
        BiFunction<Entity2, Function<Entity3, Resource3>, Resource2> subAssembler1 = (Entity2 e, Function<Entity3, Resource3> subassembler) -> new Resource2("r2 from e2: " + e.val + " and " + subassembler.apply(new Entity3("sub entity3")).val);

        //An assembler that needs another assembler (matching teh sig of the one before if it was curried
        BiFunction<Entity1, Function<Entity2, Resource2>, Resource1> assembler = (Entity1 e, Function<Entity2, Resource2> subassembler) -> new Resource1("r1 from e1: " + e.val + " and " + subassembler.apply(new Entity2("sub entity2")).val);



        AssemblerRegistry subject = new AssemblerRegistry();

        assertNull(subject.findAssembler(Resource1.class, Entity1.class));
        //subject.register(assembler, Resource1.class, Entity1.class, Resource2.class, Entity2.class);
        //subject.register(subAssembler1, Resource2.class, Entity2.class, Resource3.class, Entity3.class);
        //subject.register(subAssembler2, Resource3.class, Entity3.class);





    }




}
