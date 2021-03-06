package ch.hsr.dcc;

import ch.hsr.dcc.view.ErrorBoxController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import java.io.IOException;

//TODO use @EnableAsync here, even do it concerns an other module?
//TODO logger config https://dzone.com/articles/configuring-logback-with-spring-boot
//TODO check all catch blocks, that exceptions get logged and right exception type, maybe make custom type
//TODO handle all dbObjects that have failed true
//TODO javaFx themes https://stackoverflow.com/questions/28474914/javafx-css-themes
@SpringBootApplication
public class DistributedChatClient extends Application {

    private ConfigurableApplicationContext springContext;
    private Parent root;

    public static void main(String[] args) {
        Application.launch(DistributedChatClient.class, args);
    }

    @Override
    public void init() throws IOException {
        springContext = SpringApplication.run(DistributedChatClient.class);
        Thread.setDefaultUncaughtExceptionHandler(springContext.getBean(ErrorBoxController.class));

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/root.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        root = fxmlLoader.load();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setOnCloseRequest(windowEvent -> {
            windowEvent.consume();
            stop();
        });

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Chat client");
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    //TODO handle case when exception get's thrown by shutdown
    //TODO show in view that shutdown is happening @PreDestroy
    @Override
    public void stop() {
        springContext.close();
        System.exit(0);
    }
}
