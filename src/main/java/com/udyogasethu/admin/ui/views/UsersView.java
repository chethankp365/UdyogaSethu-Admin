package com.udyogasethu.admin.ui.views;

import com.udyogasethu.admin.domain.Profile;
import com.udyogasethu.admin.domain.WorkerProfile;
import com.udyogasethu.admin.service.UserService;
import com.udyogasethu.admin.ui.components.ConfirmReasonDialog;
import com.udyogasethu.admin.ui.components.DetailDrawer;
import com.udyogasethu.admin.ui.components.StatusChip;
import com.udyogasethu.admin.ui.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

@Route(value = "users", layout = MainLayout.class)
@PageTitle("User Management | UdyogaSethu Admin")
@PermitAll
public class UsersView extends VerticalLayout {

    private final UserService userService;
    private final Grid<Profile> grid = new Grid<>(Profile.class, false);
    private final TextField searchField = new TextField();
    private final ComboBox<String> roleFilter = new ComboBox<>();
    private final DetailDrawer detailDrawer = new DetailDrawer("User Profile Inspection");

    public UsersView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background", "var(--us-bg-root)");

        buildHeader();
        buildFilterBar();
        buildGrid();
        refreshGrid();
    }

    private void buildHeader() {
        H2 title = new H2("User Management & Account Governance");
        title.getStyle().set("margin", "0").set("color", "var(--us-text-main)");

        Paragraph sub = new Paragraph("Server-side paged registry of all verified accounts across seeker, provider, and farmer roles.");
        sub.getStyle().set("margin", "4px 0 0 0").set("color", "var(--us-text-muted)");

        add(new VerticalLayout(title, sub));
    }

    private void buildFilterBar() {
        searchField.setPlaceholder("Search by full name or phone number...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setWidth("320px");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> refreshGrid());

        roleFilter.setPlaceholder("Filter by Role");
        roleFilter.setItems("ALL", "seeker", "provider", "farmer");
        roleFilter.setValue("ALL");
        roleFilter.addValueChangeListener(e -> refreshGrid());

        HorizontalLayout bar = new HorizontalLayout(searchField, roleFilter);
        bar.setWidthFull();
        bar.setAlignItems(FlexComponent.Alignment.CENTER);
        add(bar);
    }

    private void buildGrid() {
        grid.addClassName("setu-card");
        grid.setSizeFull();

        grid.addColumn(Profile::getFullName).setHeader("Full Name").setSortable(true).setAutoWidth(true);
        grid.addColumn(Profile::getPhone).setHeader("Phone Number").setAutoWidth(true);
        grid.addComponentColumn(p -> StatusChip.create(p.getActiveRole())).setHeader("Role").setAutoWidth(true);
        grid.addColumn(p -> p.getLanguage() != null ? p.getLanguage().toUpperCase() : "EN").setHeader("Language").setAutoWidth(true);
        grid.addComponentColumn(p -> {
            if (p.isSuspended()) {
                return new StatusChip("SUSPENDED", "suspended");
            } else {
                return new StatusChip("ACTIVE", "active");
            }
        }).setHeader("Account Status").setAutoWidth(true);

        grid.addComponentColumn(p -> {
            HorizontalLayout actions = new HorizontalLayout();

            Button viewBtn = new Button(VaadinIcon.EYE.create(), e -> openProfileDrawer(p));
            viewBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            viewBtn.setTooltipText("Inspect User");

            Button toggleBtn;
            if (p.isSuspended()) {
                toggleBtn = new Button("Restore", VaadinIcon.CHECK.create(), e -> {
                    new ConfirmReasonDialog(
                            "Restore User Account",
                            "Reactivate account access for " + p.getFullName() + " (" + p.getPhone() + ").",
                            "Confirm Restore",
                            false,
                            reason -> {
                                userService.toggleUserSuspension(p.getId(), false, reason);
                                Notification.show("User restored successfully.");
                                refreshGrid();
                            }
                    ).open();
                });
                toggleBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_TERTIARY);
            } else {
                toggleBtn = new Button("Suspend", VaadinIcon.BAN.create(), e -> {
                    new ConfirmReasonDialog(
                            "Suspend User Account",
                            "Deactivate account access for " + p.getFullName() + ". Reason will be logged in security audit trail.",
                            "Confirm Suspension",
                            true,
                            reason -> {
                                userService.toggleUserSuspension(p.getId(), true, reason);
                                Notification.show("User suspended successfully.");
                                refreshGrid();
                            }
                    ).open();
                });
                toggleBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            }

            actions.add(viewBtn, toggleBtn);
            return actions;
        }).setHeader("Actions").setAutoWidth(true);

        add(grid);
    }

    private void refreshGrid() {
        String search = searchField.getValue();
        String role = roleFilter.getValue();

        grid.setItems(new CallbackDataProvider<>(
                query -> {
                    int page = query.getOffset() / query.getLimit();
                    int size = query.getLimit();
                    return userService.getUsers(search, role, PageRequest.of(page, size)).stream();
                },
                query -> (int) userService.getUsers(search, role, PageRequest.of(0, 1)).getTotalElements()
        ));
    }

    private void openProfileDrawer(Profile profile) {
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        content.add(new Paragraph("User ID: " + profile.getId()));
        content.add(new Paragraph("Full Name: " + profile.getFullName()));
        content.add(new Paragraph("Phone: " + profile.getPhone()));
        content.add(new Paragraph("Role: " + profile.getActiveRole()));
        content.add(new Paragraph("Language: " + profile.getLanguage()));
        content.add(new Paragraph("Suspended: " + profile.isSuspended()));
        content.add(new Paragraph("Member Since: " + profile.getCreatedAt()));

        Optional<WorkerProfile> wp = userService.getWorkerProfile(profile.getId());
        wp.ifPresent(w -> {
            content.add(new Paragraph("--- Worker Profile Details ---"));
            content.add(new Paragraph("Seeker Type: " + w.getSeekerType()));
            content.add(new Paragraph("Daily Rate: ₹" + (w.getDailyRatePaise() != null ? w.getDailyRatePaise() / 100 : 0)));
            content.add(new Paragraph("Rating: " + w.getRatingAvg() + " / 5.0"));
            content.add(new Paragraph("Jobs Completed: " + w.getJobsCompletedCount()));
            content.add(new Paragraph("Bio: " + w.getBio()));
        });

        detailDrawer.setContent(content);
        detailDrawer.open();
    }
}
