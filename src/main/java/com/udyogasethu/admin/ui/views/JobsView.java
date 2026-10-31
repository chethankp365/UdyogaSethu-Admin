package com.udyogasethu.admin.ui.views;

import com.udyogasethu.admin.domain.Job;
import com.udyogasethu.admin.domain.JobApplication;
import com.udyogasethu.admin.domain.JobAssignment;
import com.udyogasethu.admin.domain.Profile;
import com.udyogasethu.admin.service.JobService;
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
import com.vaadin.flow.component.html.H4;
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

import java.util.List;
import java.util.Optional;

@Route(value = "jobs", layout = MainLayout.class)
@PageTitle("Job Management | UdyogaSethu Admin")
@PermitAll
public class JobsView extends VerticalLayout {

    private final JobService jobService;
    private final UserService userService;
    private final Grid<Job> grid = new Grid<>(Job.class, false);
    private final TextField searchField = new TextField();
    private final ComboBox<String> statusFilter = new ComboBox<>();
    private final DetailDrawer detailDrawer = new DetailDrawer("Job & Applications Inspection");

    public JobsView(JobService jobService, UserService userService) {
        this.jobService = jobService;
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
        H2 title = new H2("Job Management & Shift Oversight");
        title.getStyle().set("margin", "0").set("color", "var(--us-text-main)");

        Paragraph sub = new Paragraph("Inspection of work postings, 150m geofence parameters, worker applications, and contracted shift states.");
        sub.getStyle().set("margin", "4px 0 0 0").set("color", "var(--us-text-muted)");

        add(new VerticalLayout(title, sub));
    }

    private void buildFilterBar() {
        searchField.setPlaceholder("Search jobs by title...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setWidth("320px");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> refreshGrid());

        statusFilter.setPlaceholder("Filter by Status");
        statusFilter.setItems("ALL", "open", "filled", "closed", "cancelled");
        statusFilter.setValue("ALL");
        statusFilter.addValueChangeListener(e -> refreshGrid());

        HorizontalLayout bar = new HorizontalLayout(searchField, statusFilter);
        bar.setWidthFull();
        bar.setAlignItems(FlexComponent.Alignment.CENTER);
        add(bar);
    }

    private void buildGrid() {
        grid.addClassName("setu-card");
        grid.setSizeFull();

        grid.addColumn(Job::getTitle).setHeader("Job Title").setAutoWidth(true);
        grid.addColumn(j -> {
            Optional<Profile> p = userService.getUserById(j.getProviderId());
            return p.map(Profile::getFullName).orElse(j.getProviderId() != null ? j.getProviderId().toString() : "N/A");
        }).setHeader("Employer / Provider").setAutoWidth(true);

        grid.addColumn(j -> "₹" + (j.getWagePaise() != null ? j.getWagePaise() / 100 : 0) + " (" + j.getWageType() + ")").setHeader("Agreed Wage").setAutoWidth(true);
        grid.addColumn(j -> (j.getSlotsFilled() != null ? j.getSlotsFilled() : 0) + " / " + (j.getSlotsTotal() != null ? j.getSlotsTotal() : 0)).setHeader("Slots Filled").setAutoWidth(true);
        grid.addColumn(j -> (j.getGeofenceRadiusM() != null ? j.getGeofenceRadiusM() : 150) + " m").setHeader("Geofence Radius").setAutoWidth(true);
        grid.addComponentColumn(j -> StatusChip.create(j.getStatus())).setHeader("Status").setAutoWidth(true);

        grid.addComponentColumn(j -> {
            HorizontalLayout actions = new HorizontalLayout();

            Button inspectBtn = new Button(VaadinIcon.EYE.create(), e -> openJobDrawer(j));
            inspectBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            if ("open".equalsIgnoreCase(j.getStatus())) {
                Button cancelBtn = new Button("Cancel", VaadinIcon.CLOSE_CIRCLE.create(), e -> {
                    new ConfirmReasonDialog(
                            "Cancel Job Posting",
                            "Deactivate job posting: '" + j.getTitle() + "'. This will mark the job cancelled.",
                            "Confirm Cancellation",
                            true,
                            reason -> {
                                jobService.updateJobStatus(j.getId(), "cancelled", reason);
                                Notification.show("Job cancelled.");
                                refreshGrid();
                            }
                    ).open();
                });
                cancelBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
                actions.add(inspectBtn, cancelBtn);
            } else {
                actions.add(inspectBtn);
            }

            return actions;
        }).setHeader("Actions").setAutoWidth(true);

        add(grid);
    }

    private void refreshGrid() {
        String search = searchField.getValue();
        String status = statusFilter.getValue();

        grid.setItems(new CallbackDataProvider<>(
                query -> {
                    int page = query.getOffset() / query.getLimit();
                    int size = query.getLimit();
                    return jobService.getJobs(search, status, PageRequest.of(page, size)).stream();
                },
                query -> (int) jobService.getJobs(search, status, PageRequest.of(0, 1)).getTotalElements()
        ));
    }

    private void openJobDrawer(Job job) {
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        content.add(new Paragraph("Job ID: " + job.getId()));
        content.add(new Paragraph("Title: " + job.getTitle()));
        content.add(new Paragraph("Wage: ₹" + (job.getWagePaise() != null ? job.getWagePaise() / 100 : 0) + " (" + job.getWageType() + ")"));
        content.add(new Paragraph("Slots: " + job.getSlotsFilled() + " filled of " + job.getSlotsTotal() + " total"));
        content.add(new Paragraph("Geofence Radius: " + job.getGeofenceRadiusM() + " meters"));
        content.add(new Paragraph("Status: " + job.getStatus()));

        H4 appHeading = new H4("Active Applicants (" + jobService.getApplicationsForJob(job.getId()).size() + ")");
        appHeading.getStyle().set("margin-top", "16px");
        content.add(appHeading);

        List<JobApplication> apps = jobService.getApplicationsForJob(job.getId());
        if (apps.isEmpty()) {
            content.add(new Paragraph("No applications submitted for this job yet."));
        } else {
            for (JobApplication app : apps) {
                HorizontalLayout appRow = new HorizontalLayout();
                appRow.setWidthFull();
                appRow.setAlignItems(FlexComponent.Alignment.CENTER);
                appRow.getStyle().set("padding", "6px").set("background", "var(--us-surface-2)").set("border-radius", "6px");

                Span workerSpan = new Span("Worker ID: " + app.getWorkerId());
                StatusChip statusChip = StatusChip.create(app.getStatus());
                appRow.add(workerSpan, statusChip);
                content.add(appRow);
            }
        }

        detailDrawer.setContent(content);
        detailDrawer.open();
    }
}
