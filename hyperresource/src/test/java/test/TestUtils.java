package test;


import java.util.Random;
import java.util.UUID;

//TOOD: ref OSS package once we publish
public final class TestUtils {

    private static final Random r = new Random();

    private TestUtils(){

    }

    public static String uniqueString(){
        return UUID.randomUUID().toString();
    }

    public static int randomInt(){
        return r.nextInt();
    };

    public static int randomInt(int upperBoundExclusive){
        return r.nextInt(upperBoundExclusive);
    };

}
