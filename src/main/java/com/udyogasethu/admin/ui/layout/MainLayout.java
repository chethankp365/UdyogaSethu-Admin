package com.udyogasethu.admin.ui.layout;

import com.udyogasethu.admin.repository.DisputeRepository;
import com.udyogasethu.admin.repository.JobRepository;
import com.udyogasethu.admin.repository.KycRecordRepository;
import com.udyogasethu.admin.ui.components.CommandPaletteDialog;
import com.udyogasethu.admin.ui.views.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

public class MainLayout extends AppLayout {

    private final KycRecordRepository kycRecordRepository;
    private final DisputeRepository disputeRepository;
    private final JobRepository jobRepository;

    public MainLayout(KycRecordRepository kycRecordRepository,
                      DisputeRepository disputeRepository,
                      JobRepository jobRepository) {
        this.kycRecordRepository = kycRecordRepository;
        this.disputeRepository = disputeRepository;
        this.jobRepository = jobRepository;

        setPrimarySection(Section.DRAWER);
        addHeaderContent();
        addDrawerContent();

        // Keyboard shortcut Ctrl+K / Cmd+K listener
        UI.getCurrent().getPage().executeJs(
                """
                window.addEventListener('keydown', (e) => {
                    if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
                        e.preventDefault();
                        const btn = document.getElementById('global-search-btn');
                        if (btn) btn.click();
                    }
                });
                """
        );
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        H4 pageTitle = new H4("UdyogaSethu Admin Portal");
        pageTitle.getStyle().set("margin", "0").set("font-size", "1.1rem").set("color", "var(--us-text-main)");

        // Command Palette Trigger
        Button searchBtn = new Button("Search modules (Ctrl+K)", VaadinIcon.SEARCH.create(), e -> {
            new CommandPaletteDialog().open();
        });
        searchBtn.setId("global-search-btn");
        searchBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        searchBtn.getStyle()
                .set("background", "var(--us-surface-2)")
                .set("border", "1px solid var(--us-border-subtle)")
                .set("border-radius", "8px")
                .set("padding", "6px 14px")
                .set("font-size", "0.85rem")
                .set("color", "var(--us-text-muted)");

        // Theme Toggle (Dark/Light)
        Button themeToggle = new Button(VaadinIcon.MOON.create(), e -> {
            UI.getCurrent().getPage().executeJs(
                    """
                    const current = document.documentElement.getAttribute('theme') || '';
                    if (current.includes('light')) {
                        document.documentElement.setAttribute('theme', 'dark');
                    } else {
                        document.documentElement.setAttribute('theme', 'light');
                    }
                    """
            );
        });
        themeToggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // System Health Indicator
        Span healthDot = new Span();
        healthDot.getStyle()
                .set("width", "10px")
                .set("height", "10px")
                .set("border-radius", "50%")
                .set("background", "var(--us-primary)")
                .set("box-shadow", "0 0 8px var(--us-primary)");
        healthDot.setTitle("Supabase DB & Backend: Healthy");

        // Logout Button
        Button logoutBtn = new Button("Logout", VaadinIcon.SIGN_OUT.create(), e -> {
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(
                    VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
            UI.getCurrent().getPage().setLocation("/login");
        });
        logoutBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);

        HorizontalLayout actions = new HorizontalLayout(searchBtn, themeToggle, healthDot, logoutBtn);
        actions.setAlignItems(FlexComponent.Alignment.CENTER);
        actions.setSpacing(true);

        HorizontalLayout header = new HorizontalLayout(toggle, pageTitle, actions);
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle()
                .set("padding", "10px 20px")
                .set("background", "var(--us-surface-1)")
                .set("border-bottom", "1px solid var(--us-border-subtle)");

        addToNavbar(header);
    }

