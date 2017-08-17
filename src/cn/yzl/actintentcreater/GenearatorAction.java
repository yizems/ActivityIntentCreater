package cn.yzl.actintentcreater;

import cn.yzl.actintentcreater.modle.TypeBean;
import cn.yzl.actintentcreater.utils.Utils;
import cn.yzl.actintentcreater.view.MyDialog;
import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YZL on 2017/8/13.
 */
public class GenearatorAction extends BaseGenerateAction {
    @SuppressWarnings("unused")
    public GenearatorAction() {
        super(null);
    }

    public GenearatorAction(CodeInsightActionHandler handler) {
        super(handler);
    }

    @Override
    protected boolean isValidForClass(PsiClass targetClass) {
//        PsiClass[] supers = targetClass.getSupers();
//        for (int i = 0; i < supers.length; i++) {
//            if (supers[i].getQualifiedName().equals("android.app.Activity")) {
//                return true;
//            }
//        }
        return true;
    }

    @Override
    protected boolean isValidForFile(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        return file.getName().endsWith(".java");
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        actionPerformedImpl(project, editor);
    }

    @Override
    public void actionPerformedImpl(@NotNull Project project, Editor editor) {
        PsiJavaFile psiFile = (PsiJavaFile) PsiUtilBase.getPsiFileInEditor(editor, project);
        PsiClass psiClass = getPsiClassFromContext(psiFile, editor);

        if (!(isValidForClass(psiClass) && isValidForFile(project, editor, psiFile))) {
            return;
        }

        PsiField[] fields = psiClass.getFields();

        List<TypeBean> types = new ArrayList<>();

        for (PsiField field : fields) {
            TypeBean typeBean = new TypeBean();
            typeBean.fieldName = field.getName();
            typeBean.typeName = field.getType().toString().replace("PsiType:","");
            typeBean.psiType = field.getType();
            if (Utils.prepareType(typeBean)) {
                types.add(typeBean);
            }
        }

        MyDialog dialog = new MyDialog(types, new MyDialog.CallBack() {
            @Override
            public void pick(List<TypeBean> result) {
                createCode(psiFile, psiClass, result);
            }
        });
        dialog.pack();
        dialog.setSize(500, 500);
        Toolkit kit = Toolkit.getDefaultToolkit();    // 定义工具包

        Dimension screenSize = kit.getScreenSize();   // 获取屏幕的尺寸

        int screenWidth = screenSize.width / 2;         // 获取屏幕的宽

        int screenHeight = screenSize.height / 2;       // 获取屏幕的高

        int height = 500;

        int width = 500;
        dialog.setLocation(screenWidth - width / 2, screenHeight - height / 2);

        dialog.setVisible(true);
    }

    public void createCode(PsiJavaFile psiFile, PsiClass psiClass, List<TypeBean> types) {

        new IWriter(psiFile.getProject(), psiFile, psiClass, types, JavaPsiFacade.getElementFactory(psiClass.getProject())).execute();
    }

    /**
     * @param psiFile
     * @param editor
     * @return
     */
    private PsiClass getPsiClassFromContext(PsiFile psiFile, Editor editor) {
        if (psiFile == null || editor == null) {
            return null;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);
        return PsiTreeUtil.getParentOfType(element, PsiClass.class);
    }
}
