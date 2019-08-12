package cn.yzl.actintentcreater.kotlin;


import cn.yzl.actintentcreater.kotlin.util.KtClassHelper;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor;
import org.jetbrains.kotlin.idea.KotlinLanguage;
import org.jetbrains.kotlin.idea.internal.Location;
import org.jetbrains.kotlin.psi.KtClass;

import java.util.List;

public class GenearatorKotlinAction extends AnAction {

    private KtClass ktClass;

    @Override
    public void update(AnActionEvent e) {
        ktClass = getPsiClassFromEvent(e);

        e.getPresentation().setEnabled(
                ktClass != null &&
                        !ktClass.isEnum() &&
                        !ktClass.isInterface()
        );
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        generateParcelable(ktClass, KtClassHelper.findParams(ktClass));
    }

    private void generateParcelable(final KtClass ktClass, final List<ValueParameterDescriptor> fields) {

        for (ValueParameterDescriptor field : fields) {
            System.out.println(field.toString());
        }

//        new WriteCommandAction.Simple(ktClass.getProject(), ktClass.getContainingFile()) {
//            @Override
//            protected void run() throws Throwable {
//                new CodeGenerator(ktClass, fields).generate();
//            }
//        }.execute();
    }

    private KtClass getPsiClassFromEvent(AnActionEvent e) {
        final Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (editor == null) return null;

        final Project project = editor.getProject();
        if (project == null) return null;

        final PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        if (psiFile == null || psiFile.getLanguage() != KotlinLanguage.INSTANCE) return null;

        final Location location = Location.fromEditor(editor, project);
        final PsiElement psiElement = psiFile.findElementAt(location.getStartOffset());
        if (psiElement == null) return null;

        return KtClassHelper.getKtClassForElement(psiElement);
    }
}
