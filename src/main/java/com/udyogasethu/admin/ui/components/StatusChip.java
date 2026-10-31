package com.udyogasethu.admin.ui.components;

import com.vaadin.flow.component.html.Span;

public class StatusChip extends Span {

    public StatusChip(String text, String type) {
        super(text);
        addClassName("status-chip");
        if (type != null) {
            addClassName("status-chip-" + type.toLowerCase().trim());
        }
    }

    public static StatusChip create(String status) {
        if (status == null) {
            return new StatusChip("UNKNOWN", "neutral");
        }
        String s = status.toLowerCase();
        String styleType;
        if (s.contains("open") || s.contains("active") || s.contains("verified") || s.contains("delivered") || s.contains("captured") || s.contains("completed")) {
            styleType = "active";
        } else if (s.contains("pending") || s.contains("review") || s.contains("under_review") || s.contains("draft") || s.contains("applied")) {
            styleType = "pending";
        } else if (s.contains("suspend") || s.contains("reject") || s.contains("cancel") || s.contains("failed") || s.contains("no_show")) {
            styleType = "suspended";
        } else {
            styleType = "active";
        }
        return new StatusChip(status.replace("_", " ").toUpperCase(), styleType);
    }
}
