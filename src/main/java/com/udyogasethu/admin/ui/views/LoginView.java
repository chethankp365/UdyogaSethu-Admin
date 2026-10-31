package com.udyogasethu.admin.ui.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("Login | UdyogaSethu Admin")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm loginForm = new LoginForm();

    public LoginView() {
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        getStyle()
                .set("background", "linear-gradient(135deg, #F0FDF4 0%, #F8FAFC 50%, #ECFDF5 100%)")
                .set("overflow", "hidden")
                .set("position", "relative");

        // Living Bridge dynamic HTML5 Canvas element
        Div canvasContainer = new Div();
        canvasContainer.setId("bridge-canvas-container");
        canvasContainer.getStyle()
                .set("position", "absolute")
                .set("top", "0")
                .set("left", "0")
                .set("width", "100%")
                .set("height", "100%")
                .set("pointer-events", "none")
                .set("opacity", "0.6");

        // Center Login Card
        VerticalLayout card = new VerticalLayout();
        card.addClassName("setu-card");
        card.setWidth("420px");
        card.setPadding(true);
        card.setSpacing(true);
        card.setAlignItems(FlexComponent.Alignment.CENTER);
        card.getStyle()
                .set("padding", "32px")
                .set("background", "#FFFFFF")
                .set("border", "1px solid #E2E8F0")
                .set("box-shadow", "0 20px 40px -10px rgba(22, 163, 74, 0.12), 0 8px 16px -4px rgba(0, 0, 0, 0.04)");

        Image logo = new Image("images/logo.png", "UdyogaSethu Logo");
        logo.setWidth("200px");
        logo.getStyle().set("margin-bottom", "10px");

        H2 title = new H2("Administrative Portal");
        title.getStyle()
                .set("margin", "0")
                .set("font-size", "1.3rem")
                .set("font-weight", "700")
                .set("color", "var(--us-text-main)");

        Paragraph subtitle = new Paragraph("Authorized Personnel Only · Supabase Auth");
        subtitle.getStyle()
                .set("margin", "0 0 16px 0")
                .set("font-size", "0.85rem")
                .set("color", "var(--us-text-muted)");

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setTitle("Sign In");
        i18n.getForm().setUsername("Admin Email");
        i18n.getForm().setPassword("Password");
        i18n.getForm().setSubmit("Access Setu Portal");
        i18n.getErrorMessage().setTitle("Authentication Denied");
        i18n.getErrorMessage().setMessage("Invalid credentials or user is not registered in admin.admin_users.");

        loginForm.setI18n(i18n);
        loginForm.setAction("login");
        loginForm.setForgotPasswordButtonVisible(false);

        card.add(logo, title, subtitle, loginForm);
        add(canvasContainer, card);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        // Living Bridge dynamic Canvas Javascript
        UI.getCurrent().getPage().executeJs(
                """
                const container = document.getElementById('bridge-canvas-container');
                if (!container || document.getElementById('bridge-canvas')) return;

                const canvas = document.createElement('canvas');
                canvas.id = 'bridge-canvas';
                canvas.style.width = '100%';
                canvas.style.height = '100%';
                container.appendChild(canvas);

                const ctx = canvas.getContext('2d');
                let w = canvas.width = window.innerWidth;
                let h = canvas.height = window.innerHeight;

                const nodes = [];
                for (let i = 0; i < 40; i++) {
                    nodes.push({
                        x: Math.random() * w,
                        y: Math.random() * h,
                        vx: (Math.random() - 0.5) * 0.6,
                        vy: (Math.random() - 0.5) * 0.6,
                        radius: Math.random() * 2.5 + 1.5
                    });
                }

                function draw() {
                    ctx.clearRect(0, 0, w, h);
                    ctx.fillStyle = '#16A34A';
                    ctx.strokeStyle = 'rgba(22, 163, 74, 0.2)';

                    for (let i = 0; i < nodes.length; i++) {
                        const n = nodes[i];
                        n.x += n.vx;
                        n.y += n.vy;
                        if (n.x < 0 || n.x > w) n.vx *= -1;
                        if (n.y < 0 || n.y > h) n.vy *= -1;

                        ctx.beginPath();
                        ctx.arc(n.x, n.y, n.radius, 0, Math.PI * 2);
                        ctx.fill();

                        for (let j = i + 1; j < nodes.length; j++) {
                            const n2 = nodes[j];
                            const dist = Math.hypot(n.x - n2.x, n.y - n2.y);
                            if (dist < 140) {
                                ctx.lineWidth = 1 - dist / 140;
                                ctx.beginPath();
                                ctx.moveTo(n.x, n.y);
                                ctx.lineTo(n2.x, n2.y);
                                ctx.stroke();
                            }
                        }
                    }
                    requestAnimationFrame(draw);
                }
                draw();

                window.addEventListener('resize', () => {
                    w = canvas.width = window.innerWidth;
                    h = canvas.height = window.innerHeight;
                });
                """
        );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            loginForm.setError(true);
        }
    }
}
