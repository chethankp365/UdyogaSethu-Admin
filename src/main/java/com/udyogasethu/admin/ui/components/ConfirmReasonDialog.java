package com.udyogasethu.admin.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import java.util.function.Consumer;

public class ConfirmReasonDialog extends Dialog {

    private final TextArea reasonField = new TextArea("Audit Reason (Required)");

    public ConfirmReasonDialog(String title, String description, String actionButtonText, boolean isDestructive, Consumer<String> onConfirm) {
        setHeaderTitle(title);
        setWidth("460px");

        Paragraph desc = new Paragraph(description);
        desc.getStyle().set("color", "var(--us-text-muted)").set("margin-top", "0");

        reasonField.setWidthFull();
        reasonField.setMinHeight("100px");
        reasonField.setPlaceholder("State explicit operational reason for compliance and security audit logs...");
        reasonField.setRequired(true);

        VerticalLayout layout = new VerticalLayout(desc, reasonField);
        layout.setPadding(false);
        layout.setSpacing(true);
        add(layout);

        Button cancelBtn = new Button("Cancel", e -> close());
        Button confirmBtn = new Button(actionButtonText, e -> {
            String reason = reasonField.getValue();
            if (reason == null || reason.trim().length() < 5) {
                reasonField.setErrorMessage("A detailed audit reason (at least 5 characters) is mandatory.");
                reasonField.setInvalid(true);
                return;
            }
            close();
            onConfirm.accept(reason.trim());
        });

        if (isDestructive) {
            confirmBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        } else {
            confirmBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        }

        getFooter().add(cancelBtn, confirmBtn);
    }
}
