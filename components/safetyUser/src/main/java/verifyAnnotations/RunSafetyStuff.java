package verifyAnnotations;

import samples.exampleObj.SuperGiantObject;

import java.util.Optional;

public class RunSafetyStuff {
    public static void main(String[] args) {
        FancyClass fancyClass = new FancyClass();
        Optional<String> value = fancyClass.getValue(new SuperGiantObject());

        System.out.println(value);
    }
}
