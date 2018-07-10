## Safety Suite

# Purpose

Sometimes there is a giant object that might be full of 'toxic' nulls.  A deep dereference can result in a dreaded null pointer exception.  For example:

```Java
class WrappingObject {
  private I_might_be_null i_might_be_null;

  public WrappingObject(I_might_be_null i_might_be_null) {
    this.i_might_be_null = i_might_be_null;
  }

  public I_might_be_null getI_might_be_null() {
    return this.i_might_be_null;
  }
}

class I_might_be_null {
  private I_also_could_be_null i_also_could_be_null;

  public I_might_be_null(I_also_could_be_null i_also_could_be_null) {
    this.i_also_could_be_null = i_also_could_be_null;
  }

  public I_also_could_be_null getI_also_could_be_null() {
    return this.i_also_could_be_null;
  }
}

class I_also_could_be_null {
  private What_about_me what_about_me;

  public I_also_could_be_null(What_about_me what_about_me) {
    this.what_about_me = what_about_me;
  }

  public What_about_me getWhat_about_me() {
    return what_about_me;
  }
}

class What_about_me {
  private String value_you_want;

  public What_about_me(String value_you_want) {
    this.value_you_want = value_you_want;
  }

  public String getValue_you_want() {
    return value_you_want;
  }
}

wrappingObject.getI_might_be_null().getI_also_could_be_null().getWhat_about_me().getValue_you_want();  // there are 3 or 4 null checks that need to be made (depending on whether or not the outermost object could be null)
```

Using Safety Suite one would do something like:

```

@SafetySuite
public interface WrappingObjectAccessor {

      @SafetyAccessor(path = {
              "getI_might_be_null",
              "getI_also_could_be_null",
              "getWhat_about_me",
              "getValue_you_want"
      })
      Optional<String> getValueWeAreAfter(WrappingObject object);
}

```

