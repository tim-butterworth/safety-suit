package accessors;

import annotations.SafetyAccessor;
import annotations.SafetySuit;
import samples.exampleObj.SuperGiantObject;

import java.util.Optional;

@SafetySuit
public interface SafeAccessor {

    @SafetyAccessor(path = {
            "getGiantObject",
            "getNormalObject",
            "getValue"
    })
    Optional<String> getValue(SuperGiantObject giantObject);
}
