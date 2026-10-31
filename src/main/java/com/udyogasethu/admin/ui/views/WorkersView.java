package com.udyogasethu.admin.ui.views;

import com.udyogasethu.admin.domain.Profile;
import com.udyogasethu.admin.domain.WorkerProfile;
import com.udyogasethu.admin.service.UserService;
import com.udyogasethu.admin.ui.components.DetailDrawer;
import com.udyogasethu.admin.ui.components.StatusChip;
import com.udyogasethu.admin.ui.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

@Route(value = "workers", layout = MainLayout.class)
@PageTitle("Worker Management | UdyogaSethu Admin")
@PermitAll
public class WorkersView extends VerticalLayout {

    private final UserService userService;
    private final Grid<WorkerProfile> grid = new Grid<>(WorkerProfile.class, false);
    private final DetailDrawer detailDrawer = new DetailDrawer("Worker Profile Inspection");

    public WorkersView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background", "var(--us-bg-root)");

        buildHeader();
        buildGrid();
        refreshGrid();
    }

    private void buildHeader() {
        H2 title = new H2("Worker & Daily Wage Seeker Registry");
        title.getStyle().set("margin", "0").set("color", "var(--us-text-main)");

        Paragraph sub = new Paragraph("Inspection of skills, daily/hourly rates, driving licences, and verified trust badges.");
        sub.getStyle().set("margin", "4px 0 0 0").set("color", "var(--us-text-muted)");

        add(new VerticalLayout(title, sub));
    }

    private void buildGrid() {
        grid.addClassName("setu-card");
        grid.setSizeFull();

        grid.addColumn(w -> {
            Optional<Profile> p = userService.getUserById(w.getUserId());
            return p.map(Profile::getFullName).orElse(w.getUserId().toString());
        }).setHeader("Worker Name").setAutoWidth(true);

        grid.addComponentColumn(w -> StatusChip.create(w.getSeekerType())).setHeader("Seeker Type").setAutoWidth(true);
        grid.addColumn(w -> "₹" + (w.getDailyRatePaise() != null ? w.getDailyRatePaise() / 100 : 0)).setHeader("Daily Wage").setAutoWidth(true);
        grid.addColumn(w -> w.getRatingAvg() != null ? w.getRatingAvg() + " ★" : "N/A").setHeader("Avg Rating").setAutoWidth(true);
        grid.addColumn(WorkerProfile::getJobsCompletedCount).setHeader("Jobs Completed").setAutoWidth(true);

        grid.addComponentColumn(w -> {
            boolean hasLicence = Boolean.TRUE.equals(w.getHasDrivingLicence());
            return hasLicence ? new StatusChip("LICENSED", "active") : new StatusChip("NONE", "suspended");
        }).setHeader("Driving Licence").setAutoWidth(true);

        grid.addComponentColumn(w -> {
            boolean isVerified = Boolean.TRUE.equals(w.getIsVerifiedBadge());
            return isVerified ? new StatusChip("VERIFIED", "active") : new StatusChip("UNVERIFIED", "pending");
        }).setHeader("Trust Badge").setAutoWidth(true);

        grid.addComponentColumn(w -> {
            Button inspectBtn = new Button(VaadinIcon.EYE.create(), e -> openWorkerDrawer(w));
            inspectBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            return inspectBtn;
        }).setHeader("Inspect").setAutoWidth(true);

        add(grid);
    }

    private void refreshGrid() {
        grid.setItems(new CallbackDataProvider<>(
                query -> {
                    int page = query.getOffset() / query.getLimit();
                    int size = query.getLimit();
                    return userService.getWorkerProfiles(PageRequest.of(page, size)).stream();
                },
                query -> (int) userService.getWorkerProfiles(PageRequest.of(0, 1)).getTotalElements()
        ));
    }

    private void openWorkerDrawer(WorkerProfile w) {
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        content.add(new Paragraph("User ID: " + w.getUserId()));
        content.add(new Paragraph("Seeker Type: " + w.getSeekerType()));
        content.add(new Paragraph("Daily Rate: ₹" + (w.getDailyRatePaise() != null ? w.getDailyRatePaise() / 100 : 0)));
        content.add(new Paragraph("Hourly Rate: ₹" + (w.getHourlyRatePaise() != null ? w.getHourlyRatePaise() / 100 : 0)));
        content.add(new Paragraph("Bio: " + (w.getBio() != null ? w.getBio() : "No bio provided.")));
        content.add(new Paragraph("Jobs Completed: " + w.getJobsCompletedCount()));
        content.add(new Paragraph("Average Rating: " + w.getRatingAvg()));

        detailDrawer.setContent(content);
        detailDrawer.open();
    }
}