    private void addDrawerContent() {
        Image logo = new Image("images/logo.png", "UdyogaSethu Logo");
        logo.setWidth("160px");
        logo.getStyle().set("margin", "20px auto 10px auto").set("display", "block");

        VerticalLayout navContainer = new VerticalLayout();
        navContainer.setPadding(false);
        navContainer.setSpacing(false);
        navContainer.setWidthFull();

        // Section 1: Core Dashboard
        navContainer.add(createSectionHeader("CORE & ANALYTICS"));
        navContainer.add(createNavItem("Overview Dashboard", VaadinIcon.DASHBOARD, DashboardView.class, null));
        navContainer.add(createNavItem("Reports & Analytics", VaadinIcon.CHART, ReportsView.class, null));

        // Section 2: Labour & Identity
        navContainer.add(createSectionHeader("LABOUR & IDENTITY"));
        navContainer.add(createNavItem("User Management", VaadinIcon.USERS, UsersView.class, null));
        navContainer.add(createNavItem("Worker Management", VaadinIcon.USER_CARD, WorkersView.class, null));
        navContainer.add(createNavItem("Service Providers", VaadinIcon.BRIEFCASE, ProvidersView.class, null));
        navContainer.add(createNavItem("Job Management", VaadinIcon.WORKPLACE, JobsView.class,
                String.valueOf(jobRepository.countByStatusIgnoreCase("open"))));

        // Section 3: Agri-Commerce
        navContainer.add(createSectionHeader("AGRI-COMMERCE"));
        navContainer.add(createNavItem("Farmer Management", VaadinIcon.HOME, FarmersView.class, null));
        navContainer.add(createNavItem("Produce Marketplace", VaadinIcon.CART, MarketplaceView.class, null));
        navContainer.add(createNavItem("Orders Management", VaadinIcon.PACKAGE, OrdersView.class, null));

        // Section 4: Finance & Security
        navContainer.add(createSectionHeader("FINANCIALS & TRUST"));
        navContainer.add(createNavItem("Payments & Ledger", VaadinIcon.MONEY_DEPOSIT, PaymentsView.class, null));
        navContainer.add(createNavItem("Identity Verification", VaadinIcon.DIPLOMA_SCROLL, VerificationView.class,
                String.valueOf(kycRecordRepository.countByStatusIgnoreCase("pending"))));
        navContainer.add(createNavItem("Reviews & Ratings", VaadinIcon.STAR, ReviewsView.class, null));
        navContainer.add(createNavItem("Complaints & Disputes", VaadinIcon.EXCLAMATION_CIRCLE_O, DisputesView.class,
                String.valueOf(disputeRepository.countByStatusIgnoreCase("open"))));

        // Section 5: Voice & Communications
        navContainer.add(createSectionHeader("AI & NOTIFICATIONS"));
        navContainer.add(createNavItem("Voice AI Monitoring", VaadinIcon.MICROPHONE, VoiceView.class, null));
        navContainer.add(createNavItem("Notifications", VaadinIcon.BELL, NotificationsView.class, null));

        // Section 6: Governance
        navContainer.add(createSectionHeader("GOVERNANCE & AUDIT"));
        navContainer.add(createNavItem("Admin Management", VaadinIcon.SHIELD, AdminsView.class, null));
        navContainer.add(createNavItem("Audit Logs", VaadinIcon.FILE_TEXT_O, AuditView.class, null));
        navContainer.add(createNavItem("Platform Settings", VaadinIcon.COG_O, SettingsView.class, null));

        Scroller scroller = new Scroller(navContainer);
        scroller.setSizeFull();

        VerticalLayout drawerLayout = new VerticalLayout(logo, scroller);
        drawerLayout.setSizeFull();
        drawerLayout.setPadding(false);
        drawerLayout.setSpacing(false);
        drawerLayout.addClassName("setu-sidebar");

        addToDrawer(drawerLayout);
    }

    private Component createSectionHeader(String title) {
        Paragraph header = new Paragraph(title);
        header.getStyle()
                .set("font-size", "0.7rem")
                .set("font-weight", "800")
                .set("letter-spacing", "0.1em")
                .set("color", "var(--us-text-muted)")
                .set("margin", "16px 16px 4px 16px");
        return header;
    }

    private Component createNavItem(String title, VaadinIcon iconType, Class<? extends Component> viewClass, String badgeText) {
        RouterLink link = new RouterLink();
        link.setRoute(viewClass);
        link.addClassName("setu-nav-item");
        link.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("text-decoration", "none")
                .set("gap", "10px");

        Icon icon = iconType.create();
        icon.setSize("18px");

        Span text = new Span(title);
        text.getStyle().set("font-size", "0.875rem").set("font-weight", "500");

        link.add(icon, text);

        if (badgeText != null && !badgeText.equals("0")) {
            Span badge = new Span(badgeText);
            badge.getStyle()
                    .set("margin-left", "auto")
                    .set("background", "rgba(255, 138, 0, 0.2)")
                    .set("color", "var(--us-amber)")
                    .set("font-size", "0.7rem")
                    .set("font-weight", "700")
                    .set("padding", "2px 6px")
                    .set("border-radius", "9999px");
            link.add(badge);
        }

        return link;
    }
}
