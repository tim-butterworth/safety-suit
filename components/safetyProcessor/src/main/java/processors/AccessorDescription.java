package processors;

public class AccessorDescription {

    private String methodName;
    private String argumentName;
    private String argumentType;
    private String[] path;
    private String returnType;
    private String argumentTypeImport;
    private String returnTypeImport;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getArgumentName() {
        return argumentName;
    }

    public void setArgumentName(String argumentName) {
        this.argumentName = argumentName;
    }

    public String getArgumentType() {
        return argumentType;
    }

    public void setArgumentType(String argumentType) {
        this.argumentType = argumentType;
    }

    public String[] getPath() {
        return path;
    }

    public void setPath(String[] path) {
        this.path = path;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setArgumentTypeImport(String argumentTypeImport) {
        this.argumentTypeImport = argumentTypeImport;
    }

    public String getArgumentTypeImport() {
        return argumentTypeImport;
    }

    public void setReturnTypeImport(String returnTypeImport) {
        this.returnTypeImport = returnTypeImport;
    }

    public String getReturnTypeImport() {
        return returnTypeImport;
    }
}
