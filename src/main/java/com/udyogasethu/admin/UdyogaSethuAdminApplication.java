package com.udyogasethu.admin;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@Theme(value = "udyogasethu")
@Push(PushMode.AUTOMATIC)
public class UdyogaSethuAdminApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(UdyogaSethuAdminApplication.class, args);
    }
}
