package accessors;

import annotations.SafetyAccessor;
import annotations.SafetySuit;
import samples.exampleObj.SmallObject;
import samples.exampleObj.SuperGiantObject;

@SafetySuit({
        @SafetyAccessor(
                path = {"getGiantObject", "getNormalObject", "getValue"},
                input = SuperGiantObject.class,
                output = String.class,
                name = "getValue"
        ),
        @SafetyAccessor(
                path = {"getGiantObject", "getNormalObject", "getSmallObject"},
                input = SuperGiantObject.class,
                output = SmallObject.class,
                name = "getSecondValue"
        )
})
public interface SafeAccessor {
}
