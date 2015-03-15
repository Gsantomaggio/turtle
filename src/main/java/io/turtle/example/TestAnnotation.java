package io.turtle.example;

/**
 * Created by gabriele on 15/03/15.
 */
public class TestAnnotation {
    public @interface MySampleAnn {

        String name();
        String desc();
    }


    @MySampleAnn(name = "test1", desc = "testing annotations")
    public void myTestMethod() {

    }


    public static void main(String[] args){




    }

}
