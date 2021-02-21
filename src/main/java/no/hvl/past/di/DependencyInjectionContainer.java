package no.hvl.past.di;

import no.hvl.past.graph.Universe;
import no.hvl.past.plugin.MetaRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DependencyInjectionContainer {

    private final ApplicationContext applicationContext;

    private DependencyInjectionContainer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public MetaRegistry getPluginRegistry() {
        return this.applicationContext.getBean(MetaRegistry.class);
    }

    public Universe getUniverse() {
        return this.applicationContext.getBean(Universe.class);
    }

    public PropertyHolder getPropertyHolder() {
        return this.applicationContext.getBean(PropertyHolder.class);
    }

    public ServerStarter getServer() {
        return this.applicationContext.getBean(ServerStarter.class);
    }

    public void setUpLogging() {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        Layout layout = PatternLayout.newBuilder().withConfiguration(config).withPattern("%d %p %c [%t] %m %ex%n").build();
        FileAppender fileAppender = FileAppender.newBuilder()
                .setName("configuredFile")
                .withFileName(getPropertyHolder().getLogDir() + File.separator + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".log")
                .setLayout(layout)
                .build();
        fileAppender.start();
        config.addAppender(fileAppender);
        config.getRootLogger().removeAppender("file");
        config.getRootLogger().addAppender(fileAppender, null, null);
        config.getRootLogger().setLevel(getPropertyHolder().getLogLevel());
        ctx.updateLoggers();
    }

    public static DependencyInjectionContainer create() throws IOException {
        String basePath = System.getProperty("user.dir");
        return create(basePath);
    }

    public static DependencyInjectionContainer create(String basePath) throws IOException {
        File file = new File(basePath, "corrlang.conf");
        return create(file);
    }

    public static DependencyInjectionContainer create(File configFilePath) throws IOException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-bean-config.xml");
        DependencyInjectionContainer dependencyInjectionContainer = new DependencyInjectionContainer(applicationContext);
        PropertyHolder propertyHolder = dependencyInjectionContainer.getPropertyHolder();
        if (configFilePath.exists()) {
            propertyHolder.load(configFilePath);
        } else {
            propertyHolder.bootstrap(configFilePath);
        }
        return dependencyInjectionContainer;
    }

}
