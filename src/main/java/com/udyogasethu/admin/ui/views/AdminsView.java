package com.udyogasethu.admin.ui.views;

import com.udyogasethu.admin.domain.AdminUser;
import com.udyogasethu.admin.service.AdminService;
import com.udyogasethu.admin.ui.components.ConfirmReasonDialog;
import com.udyogasethu.admin.ui.components.StatusChip;
import com.udyogasethu.admin.ui.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;

@Route(value = "admins", layout = MainLayout.class)
@PageTitle("Admin Management | UdyogaSethu Admin")
@RolesAllowed("SUPER_ADMIN")
public class AdminsView extends VerticalLayout {

    private final AdminService adminService;
    private final Grid<AdminUser> grid = new Grid<>(AdminUser.class, false);

    public AdminsView(AdminService adminService) {
        this.adminService = adminService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background", "var(--us-bg-root)");

        buildHeader();
        buildGrid();
        refreshGrid();
    }

    private void buildHeader() {
        HorizontalLayout bar = new HorizontalLayout();
        bar.setWidthFull();
        bar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        bar.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout titleBox = new VerticalLayout();
        titleBox.setPadding(false);
        titleBox.setSpacing(false);

        H2 title = new H2("Administrative Access & Role Governance");
        title.getStyle().set("margin", "0").set("color", "var(--us-text-main)");

        Paragraph sub = new Paragraph("Strict RBAC enforcement across SUPER_ADMIN, ADMIN, MODERATOR, and SUPPORT tiers with last-admin safeguards.");
        sub.getStyle().set("margin", "4px 0 0 0").set("color", "var(--us-text-muted)");
        titleBox.add(title, sub);

        Button createBtn = new Button("Provision New Admin", VaadinIcon.PLUS.create(), e -> openCreateDialog());
        createBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        bar.add(titleBox, createBtn);
        add(bar);
    }

    private void buildGrid() {
        grid.addClassName("setu-card");
        grid.setSizeFull();

        grid.addColumn(AdminUser::getFullName).setHeader("Admin Name").setAutoWidth(true);
        grid.addColumn(AdminUser::getEmail).setHeader("Corporate Email").setAutoWidth(true);
        grid.addComponentColumn(a -> StatusChip.create(a.getRole())).setHeader("RBAC Role").setAutoWidth(true);
        grid.addComponentColumn(a -> a.isActive() ? new StatusChip("ACTIVE", "active") : new StatusChip("DEACTIVATED", "suspended")).setHeader("Status").setAutoWidth(true);
        grid.addColumn(AdminUser::getCreatedAt).setHeader("Provisioned At").setAutoWidth(true);

        grid.addComponentColumn(a -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(true);

            Button resetPwBtn = new Button("Reset PW", VaadinIcon.KEY.create(), e -> openResetPasswordDialog(a));
            resetPwBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);

            Button toggleBtn;
            if (a.isActive()) {
                toggleBtn = new Button("Deactivate", VaadinIcon.BAN.create(), e -> {
                    new ConfirmReasonDialog(
                            "Deactivate Admin Account",
                            "Revoke all portal access for: " + a.getEmail(),
                            "Confirm Deactivation",
                            true,
                            reason -> {
                                try {
                                    adminService.toggleAdminStatus(a.getId(), false, reason);
                                    Notification.show("Admin access deactivated.");
                                    refreshGrid();
                                } catch (Exception ex) {
                                    Notification.show("Error: " + ex.getMessage(), 4000, Notification.Position.MIDDLE);
                                }
                            }
                    ).open();
                });
                toggleBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
            } else {
                toggleBtn = new Button("Reactivate", VaadinIcon.CHECK.create(), e -> {
                    new ConfirmReasonDialog(
                            "Reactivate Admin Account",
                            "Restore portal access for: " + a.getEmail(),
                            "Confirm Reactivation",
                            false,
                            reason -> {
                                adminService.toggleAdminStatus(a.getId(), true, reason);
                                Notification.show("Admin access reactivated.");
                                refreshGrid();
                            }
                    ).open();
                });
                toggleBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
            }

            actions.add(resetPwBtn, toggleBtn);
            return actions;
        }).setHeader("Actions").setAutoWidth(true);

        add(grid);
    }

    private void refreshGrid() {
        grid.setItems(new CallbackDataProvider<>(
                query -> {
                    int page = query.getOffset() / query.getLimit();
                    int size = query.getLimit();
                    return adminService.getAdminUsers("", PageRequest.of(page, size)).stream();
                },
                query -> (int) adminService.getAdminUsers("", PageRequest.of(0, 1)).getTotalElements()
        ));
    }

    private void openCreateDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Provision Administrative Account");
        dialog.setWidth("420px");

        TextField email = new TextField("Admin Email Address");
        email.setWidthFull();
        email.setPlaceholder("e.g. ops@udyogasethu.org");

        TextField name = new TextField("Full Legal Name");
        name.setWidthFull();
        name.setPlaceholder("e.g. Priya Sharma");

        ComboBox<String> role = new ComboBox<>("RBAC Authority Tier");
        role.setWidthFull();
        role.setItems("ADMIN", "MODERATOR", "SUPPORT", "SUPER_ADMIN");
        role.setValue("ADMIN");

        PasswordField password = new PasswordField("Initial Password");
        password.setWidthFull();
        password.setPlaceholder("Min 8 chars, 1 uppercase, 1 digit");
        password.setHelperText("Must contain at least 8 characters, 1 uppercase letter and 1 number");

        VerticalLayout layout = new VerticalLayout(email, name, role, password);
        layout.setPadding(false);
        dialog.add(layout);

        Button cancel = new Button("Cancel", e -> dialog.close());
        Button save = new Button("Create Admin", e -> {
            String em = email.getValue();
            String nm = name.getValue();
            String rl = role.getValue();
            String pw = password.getValue();

            if (em == null || em.trim().isEmpty() || nm == null || nm.trim().isEmpty() || pw == null || pw.trim().isEmpty()) {
                Notification.show("All fields including password are required.");
                return;
            }

            try {
                adminService.createAdmin(em, nm, rl, pw);
                Notification.show("Administrator provisioned successfully with BCrypt hashed credentials.");
                dialog.close();
                refreshGrid();
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 4000, Notification.Position.MIDDLE);
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        dialog.getFooter().add(cancel, save);
        dialog.open();
    }

    private void openResetPasswordDialog(AdminUser admin) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Reset Password for " + admin.getEmail());
        dialog.setWidth("400px");

        PasswordField newPassword = new PasswordField("New Password");
        newPassword.setWidthFull();
        newPassword.setPlaceholder("Min 8 chars, 1 uppercase, 1 digit");

        PasswordField confirmPassword = new PasswordField("Confirm Password");
        confirmPassword.setWidthFull();

        VerticalLayout layout = new VerticalLayout(newPassword, confirmPassword);
        layout.setPadding(false);
        dialog.add(layout);

        Button cancel = new Button("Cancel", e -> dialog.close());
        Button save = new Button("Update Password", e -> {
            String p1 = newPassword.getValue();
            String p2 = confirmPassword.getValue();
            if (p1 == null || p1.isBlank() || !p1.equals(p2)) {
                Notification.show("Passwords do not match or are empty.");
                return;
            }

            try {
                adminService.resetPassword(admin.getId(), p1);
                Notification.show("Password updated successfully.");
                dialog.close();
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 4000, Notification.Position.MIDDLE);
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        dialog.getFooter().add(cancel, save);
        dialog.open();
    }
}
