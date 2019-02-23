package be.gerard.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class StarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarterApplication.class, args);

        /*
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        context.registerBean(GuestService.class);
        context.registerBean(GuestHandler.class);

        context.refresh();

        final HttpHandler handler = WebHttpHandlerBuilder
                .webHandler(RouterFunctions.toHttpHandler(...))
                .applicationContext(context)
                .build();
                */
    }

}
