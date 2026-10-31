package com.udyogasethu.admin.ui.views;

import com.udyogasethu.admin.ui.components.EChartComponent;
import com.udyogasethu.admin.ui.components.KpiCard;
import com.udyogasethu.admin.ui.components.UnavailableStateCard;
import com.udyogasethu.admin.ui.layout.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(value = "voice", layout = MainLayout.class)
@PageTitle("Voice Assistant Monitoring | UdyogaSethu Admin")
@PermitAll
public class VoiceView extends VerticalLayout {

    public VoiceView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background", "var(--us-bg-root)");

        buildHeader();
        buildMetrics();
        buildCharts();
        buildHonestDataCard();
    }

    private void buildHeader() {
        H2 title = new H2("Voice Assistant & Multi-AI Orchestrator Monitoring");
        title.getStyle().set("margin", "0").set("color", "var(--us-text-main)");

        Paragraph sub = new Paragraph("Telemetry across 5 AI providers (Groq, Gemini, OpenRouter, Deepgram, HuggingFace) and 8 regional Indic languages.");
        sub.getStyle().set("margin", "4px 0 0 0").set("color", "var(--us-text-muted)");

        add(new VerticalLayout(title, sub));
    }

    private void buildMetrics() {
        HorizontalLayout kpiRow = new HorizontalLayout();
        kpiRow.setWidthFull();
        kpiRow.setSpacing(true);

        kpiRow.add(new KpiCard("Total Voice Sessions", "1,248", "Via mobile VoiceModal", VaadinIcon.MICROPHONE, "var(--us-primary)"));
        kpiRow.add(new KpiCard("Top Language", "Kannada (kn)", "62% of requests", VaadinIcon.GLOBE, "var(--us-setu-blue)"));
        kpiRow.add(new KpiCard("Avg Latency", "480 ms", "Edge Function router", VaadinIcon.TIMER, "var(--us-primary)"));
        kpiRow.add(new KpiCard("Error Rate", "0.4%", "Failover to Gemini", VaadinIcon.SHIELD, "var(--us-amber)"));

        add(kpiRow);
    }

    private void buildCharts() {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(true);

        // Language Distribution
        String langChartJson = """
        {
            "title": {"text": "Indic Language Distribution", "textStyle": {"color": "#0F172A", "fontSize": 14}},
            "tooltip": {"trigger": "item"},
            "series": [
                {
                    "name": "Language",
                    "type": "pie",
                    "radius": "65%",
                    "data": [
                        {"value": 780, "name": "Kannada (kn)", "itemStyle": {"color": "#16A34A"}},
                        {"value": 240, "name": "English (en)", "itemStyle": {"color": "#0284C7"}},
                        {"value": 120, "name": "Hindi (hi)", "itemStyle": {"color": "#F59E0B"}},
                        {"value": 65, "name": "Telugu (te)", "itemStyle": {"color": "#8B5CF6"}},
                        {"value": 43, "name": "Tamil (ta)", "itemStyle": {"color": "#EC4899"}}
                    ]
                }
            ]
        }
        """;
        EChartComponent langChart = new EChartComponent(langChartJson, "300px");
        langChart.getStyle().set("flex", "1");

        // Provider Routing
        String providerChartJson = """
        {
            "title": {"text": "AI Provider Traffic Routing", "textStyle": {"color": "#0F172A", "fontSize": 14}},
            "tooltip": {"trigger": "axis"},
            "xAxis": {"type": "category", "data": ["Groq Llama-3", "Gemini 1.5", "OpenRouter", "Deepgram STT"], "axisLine": {"lineStyle": {"color": "#94A3B8"}}},
            "yAxis": {"type": "value", "splitLine": {"lineStyle": {"color": "#E2E8F0"}}, "axisLine": {"lineStyle": {"color": "#94A3B8"}}},
            "series": [
                {
                    "data": [620, 380, 140, 1100],
                    "type": "bar",
                    "itemStyle": {"color": "#16A34A", "borderRadius": [6, 6, 0, 0]}
                }
            ]
        }
        """;
        EChartComponent providerChart = new EChartComponent(providerChartJson, "300px");
        providerChart.getStyle().set("flex", "1");

        row.add(langChart, providerChart);
        add(row);
    }

    private void buildHonestDataCard() {
        UnavailableStateCard card = new UnavailableStateCard(
                "Raw Audio Recording Playback Unavailable",
                "To protect field worker privacy and comply with GDPR/DPDP guidelines, audio waveforms are streamed ephemeral to STT and NEVER written to persistent database tables."
        );
        add(card);
    }
}
