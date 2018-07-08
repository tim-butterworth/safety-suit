package verifyAnnotations;

import annotations.SafetyAccessor;
import annotations.SafetySuite;
import samples.exampleObj.SuperGiantObject;

import java.util.Optional;

@SafetySuite
public interface SafeAccessor {

    @SafetyAccessor(path = {
            "getGiantObject",
            "getNormalObject",
            "getValue"
    })
    Optional<String> getValue(SuperGiantObject giantObject);
}
