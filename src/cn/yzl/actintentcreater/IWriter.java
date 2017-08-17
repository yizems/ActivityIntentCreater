package cn.yzl.actintentcreater;

import cn.yzl.actintentcreater.modle.TypeBean;
import cn.yzl.actintentcreater.utils.Utils;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.impl.source.codeStyle.ImportHelper;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;

import java.util.List;

/**
 * Created by YZL on 2017/8/14.
 */
public class IWriter extends WriteCommandAction.Simple {

    private List<TypeBean> types;
    protected PsiFile mFile;
    protected Project mProject;
    protected PsiClass mClass;
    protected PsiElementFactory mFactory;

    public IWriter(Project project, PsiFile mFile, PsiClass mClass, List<TypeBean> types, PsiElementFactory mFactory, PsiFile... files) {
        super(project, files);
        this.mFile = mFile;
        this.mProject = project;
        this.mClass = mClass;
        this.mFactory = mFactory;
        this.types = types;
    }

    @Override
    protected void run() throws Throwable {
        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mProject);
        addCreateIntentMethod();
        addInitIntentMethod();

        addImport("android.content.Context", "Context");
        addImport("android.content.Intent", "Intent");

        //重新格式化代码
        styleManager.optimizeImports(mFile);
        styleManager.shortenClassReferences(mClass);
        new ReformatCodeProcessor(mProject, mClass.getContainingFile(), null, false).runWithoutProgress();
    }

    private void addInitIntentMethod() {
        StringBuffer creatIntentSB = new StringBuffer();
        creatIntentSB.append("public void initIntentParams(){\n");
        creatIntentSB.append("Intent intent = getIntent();\n");
        for (TypeBean bean : types) {
            creatIntentSB.append(Utils.generateGetMethod(bean));
            creatIntentSB.append("\n");
        }
        creatIntentSB.append("}\n");
        PsiMethod methodFromText = mFactory.createMethodFromText(creatIntentSB.toString(), mClass);
        mClass.addBefore(methodFromText, mClass.getLastChild());
    }

    /**
     * 添加 创建Intent 方法
     */
    private void addCreateIntentMethod() {
        StringBuffer creatIntentSB = new StringBuffer();
        creatIntentSB.append("public static Intent createIntent(");
        if (types.size() > 0) {
            creatIntentSB.append("Context context,");
        } else {
            creatIntentSB.append("Context context)");
        }

        int i = 0;
        for (TypeBean bean : types) {
            if (i == types.size() - 1) {
                creatIntentSB.append(bean.typeName + " " + bean.fieldName + ")");
            } else {
                creatIntentSB.append(bean.typeName + " " + bean.fieldName + ",");
            }
            i += 1;
        }
        creatIntentSB.append("{\n");
        creatIntentSB.append("Intent intent = new Intent(context," + mClass.getName() + ".class);\n");
        for (TypeBean bean : types) {
            if (bean.typeName.startsWith("Array")) {
                creatIntentSB.append(getArrayPutMethod(bean.typeName, bean.fieldName));
                continue;
            }
            creatIntentSB.append("intent.putExtra(\"" + bean.fieldName + "\"" + "," + bean.fieldName + ");");
        }
        creatIntentSB.append("\nreturn intent;\n}");
        PsiMethod methodFromText = mFactory.createMethodFromText(creatIntentSB.toString(), mClass);
        mClass.addBefore(methodFromText, mClass.getLastChild());
    }


    /**
     * 添加 import
     *
     * @param fullyQualifiedName
     */
    private void addImport(String fullyQualifiedName, String simpleName) {
        if (!(mFile instanceof PsiJavaFile)) {
            return;
        }
        final PsiJavaFile javaFile = (PsiJavaFile) mFile;

        PsiImportList importList = javaFile.getImportList();
        if (importList == null) {
            return;
        }

        // Check if already imported
//        for (PsiImportStatementBase is : importList.getAllImportStatements()) {
//            String impQualifiedName = is.getImportReference().getQualifiedName();
//            if (fullyQualifiedName.equals(impQualifiedName)) {
//                return; // Already imported so nothing neede
//            }
//
//        }
        if (ImportHelper.isAlreadyImported(javaFile, fullyQualifiedName)) {
            return;
        }
        GlobalSearchScope searchScope = GlobalSearchScope.allScope(mClass.getProject());
        PsiClass[] psiClasses = PsiShortNamesCache.getInstance(mClass.getProject()).getClassesByName(simpleName, searchScope);
        for (int i = 0; i < psiClasses.length; i++) {
            if (psiClasses[i].getQualifiedName().equals(fullyQualifiedName)) {
                importList.add(mFactory.createImportStatement(psiClasses[i]));
                return;
            }
        }
    }

    private String getArrayPutMethod(String type, String key) {
        if (type.equals("ArrayList<String>")) {
            return "intent.putStringArrayListExtra(\"" + key + "\"" + "," + key + ");";
        }else if (type.equals("ArrayList<Integer>")) {
            return "intent.putIntegerArrayListExtra(\"" + key + "\"" + "," + key + ");";
        }else if (type.equals("ArrayList<CharSequence>")) {
            return "intent.putCharSequenceArrayListExtra(\"" + key + "\"" + "," + key + ");";
        }else{
            return "intent.putParcelableArrayListExtra(\"" + key + "\"" + "," + key + ");";
        }
    }

}
