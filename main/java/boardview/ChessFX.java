package boardview;

import java.io.IOException;
import java.net.InetAddress;
import gamecontrol.AIChessController;
import gamecontrol.ChessController;
import gamecontrol.GameController;
import gamecontrol.NetworkedChessController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Font;
import javafx.scene.layout.Pane;


/**
 * Main class for the chess application
 * Sets up the top level of the GUI
 * @author Gustavo
 * @version
 */
public class ChessFX extends Application {

    private GameController controller;
    private BoardView board;
    private Text state;
    private Text sideStatus;
    private VBox root;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // set up the main stage with a BoardView etc..
        controller = new ChessController();
        Text state = new Text();
        Text sideStatus = new Text();
        String ip = InetAddress.getLocalHost().toString();
        Text ipTxt = new Text(ip);
        ipTxt.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        board = new BoardView(controller, state, sideStatus);
        Pane pane = board.getView();
        root = new VBox(10);
        root.getChildren().addAll(pane);

        Button reset = new Button("Reset");
        reset.setOnAction((event) -> {
                controller = new ChessController();
                board.reset(controller);
            });
        Button playAI = new Button("Play Computer");
        playAI.setOnAction((event) -> {
                controller = new AIChessController();
                board.reset(controller);
            });
        HBox play = new HBox(10);
        play.getChildren().addAll(reset, playAI, sideStatus, state);

        Button host = new Button("Host");
        host.setOnMouseClicked(makeHostListener());
        TextField textField = new TextField();
        textField.setPromptText("Enter Host IP");
        Button join = new Button("Join");
        join.setOnMouseClicked(makeJoinListener(textField));
        HBox online = new HBox(10);
        online.getChildren().addAll(host, ipTxt, textField, join);


        VBox main = new VBox(10);
        main.getChildren().addAll(root, play, online);

        Scene scene = new Scene(main, 650, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chess");
        primaryStage.show();
    }

    private EventHandler<? super MouseEvent> makeHostListener() {
        return event -> {
            board.reset(new NetworkedChessController());
        };
    }

    private EventHandler<? super MouseEvent> makeJoinListener(TextField input) {
        return event -> {
            try {
                InetAddress addr = InetAddress.getByName(input.getText());
                GameController newController
                    = new NetworkedChessController(addr);
                board.reset(newController);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }


    public static void main(String[] args) {
        launch(args);
    }
}
