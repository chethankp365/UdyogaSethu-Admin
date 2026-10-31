package com.udyogasethu.admin.ui.components;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;

public class EChartComponent extends Div {

    private final String chartId = "echart-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    private String chartOptionsJson;

    public EChartComponent(String chartOptionsJson, String height) {
        this.chartOptionsJson = chartOptionsJson;
        setId(chartId);
        setWidthFull();
        setHeight(height != null ? height : "320px");
        addClassName("setu-card");
        getStyle().set("padding", "16px").set("position", "relative");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        renderChart();
    }

    public void setOptions(String json) {
        this.chartOptionsJson = json;
        renderChart();
    }

    private void renderChart() {
        if (chartOptionsJson == null) return;

        UI.getCurrent().getPage().executeJs(
                """
                const render = () => {
                    const dom = document.getElementById($0);
                    if (!dom) return;
                    if (!window.echarts) {
                        const script = document.createElement('script');
                        script.src = 'https://cdn.jsdelivr.net/npm/echarts@5.5.0/dist/echarts.min.js';
                        script.onload = () => {
                            const chart = echarts.init(dom);
                            chart.setOption(JSON.parse($1));
                            window.addEventListener('resize', () => chart.resize());
                        };
                        document.head.appendChild(script);
                    } else {
                        let chart = echarts.getInstanceByDom(dom);
                        if (!chart) {
                            chart = echarts.init(dom);
                            window.addEventListener('resize', () => chart.resize());
                        }
                        chart.setOption(JSON.parse($1));
                    }
                };
                setTimeout(render, 50);
                """,
                chartId,
                chartOptionsJson
        );
    }
}
