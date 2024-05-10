package org.example.sanitycheck;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiLiteralValue;
import org.jetbrains.annotations.NotNull;

public class HelloWorldInspection extends LocalInspectionTool {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                super.visitElement(element);

                if (element instanceof PsiLiteralValue literal) {
                    String text = literal.getValue() != null ? literal.getValue().toString() : "";
                    if ("Hello World".equals(text)) {
                        holder.registerProblem(element, "Are you really still printing 'Hello World'?",
                                new HelloWorldQuickFix());
                    }
                }
            }
        };
    }
}
