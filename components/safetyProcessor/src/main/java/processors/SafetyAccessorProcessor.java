package processors;

import annotations.SafetyAccessor;
import annotations.SafetySuit;
import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AutoService(Processor.class)
public class SafetyAccessorProcessor extends AbstractProcessor {

    private final SourceFile sourceFile = new SourceFile();

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
        List<AccessorDescription> accessorDescriptionList;

        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(SafetySuit.class)) {
            accessorDescriptionList = getMethodData(annotatedElement);

            String fullyQualified = annotatedElement.asType().toString() + "SafetySuit";
            int lastIndexOf = fullyQualified.lastIndexOf(".");
            String className = fullyQualified.substring(lastIndexOf + 1);
            String packageName = annotatedElement.getEnclosingElement().asType().toString();

            sourceFile.writeSourceFile(accessorDescriptionList, fullyQualified, className, packageName, processingEnv);
        }

        return true;
    }

    private List<AccessorDescription> getMethodData(Element annotatedElement) {
        SafetySuit annotation = annotatedElement.getAnnotation(SafetySuit.class);

        return Stream.of(annotation.value()).map(safetyAccessor -> {
            AccessorDescription accessorDescription = new AccessorDescription();

            TypeMirror argumentType = safelyGetTypeNameWithAccessor(safetyAccessor::input);
            String argumentTypeName = getTypeName(argumentType);

            TypeMirror outputType = safelyGetTypeNameWithAccessor(safetyAccessor::output);
            String outputTypeName = getTypeName(outputType);

            accessorDescription.setArgumentType(argumentTypeName);
            accessorDescription.setArgumentName(argumentTypeName.toLowerCase());
            accessorDescription.setArgumentTypeImport(getTypeImport(argumentType));

            accessorDescription.setReturnType(String.format("Optional<%s>", outputTypeName));
            accessorDescription.setReturnTypeImport(getTypeImport(outputType));

            accessorDescription.setMethodName(safetyAccessor.name());
            accessorDescription.setPath(safetyAccessor.path());

            return accessorDescription;
        }).collect(Collectors.toList());
    }

    private String getTypeName(TypeMirror typeMirror) {
        String[] splitClassAndPath = typeMirror.toString().split("\\.");
        return splitClassAndPath[splitClassAndPath.length - 1];
    }

    private TypeMirror safelyGetTypeNameWithAccessor(Supplier<Class<?>> safetyAccessor) {
        try {
            safetyAccessor.get();

            throw new RuntimeException("MirroredTypeException is expected");
        } catch (MirroredTypeException e) {
            return e.getTypeMirror();
        }
    }

    private String getTypeImport(TypeMirror typeMirror) {
        return typeMirror.toString();
    }
}
