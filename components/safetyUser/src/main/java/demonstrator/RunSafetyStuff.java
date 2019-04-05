package demonstrator;

import accessors.SafeAccessorSafetySuit;
import samples.exampleObj.GiantObject;
import samples.exampleObj.NormalObject;
import samples.exampleObj.SuperGiantObject;

import java.util.Optional;

public class RunSafetyStuff {
    public static void main(String[] args) {
        System.out.println(getString(new SuperGiantObject()));

        SuperGiantObject superGiantObject = new SuperGiantObject();
        GiantObject giantObject = new GiantObject();
        NormalObject normalObject = new NormalObject();

        superGiantObject.setGiantObject(giantObject);
        giantObject.setNormalObject(normalObject);
        normalObject.setValue("Some sort of value everyone!");

        System.out.println(getString(superGiantObject));
        System.out.println(getString(null));
    }

    private static Optional<String> getString(SuperGiantObject giantObject) {
        SafeAccessorSafetySuit fancyClass = new SafeAccessorSafetySuit();
        return fancyClass.getValue(giantObject);
    }
}
