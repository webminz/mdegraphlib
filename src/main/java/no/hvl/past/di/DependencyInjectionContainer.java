package no.hvl.past.di;

import no.hvl.past.graph.Universe;
import no.hvl.past.plugin.MetaRegistry;
import no.hvl.past.util.FileSystemUtils;
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

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class DependencyInjectionContainer {

    public static final String SPRING_BEAN_CONFIG_XML = "spring-bean-config.xml";
    public static final String UPDATE_CONFIG_COMMAND = "updateConfig";
    private final ApplicationContext applicationContext;

    private DependencyInjectionContainer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public <B> B getBean(Class<B> type) {
        return this.applicationContext.getBean(type);
    }

    public <B> B getBean(String beanName, Class<B> returnType) {
        return this.applicationContext.getBean(beanName, returnType);
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


    private void setUpHttps() {
        if (getPropertyHolder().isSSLAllowAll()) {
            try {
                TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return null;
                            }

                            @Override
                            public void checkClientTrusted(
                                    java.security.cert.X509Certificate[] certs, String authType) {
                            }

                            @Override
                            public void checkServerTrusted(
                                    java.security.cert.X509Certificate[] certs, String authType) {
                            }
                        }};
                SSLContext context = SSLContext.getInstance("SSL");
                context.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
        }

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

    public FileSystemUtils getFSUtils() {
        return applicationContext.getBean(FileSystemUtils.class);
    }

    public static DependencyInjectionContainer create(File configFilePath) throws IOException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(SPRING_BEAN_CONFIG_XML);
        DependencyInjectionContainer dependencyInjectionContainer = new DependencyInjectionContainer(applicationContext);
        PropertyHolder propertyHolder = dependencyInjectionContainer.getPropertyHolder();
        if (configFilePath.exists()) {
            propertyHolder.load(configFilePath);
        } else {
            propertyHolder.bootstrap(configFilePath);
        }
        dependencyInjectionContainer.getFSUtils().setBaseDir(propertyHolder.getBaseDir());
        return dependencyInjectionContainer;
    }

    public void load(Properties properties) throws IOException {
        PropertyHolder propertyHolder = getPropertyHolder();
        boolean updateProperties = false;
        for (Object o : properties.keySet()) {
            String key = o.toString();
            if (key.equals(UPDATE_CONFIG_COMMAND)) {
                updateProperties = true;
            } else if (properties.containsKey(key) && properties.get(key) != null) {
                propertyHolder.set(key, properties.getProperty(key));
            }
        }
        if (updateProperties) {
            propertyHolder.persistCurrentConfig();
        }
        setUpLogging();
        setUpHttps();
    }
}
