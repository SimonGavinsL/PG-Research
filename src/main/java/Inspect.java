import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.TreeVisitor;

public class Inspect {
    public String Anonymous_Method;
    public String original;
    public int valid = 0;
    public int valid_total = 0;

    public Inspect(String s) {
        Anonymous_Method = s;
    }

    public void cutAST() {
        CompilationUnit cu = StaticJavaParser.parse("class X {" + Anonymous_Method + "}");

//        System.out.println(cu);
        Predict pre = new Predict(cu.toString());
        try {
            pre.pred();
            original = pre.result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        TreeVisitor visitor = new TreeVisitor() {
            @Override
            public void process(Node node) {
                String stmt = node.getMetaModel().toString();
                if(stmt.equals("ForStmt") || stmt.equals("IfStmt")) {
                    node.remove();
                    System.out.println(cu);
                    Predict p = new Predict(cu.toString());
                    try {
                        p.pred();
                        if(!p.result.equals(original)) {
                            ++valid;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

//                    System.out.println(p.result.csv);
                    ++valid_total;
                }
            }
        };
        visitor.visitPostOrder(cu);
    }
}