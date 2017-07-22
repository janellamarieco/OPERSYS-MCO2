package cal_train;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CalTrain extends Application {

	/** Global Variables **/

	public static Station stations[];
	public static Thread trains[];
	public static Semaphore semaphore;
	public static Semaphore mutex;
//	public static Lock station_lock = new ReentrantLock();
//	public static Condition trainArrival = station_lock.newCondition();

	/** GUI **/

	private BorderPane mainPane;
	private TextField peopleTextField,
			  		  trainTextField;

	public static void station_init(){
		
		/* NOTES: 
		 * 1. used to invoke station object
		 * 2. 8 stations lang
		 */
				
		for(int i = 0; i < 8; i++)
			stations[i] = new Station(i+1);
		
		System.out.println("INITIALIZED STATIONS\n");

	}
	
	public static void train_init(){
		
		/* NOTES:
		 * 1. 15 trains all in all
		 */
				
		for(int i = 0; i < 15; i++){
			trains[i] = new Thread(new Train((i+1), 1));
		}		
		
		System.out.println("INITIALIZED TRAINS\n");
	}
	
	public static void main(String[] args){
		
		/* INITIALIZE GLOBAL VARIABLES */
		
		stations = new Station[8];
		trains = new Thread[15];
		
		semaphore = new Semaphore(8); // max 8 stations
		mutex = new Semaphore(1); 	  // mutual exclusion for threads
		
		station_init();
		train_init();
		
		Passenger pass = null;
		
		for(int i = 0; i < 8; i++){
			if(i < 7){
				pass = new Passenger(stations[i], stations[i+1]);
			}else{
				pass = new Passenger(stations[i], stations[0]);
			}	
			
			stations[i].addPassenger(pass);
			pass.run();
		}
				
		for(int i = 0; i < 15; i++){
			trains[i].start();	
		}
		
//		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {

		mainPane = new BorderPane();
		mainPane.setId("mainPane");
		Scene scene = new Scene(mainPane, 1000, 650);
		primaryStage.setTitle("CaltrainII (OPERSYS MP2)");
		primaryStage.getIcons().add(new Image("images/trainSubIcon.png"));
		scene.getStylesheets().add("Style.css");
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(e -> terminateProgram());

		mainPane.setRight(initRightVBox());
		mainPane.setCenter(initCenterVBox());
		mainPane.setAlignment(mainPane.getRight(), Pos.CENTER);
		mainPane.setAlignment(mainPane.getCenter(), Pos.CENTER);
	}

	public VBox initRightVBox()
	{
		VBox vBox = new VBox();
		vBox.setId("vBoxRight");

		GridPane gridPane = new GridPane();
		gridPane.setId("rightGridPane");

		//Train Number
		ImageView trainIcon = new ImageView("images/trainSubIcon2.png");
		trainIcon.setFitHeight(25);
		trainIcon.setFitWidth(25);
		trainIcon.setPreserveRatio(true);

		trainTextField = new TextField();
		trainTextField.setDisable(true);
		trainTextField.setPromptText("Train Number");

		GridPane.setConstraints(trainIcon, 0, 0);
		GridPane.setConstraints(trainTextField, 1, 0);
		gridPane.getChildren().addAll(trainIcon, trainTextField);

		//People
		ImageView peopleIcon = new ImageView("images/peopleIcon2.png");
		peopleIcon.setFitHeight(25);
		peopleIcon.setFitWidth(25);
		peopleIcon.setPreserveRatio(true);

		peopleTextField = new TextField();
		peopleTextField.setDisable(true);
		peopleTextField.setPromptText("Number of People");
		Button button = new Button("^");

		GridPane.setConstraints(peopleIcon, 0, 1);
		GridPane.setConstraints(peopleTextField, 1, 1);
		gridPane.getChildren().addAll(peopleTextField);
		GridPane.setConstraints(button, 2, 1);
		gridPane.getChildren().addAll(peopleIcon, button);

		vBox.getChildren().addAll(gridPane);
		return vBox;
	}

	public VBox initCenterVBox()
    {
        VBox vBox = new VBox();
        vBox.setId("vBoxCenter");

        Pane field = new Pane();
        field.setId("centerPane");

        for(int i = 0; i < 8; i++){
            ImageView trainStation = new ImageView("images/station.png");
            trainStation.setFitHeight(100);
            trainStation.setFitWidth(100);
            trainStation.setPreserveRatio(true);

            trainStation.setOnMouseClicked(e -> {
                System.out.println("WA");
            });

            trainStation.setLayoutX(150 * (i + 1));
            trainStation.setLayoutY(0);

            if(i > 2){
                trainStation.setLayoutX(150 * (i - 3 + 1));
                trainStation.setLayoutY(300);
            }

            if(i >= 6){
                trainStation.setLayoutX(10);
                trainStation.setLayoutY(150);

                if(i == 7)
                    trainStation.setLayoutX(600);
            }

            field.getChildren().add(trainStation);
        }

        for(int i = 0; i < 16; i++){
            ImageView train1 = new ImageView("images/train2.png");
            train1.setFitHeight(50);
            train1.setFitWidth(50);

            if(i == 0){ //First Train
                train1 = new ImageView("images/train1.png");
                train1.setFitHeight(50);
                train1.setFitWidth(50);
                train1.setLayoutX(225);
                train1.setLayoutY(425);
            } else if(i < 4){
                train1.setLayoutX(225 + (75 * i));
                train1.setLayoutY(425);
            } else if(i < 8){
                train1.setLayoutX(225 + (75 * (i - 4)));
                train1.setLayoutY(450);
            } else if(i < 12){
                train1.setLayoutX(225 + (75 * (i - 8)));
                train1.setLayoutY(475);
            } else if(i < 16){
                train1.setLayoutX(225 + (75 * (i - 12)));
                train1.setLayoutY(500);
            }

            train1.setRotate(train1.getRotate() + 90);
            train1.setPreserveRatio(true);

            train1.setOnMouseClicked(e -> {
                System.out.println("WA");
            });
            train1.setId("train"+i);

            field.getChildren().add(train1);
        }

        vBox.getChildren().add(field);
        return vBox;
    }

	public void terminateProgram()
	{
		System.out.println("\nProgram has been terminated.");
		Platform.exit();
		System.exit(0);
	}
}
