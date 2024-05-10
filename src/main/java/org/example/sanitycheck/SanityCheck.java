package org.example.sanitycheck;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SanityCheck implements Annotator {


    /**
     * @param element The element to annotate, if it is not a comment it is ignored. If it is a comment we gather the
     *                whole comment block and the code after it
     * @param holder The annotation holder
     */
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull final AnnotationHolder holder) {
        // Check if the element is a comment
        if (!(element instanceof PsiComment)) {
            return;
        }

        // Extract the comment content and code lines
        StringBuilder commentContent = new StringBuilder();
        List<String> codeLines = new ArrayList<>();
        PsiElement currentElement = element;

        // Find the start of the comment block
        while (currentElement.getPrevSibling() instanceof PsiComment) {
            currentElement = currentElement.getPrevSibling();
        }

        // Extract the comment content
        while (currentElement instanceof PsiComment || currentElement instanceof PsiWhiteSpace) {
            commentContent.append(currentElement.getText()).append("\n");
            currentElement = currentElement.getNextSibling();
        }

        // Extract the code lines
        while (currentElement != null && !(currentElement instanceof PsiComment)) {
            if (!(currentElement instanceof PsiWhiteSpace)) {
                codeLines.add(currentElement.getText().trim());
            }
            currentElement = currentElement.getNextSibling();
        }

        // Trim the comment and code strings
        String commentString = commentContent.toString().trim();
        String codeString = String.join("\n", codeLines).trim();

        // Check if the comment is not empty
        if (!commentString.isEmpty()) {
            CommentCodeSender sender = new CommentCodeSender();
            CompletableFuture<String> responseFuture = sender.sendCommentAndCode(commentString, codeString);

            // Use CompletableFuture asynchronously
            responseFuture.thenAccept(response -> {
                if (!"NOTHING".equals(response)) {
                    // You must update the UI or Annotations on the UI thread
                    ApplicationManager.getApplication().invokeLater(() -> {
                        // Verify if the element is still valid
                        if (!element.isValid()) {
                            return;
                        }
                        // Add the annotation
                        //Note: We use depreciated options because they work
                        Annotation annotation = holder.createAnnotation(HighlightSeverity.WARNING, element.getTextRange(), response);
                        annotation.registerFix(new DeleteElementQuickFix(element));


                        //TODO: replace depreciated options

                        /* IDK why this doesn't work
                        holder.newAnnotation(HighlightSeverity.WARNING, response)
                         .range(textRange)
                         .withFix(new DeleteElementQuickFix(element))
                         .create();
                        */
                    });
                }
            });
        }
    }
}

/* // Wait for the response synchronously
            String response = responseFuture.join();

            if (!"NOTHING".equals(response)) {
                //Add the annotation
                TextRange textRange = element.getTextRange();
                holder.newAnnotation(HighlightSeverity.WARNING, response)
                        .range(textRange)
                        .withFix(new DeleteElementQuickFix(element))
                        .create();
            }
 */