import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Inspect {
    public String Key;
    public String Anonymous_Method;
    public String Original = null;
    public String Original_Prediction = null;
    public int UnequalToPrediction = 0;
    public int UnequalToName = 0;
    public int Total = 0;

    public Inspect(String Key_Str, String Method_Str) {
        Key = Key_Str;
        Anonymous_Method = Method_Str;
    }

    public void ASTMutate() throws IOException {
        try {
            CompilationUnit cu = StaticJavaParser.parse("class X {" + Anonymous_Method + "}");

            List<String> methodNames = new ArrayList<>();
            VoidVisitor<List<String>> methodNameCollector = new MethodNameCollector();
            methodNameCollector.visit(cu, methodNames);
            Original = methodNames.isEmpty() ? "" : methodNames.get(0);

            Predict predict = new Predict(cu.toString());
            try {
                predict.pred();
                Original_Prediction = predict.Result;
            } catch (Exception e) {
                e.printStackTrace();
            }

            TreeVisitor visitor = new TreeVisitor() {
                @Override
                public void process(Node node) {
                    String stmt = node.getMetaModel().toString();
                    if (stmt.equals("ForStmt") || stmt.equals("IfStmt")) {
                        node.remove();

                        Predict p = new Predict(cu.toString());
                        try {
                            p.pred();
                            if (!p.Result.equals(Original_Prediction)) {
                                ++UnequalToPrediction;
                            }
                            if (!p.Result.equals(Original)) {
                                ++UnequalToName;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        ++Total;
                    }
                }
            };

            visitor.visitBreadthFirst(cu);
            visitor.visitLeavesFirst(cu);
            visitor.visitPostOrder(cu);
            visitor.visitPreOrder(cu);

        } catch (ParseProblemException e) {
            Files.write(
                    Paths.get("src/data/error_log"),
                    Key.concat("\n").getBytes(),
                    StandardOpenOption.APPEND);
            Files.write(
                    Paths.get("src/data/error_log"),
                    e.toString().getBytes(),
                    StandardOpenOption.APPEND);
        }
    }

    private static class MethodNameCollector extends VoidVisitorAdapter<List<String>> {

        @Override
        public void visit(MethodDeclaration md, List<String> collector) {
            super.visit(md, collector);
            collector.add(md.getNameAsString());
        }
    }
}