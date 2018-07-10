package processors;

import annotations.SafetyAccessor;
import annotations.SafetySuit;
import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@AutoService(Processor.class)
public class SafetyAccessorProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(SafetySuit.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        List<AccessorDescription> accessorDescriptionList = new LinkedList<>();
        List<String> imports = new LinkedList<>();

        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(SafetySuit.class)) {
            String fullyQualified = annotatedElement.asType().toString() + "SafetySuit";
            int lastIndexOf = fullyQualified.lastIndexOf(".");
            String className = fullyQualified.substring(lastIndexOf + 1);
            String packageName = annotatedElement.getEnclosingElement().asType().toString();
            List<? extends Element> enclosedElements = annotatedElement.getEnclosedElements();

            for (Element enclosedElement : enclosedElements) {
                System.out.println(enclosedElement.getSimpleName());

                Optional<SafetyAccessor> maybeSafetyAccessor = Optional.ofNullable(enclosedElement.getAnnotation(SafetyAccessor.class));
                maybeSafetyAccessor.ifPresent(safetyAccessor -> {
                    if (enclosedElement.getKind() == ElementKind.METHOD) {
                        String methodName = enclosedElement.getSimpleName().toString();
                        ExecutableElement executableElement = (ExecutableElement) enclosedElement;
                        TypeMirror returnType = executableElement.getReturnType();
                        String returnTypeString = returnType.toString();

                        String argumentName = executableElement.getParameters().get(0).getSimpleName().toString();
                        String fullyQualifiedArgument = executableElement.getParameters().get(0).asType().toString();

                        String[] path = safetyAccessor.path();

                        String[] split = fullyQualifiedArgument.split("\\.");
                        int length = split.length;
                        String argumentType = split[length - 1];

                        AccessorDescription accessorDescription = new AccessorDescription();
                        accessorDescription.setArgumentName(argumentName);
                        accessorDescription.setArgumentType(argumentType);
                        accessorDescription.setMethodName(methodName);
                        accessorDescription.setReturnType(returnTypeString);
                        accessorDescription.setPath(path);

                        imports.add(String.format("import %s;", fullyQualifiedArgument));
                        accessorDescriptionList.add(accessorDescription);
                    }
                });
                if (enclosedElement.getKind() == ElementKind.METHOD) {
                    ExecutableElement executableElement = (ExecutableElement) enclosedElement;
                    List<? extends VariableElement> parameters = executableElement.getParameters();

                    for (VariableElement parameter : parameters) {
                        System.out.println(parameter.getSimpleName());
                        System.out.println(parameter);
                    }
                }
            }

            enclosedElements.get(0).getAnnotation(SafetyAccessor.class).path();

            JavaFileObject sourceFile = null;
            try {
                sourceFile = processingEnv.getFiler().createSourceFile(fullyQualified);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (BufferedWriter br = new BufferedWriter(sourceFile.openWriter())) {

                imports.add("import java.util.Optional;\n");
                String methods = accessorDescriptionList.stream()
                        .map(
                                accessorDescription -> {
                                    String[] path = accessorDescription.getPath();
                                    String optionalChain = IntStream.range(0, path.length).mapToObj(
                                            i -> {
                                                String method = path[i];
                                                return String.format(
                                                        "    .map(%s -> %s.%s())",
                                                        "a" + i,
                                                        "a" + i,
                                                        method
                                                );
                                            }
                                    ).collect(Collectors.joining("\n"));

                                    return String.format(
                                            "\n    public %s %s(%s %s) { \nreturn Optional.ofNullable(%s)%s; \n    }\n",
                                            accessorDescription.getReturnType(),
                                            accessorDescription.getMethodName(),
                                            accessorDescription.getArgumentType(),
                                            accessorDescription.getArgumentName(),
                                            accessorDescription.getArgumentName(),
                                            optionalChain
                                    );
                                }
                        ).collect(Collectors.joining("\n"));

                br
                        .append(String.format("package %s;\n", packageName))
                        .append(imports.stream().collect(Collectors.joining("\n")))
                        .append(String.format("\npublic class %s {", className))
                        .append("")
                        .append(methods)
                        .append("}");

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        return true;
    }
}
