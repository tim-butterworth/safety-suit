package processors;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SourceFile {

    private final Configuration cfg;

    public SourceFile() {
        cfg = new Configuration(Configuration.VERSION_2_3_27);
        cfg.setWhitespaceStripping(false);
        cfg.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), "templates");

        try {
            Template template = cfg.getTemplate("generatedClass.txt");
            HashMap dataModel = new HashMap();
            dataModel.put("package", "com.some.package");
            dataModel.put("imports", Arrays.asList(
                    "com.something.something.*",
                    "com.gov.org.dep.*",
                    "java.util.Optional"
            ));
            dataModel.put("className", "GreatGreatClass");

            HashMap methodHashmap = new HashMap();
            methodHashmap.put("returnType", "String");
            methodHashmap.put("name", "methodName");
            methodHashmap.put("argumentType", "GiantObject");
            methodHashmap.put("argumentName", "argumentName");

            methodHashmap.put("dereferences", Arrays.asList(
                    getDereferenceMap("1"),
                    getDereferenceMap("2"),
                    getDereferenceMap("3")
            ));

            dataModel.put("methods", Collections.singletonList(
                    methodHashmap
            ));

            template.process(dataModel, new OutputStreamWriter(System.out));
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    private HashMap getDereferenceMap(String diff) {
        HashMap dereference = new HashMap();
        dereference.put("parameterName", "a" + diff);
        dereference.put("methodName", "getValue" + diff);
        return dereference;
    }

    void writeSourceFile(List<AccessorDescription> accessorDescriptionList, String fullyQualified, String className, String packageName, ProcessingEnvironment processingEnv) {
        JavaFileObject sourceFile = null;
        try {
            sourceFile = processingEnv.getFiler().createSourceFile(fullyQualified);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> imports = gatherImports(accessorDescriptionList);

        try (BufferedWriter br = new BufferedWriter(sourceFile.openWriter())) {
            String methods = accessorDescriptionList.stream()
                    .map(
                            accessorDescription -> {
                                String[] path = accessorDescription.getPath();
                                String optionalChain = IntStream.range(0, path.length).mapToObj(
                                        i -> {
                                            String method = path[i];
                                            return String.format(
                                                    "    .map(%s -> %s.%s())",
                                                    "v" + i,
                                                    "v" + i,
                                                    method
                                            );
                                        }
                                ).collect(Collectors.joining("\n"));

                                return String.format(
                                        "\n    public %s %s(%s %s) { \n        return Optional.ofNullable(%s)\n%s; \n    }\n",
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
                    .append(String.format("package %s;\n\n", packageName))
                    .append(String.join("\n", imports))
                    .append(String.format("\n\npublic class %s {", className))
                    .append("")
                    .append(methods)
                    .append("}");

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private List<String> gatherImports(List<AccessorDescription> accessorDescriptionList) {
        List<String> imports = new LinkedList<>();

        imports.add("import java.util.Optional;");

        accessorDescriptionList.forEach(accessorDescription -> {
            imports.add(String.format("import %s;", accessorDescription.getArgumentTypeImport()));
            imports.add(String.format("import %s;", accessorDescription.getReturnTypeImport()));
        });

        return imports;
    }
}