package org.example.sanitycheck;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class HelloWorldQuickFix implements LocalQuickFix {
    @NotNull
    @Override
    public String getName() {
        return "Change to 'Salutations, Humans!'";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Greeting";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiElement element = descriptor.getPsiElement();

        // Check if the action is being run in the context of a preview
        if (!ApplicationManager.getApplication().isUnitTestMode() && ApplicationManager.getApplication().isDispatchThread()) {
            // To ensure thread safety, execute the write action only when not in preview
            WriteCommandAction.runWriteCommandAction(project, () -> {
                String newText = "\"Salutations, Humans!\"";
                PsiElement newElement = element.getParent().addAfter(element, element.getPrevSibling());
                newElement.getNode().replaceAllChildrenToChildrenOf(element.getNode());
                newElement.getNode().addLeaf(element.getNode().getElementType(), newText, null);
                newElement.getNode().getFirstChildNode().getPsi().delete(); //todo: make this more robust
                element.delete();
            });
        }
    }
}