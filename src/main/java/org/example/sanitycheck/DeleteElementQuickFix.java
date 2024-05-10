package org.example.sanitycheck;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

class DeleteElementQuickFix extends BaseIntentionAction {

    private final PsiElement elementToDelete;

    DeleteElementQuickFix(PsiElement element) {
        this.elementToDelete = element;
    }

    @NotNull
    @Override
    public String getText() {
        return "Delete Element '" + elementToDelete.getText() + "'";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Create property";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    /** Deletes the element
     * @param project The project, not used
     * @param editor The editor, not used
     * @param file The file, not used
     * @throws IncorrectOperationException
     */
    @Override
    public void invoke(@NotNull final Project project, final Editor editor, PsiFile file) throws
            IncorrectOperationException {
        ApplicationManager.getApplication().invokeLater(() -> {
            // Delete the element
            if (elementToDelete != null) {
                WriteCommandAction.runWriteCommandAction(project, elementToDelete::delete);
            }
        });
    }
}
