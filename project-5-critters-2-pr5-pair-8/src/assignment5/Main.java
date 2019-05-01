/*
 * CRITTERS GUI Main.java
 * EE422C Project 5 submission by
 * Replace <...> with your actual data.
 * Fawadul Haq
 * fh5277
 * 16225
 * Drew Bernard
 * dhb653
 * 16225
 * Slip days used: 0
 * Spring 2019
 */

package assignment5;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.stage.Screen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;


import static assignment5.Critter.*;

public class Main extends Application {

	// DEFAULT PARAMETERS ///////////////////////////////////////
	private static final int DEFAULT_WALK_ENERGY_COST = 2;
	private static final int DEFAULT_RUN_ENERGY_COST = 5;
	private static final int DEFAULT_REST_ENERGY_COST = 1;
	private static final int DEFAULT_MIN_REPRODUCE_ENERGY = 20;
	private static final int DEFAULT_REFRESH_CLOVER_COUNT = 10;
	private static final int DEFAULT_PHOTOSYNTHESIS_ENERGY_AMOUNT = 1;
	private static final int DEFAULT_START_ENERGY = 100;
	private static final int DEFAULT_LOOK_ENERGY_COST = 1;
	/////////////////////////////////////////////////////////////

	public static String myPackage = Main.class.getPackage().toString().split(" ")[1];
	public static GridPane world;
	public static HBox root;
	public static String BGSetting;
    public static double size;	// length of a cell in the grid
    public static ArrayList<Label> statsData = new ArrayList<Label>();

    public static JFXSlider aniSlider;
    public static boolean inAnimation = false;

    public static boolean zoomPressed = false;


    @Override
    public void start(Stage primaryStage) {
        buildUI(primaryStage);
    }

    /**
     * Builds and wires the GUI for Project 5
     *
     * @param primaryStage
     */
    public void buildUI(Stage primaryStage) {
        // Create formatter for numbers only in TextFields
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();

            if (text.matches("[0-9]*")) {
                return change;
            }

            return null;
        };

        //Overall box
        root = new HBox(10);
        root.setPadding(new Insets(5));

        try {
			makeBG();
		} catch (IOException e1) {}
        root.setStyle(BGSetting);

        // Tabpane
        TabPane tabpane = new TabPane();
        tabpane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabpane.setMinWidth(300);
        tabpane.setPrefWidth(300);
        tabpane.setStyle("-fx-background-image: url(forest_background.jpg);");

        Tab controls = new Tab("Controls");
        Tab stats = new Tab("Stats");
        Tab parameters = new Tab("Parameters");

        controls.setStyle("-fx-background-color: green;");
        stats.setStyle("-fx-background-color: green;");
        parameters.setStyle("-fx-background-color: green;");

        GridPane controlsPane = new GridPane();
        controlsPane.setPadding(new Insets(5));


    // Time Step
        // Label
        Label doTimeStep = new Label("Pass Time");
        doTimeStep.setTextFill(Color.WHITE);
        controlsPane.addRow(0,doTimeStep);

        // Row
        HBox stepBox = new HBox(5);
        TextField stepTF = new TextField();
        stepTF.setPadding(new Insets(8));
        stepTF.setPrefWidth(80);
        stepTF.setMinHeight(30);
        stepTF.setText("Days");
        stepTF.getStyleClass().add("text-field");
        TextFormatter<String> stepFormatter = new TextFormatter<>(filter);
        stepTF.setTextFormatter(stepFormatter);
        stepBox.getChildren().add(stepTF);

        // Button
        JFXButton stepGo = new JFXButton("Skip");
        stepGo.setMaxWidth(60);
        stepGo.getStyleClass().add("button-raised");
        stepBox.getChildren().add(stepGo);

        controlsPane.addRow(1, stepBox);

// Adding space between features
HBox empty = new HBox();
empty.getChildren().add(new Label());
controlsPane.addRow(2, empty);

    // Animation
        Label animation = new Label("Watch World");
        animation.setTextFill(Color.WHITE);
        controlsPane.addRow(3,animation);

        VBox aniBox = new VBox();
        Label frameLbl = new Label("Speed");
        frameLbl.setTextFill(Color.WHITE);
        frameLbl.setLayoutX(80);
        aniSlider = new JFXSlider();
        aniSlider.setPrefWidth(80);
        aniSlider.setStyle("-fx-progress-color: #1E1B18;");
        Label animateStart = new Label("Start");
        animateStart.setTextFill(Color.WHITE);

        aniBox.getChildren().addAll(frameLbl,aniSlider,animateStart);
        controlsPane.addRow(4, aniBox);

        // Button
        JFXToggleButton animationSwitch = new JFXToggleButton();
        controlsPane.addRow(5, animationSwitch);

    // Set Seed
        // Label
        Label seedLbl = new Label("Set Seed Value");
        seedLbl.setTextFill(Color.WHITE);
        controlsPane.addRow(6,seedLbl);

        // Text Field
        HBox seedBox = new HBox(5);
        TextField seedTF = new TextField();
        seedTF.setMaxWidth(80);
        seedTF.setPrefWidth(80);
        seedTF.setMinHeight(30);
        TextFormatter<String> seedFormatter = new TextFormatter<>(filter);
        seedTF.setTextFormatter(seedFormatter);
        seedBox.getChildren().add(seedTF);

        // Button
        JFXButton seedGo = new JFXButton("Enter");
        seedGo.getStyleClass().add("button-raised");
        seedGo.setMaxWidth(60);
        seedBox.getChildren().add(seedGo);
        controlsPane.addRow(7, seedBox);

// More blank space
HBox empty2 = new HBox();
empty2.getChildren().add(new Label());
controlsPane.addRow(8, empty2);

    // Make Critter
    	// Label
        Label makeLbl = new Label("Create Critters");
        makeLbl.setTextFill(Color.WHITE);
        controlsPane.addRow(9, makeLbl);

        // Row
        HBox makeBox = new HBox(5);
        TextField makeTF = new TextField();
        makeTF.setMaxWidth(80);
        makeTF.setMinHeight(30);
        makeTF.setText("Number");
        TextFormatter<String> makeFormatter = new TextFormatter<>(filter);
        makeTF.setTextFormatter(makeFormatter);

        ComboBox<String> critType = new ComboBox<String>();
        critType.setMaxWidth(100);
        critType.setMinHeight(30);
        critType.getItems().addAll(getAllClasses());

        // Button
        JFXButton makeGo = new JFXButton("Spawn");
        makeGo.getStyleClass().add("button-raised");

        makeBox.getChildren().addAll(critType, makeTF, makeGo);
        controlsPane.addRow(10, makeBox);

// More blank space
HBox empty3 = new HBox();
empty3.getChildren().add(new Label());
controlsPane.addRow(11, empty3);

    // Zoom
        JFXButton zoom = new JFXButton("Zoom");
        zoom.getStyleClass().add("button-raised");
        controlsPane.addRow(12, zoom);

// More blank space
HBox empty4 = new HBox();
empty4.getChildren().add(new Label());
controlsPane.addRow(13, empty4);

        //Clear world button
        //Button
        JFXButton clearButt = new JFXButton("DESTROY");
        clearButt.getStyleClass().add("button-raised");
        controlsPane.addRow(14,clearButt);

// More blank space
HBox empty5 = new HBox();
empty5.getChildren().add(new Label());
controlsPane.addRow(15, empty5);

     // Quit
        JFXButton quitButt = new JFXButton("QUIT");
        quitButt.getStyleClass().add("button-raised");
        controlsPane.addRow(16, quitButt);

    // Set content of controls pane
        controls.setContent(controlsPane);

    // Stats tab
        GridPane statsPane = new GridPane();
        statsPane.setVgap(5);
        statsPane.setHgap(5);

        // Labels and Checkboxes
        ObservableList<String> allClasses = getAllClasses();

        for(String className : allClasses) {
        	Label statsClass = new Label();
        	statsClass.setTextFill(Color.WHITE);
        	statsClass.getStyleClass().add("param-label");
        	statsClass.setText(className);
        	statsPane.addColumn(0, statsClass);

        	// Make checkbox
        	JFXCheckBox C = new JFXCheckBox();

        	// Make label
        	Label newLbl = new Label();
        	newLbl.setTextFill(Color.WHITE);
        	newLbl.setText("No critter of this type.");
        	newLbl.setVisible(false);
        	// create event
        	EventHandler<ActionEvent> event = A -> {
                if(C.isSelected())
                    newLbl.setVisible(true);
                else
                    newLbl.setVisible(false);
            };

        	// set checkbox on action to event
        	C.setOnAction(event);

        	// Add features onto statsPane
        	statsPane.addColumn(1, C);
        	statsData.add(newLbl);
        	statsPane.addColumn(2, newLbl);
        }
        stats.setContent(statsPane);

    // Parameters tab
        VBox parametersPane = new VBox();
        GridPane parametersGrid = new GridPane();

        // Create textfield labels

        Label walk = new Label("Walk energy cost"); walk.setTextFill(Color.WHITE);
        walk.getStyleClass().add("param-label");
        walk.setMinWidth(200);

        Label run = new Label("Run energy cost"); run.setTextFill(Color.WHITE);
        run.getStyleClass().add("param-label");
        run.setMinWidth(200);

        Label rest = new Label("Rest energy cost"); rest.setTextFill(Color.WHITE);
        rest.getStyleClass().add("param-label");
        rest.setMinWidth(200);

        Label look = new Label("Look energy cost"); look.setTextFill(Color.WHITE);
        look.getStyleClass().add("param-label");
        look.setMinWidth(200);

        Label rep = new Label("Min reproduce energy"); rep.setTextFill(Color.WHITE);
        rep.getStyleClass().add("param-label");
        rep.setMinWidth(200);

        Label clovCount = new Label("Refresh clover count"); clovCount.setTextFill(Color.WHITE);
        clovCount.getStyleClass().add("param-label");
        clovCount.setMinWidth(200);

        Label clovEnergy = new Label("Clover energy amount"); clovEnergy.setTextFill(Color.WHITE);
        clovEnergy.getStyleClass().add("param-label");
        clovEnergy.setMinWidth(200);


        Label startEnergy = new Label("Start energy"); startEnergy.setTextFill(Color.WHITE);
        startEnergy.getStyleClass().add("param-label");
        startEnergy.setMinWidth(200);

        // Add Labels
        parametersGrid.addColumn(0,walk,run,rest,look,rep,clovCount,clovEnergy,startEnergy);

// Making space
Label makeSpace = new Label();
makeSpace.setMinWidth(60);
parametersGrid.addColumn(1, makeSpace);

        // Create text fields
        TextField walkTF = new TextField();
        walkTF.setText(Integer.toString(DEFAULT_WALK_ENERGY_COST));
        walkTF.setMaxWidth(50);

        TextField runTF = new TextField();
        runTF.setText(Integer.toString(DEFAULT_RUN_ENERGY_COST));
        runTF.setMaxWidth(50);

        TextField restTF = new TextField();
        restTF.setText(Integer.toString(DEFAULT_REST_ENERGY_COST));
        restTF.setMaxWidth(50);

        TextField lookTF = new TextField();
        lookTF.setText(Integer.toString(DEFAULT_LOOK_ENERGY_COST));
        lookTF.setMaxWidth(50);

        TextField repTF = new TextField();
        repTF.setText(Integer.toString(DEFAULT_MIN_REPRODUCE_ENERGY));
        repTF.setMaxWidth(50);

        TextField clovCountTF = new TextField();
        clovCountTF.setText(Integer.toString(DEFAULT_REFRESH_CLOVER_COUNT));
        clovCountTF.setMaxWidth(50);

        TextField clovEnergyTF = new TextField();
        clovEnergyTF.setText(Integer.toString(DEFAULT_PHOTOSYNTHESIS_ENERGY_AMOUNT));
        clovEnergyTF.setMaxWidth(50);

        TextField startEnergyTF = new TextField();
        startEnergyTF.setText(Integer.toString(DEFAULT_START_ENERGY));
        startEnergyTF.setMaxWidth(50);

        // Add TFs
        parametersGrid.addColumn(2,walkTF,runTF,restTF,lookTF,repTF,clovCountTF,clovEnergyTF,startEnergyTF);

        parametersGrid.getColumnConstraints().add(new ColumnConstraints(120));

        // Create parameter buttons
        JFXButton updateValues = new JFXButton("Update");
        updateValues.getStyleClass().add("button-raised");

        JFXButton resetValues = new JFXButton("Reset");
        resetValues.getStyleClass().add("button-raised");

        // Add all
        parametersPane.getChildren().addAll(parametersGrid,updateValues,resetValues);
        parameters.setContent(parametersPane);


    // Create the Critter Grid
        world = new GridPane();
        world.setMinWidth(518);
        world.setMinHeight(525);
        Label width = new Label();
        Label height = new Label();
        world.add(width,0,0);
        world.add(height,0,1);

        Critter.displayWorld(world, 30);

    // Assemble Stage
        Scene scene = new Scene(root);
        Slider slider = new Slider(0.5,2,1);
        tabpane.getTabs().addAll(controls, stats, parameters);
        root.getChildren().addAll(tabpane,world);
        root.setMinWidth(tabpane.getMinWidth()+world.getMinWidth());
        root.setMinHeight(world.getMinHeight());

        scene.widthProperty().addListener(observable -> windowResizingX(primaryStage, root, world, tabpane.getWidth() +
                root.getPadding().getRight()));
        scene.heightProperty().addListener(observable -> windowResizingY(primaryStage, root, world, tabpane.getWidth() +
                root.getPadding().getRight()));
        scene.getStylesheets().add("controlstyles.css");

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setMinWidth(root.getMinWidth());
        primaryStage.setMinHeight(root.getMinHeight() + root.getInsets().getBottom());
        primaryStage.setMaxWidth(primaryScreenBounds.getWidth());
        primaryStage.setMaxHeight(primaryScreenBounds.getHeight());
        primaryStage.setTitle("Project 5");
        primaryStage.setScene(scene);
        primaryStage.show();

        // verify initial grid placement in stage is correct
        windowResizingX(primaryStage, root, world, tabpane.getWidth() +
                root.getPadding().getRight());
        windowResizingY(primaryStage, root, world, tabpane.getWidth() +
                root.getPadding().getRight());

        /////////  BUTTONS/HANDLERS ///////////
        // wire stepGo button
        stepGo.setOnAction((ActionEvent e) -> {
            Integer steps = Integer.parseInt(stepTF.getText());
            for(int i = 0; i < steps; i++) {
                worldTimeStep();
                displayWorld(world, size);
                updateStats();
            }
        });

        stepTF.setOnMouseClicked(e -> {
            stepTF.setText("");
        });

        makeTF.setOnMouseClicked(e -> {
            makeTF.setText("");
        });

        // wire seedGo button
        seedGo.setOnAction(e -> {
            Integer seed = Integer.parseInt(seedTF.getText());
            Critter.setSeed(seed);
        });

        // wire Destroy world button
        clearButt.setOnAction(e -> {
            Critter.clearWorld();
            displayWorld(world, size);
        });

        // wire parameter reset button
        resetValues.setOnAction(e -> {
            Params.WALK_ENERGY_COST = DEFAULT_WALK_ENERGY_COST;
            walkTF.setText(Integer.toString(Params.WALK_ENERGY_COST));
            Params.RUN_ENERGY_COST = DEFAULT_RUN_ENERGY_COST;
            runTF.setText(Integer.toString(Params.RUN_ENERGY_COST));
            Params.REST_ENERGY_COST = DEFAULT_REST_ENERGY_COST;
            restTF.setText(Integer.toString(Params.REST_ENERGY_COST));
            Params.MIN_REPRODUCE_ENERGY = DEFAULT_MIN_REPRODUCE_ENERGY;
            repTF.setText(Integer.toString(Params.MIN_REPRODUCE_ENERGY));
            Params.REFRESH_CLOVER_COUNT = DEFAULT_REFRESH_CLOVER_COUNT;
            clovCountTF.setText(Integer.toString(Params.REFRESH_CLOVER_COUNT));
            Params.PHOTOSYNTHESIS_ENERGY_AMOUNT = DEFAULT_PHOTOSYNTHESIS_ENERGY_AMOUNT;
            clovEnergyTF.setText(Integer.toString(Params.PHOTOSYNTHESIS_ENERGY_AMOUNT));
            Params.LOOK_ENERGY_COST = DEFAULT_LOOK_ENERGY_COST;
            lookTF.setText(Integer.toString(Params.LOOK_ENERGY_COST));
            Params.START_ENERGY = DEFAULT_START_ENERGY;
            startEnergyTF.setText(Integer.toString(Params.START_ENERGY));
        });

        // wire makeGo button
        makeGo.setOnAction((ActionEvent e) -> {
            Integer amount = Integer.parseInt(makeTF.getText());
            for(int i = 0; i < amount; i++) {
                try {
                    createCritter(critType.getValue());
                } catch (InvalidCritterException ex) {}
            }
            Critter.displayWorld(world, world.getChildren().get(0).getBoundsInParent().getWidth()-1);
            updateStats();
        });

        // wire zoom button
        zoom.setOnAction(e -> {
            if (zoomPressed == true) {
                zoomPressed = false;
                root.getChildren().addAll(tabpane,world);
                scene.setRoot(root);
            } else {
                zoomPressed = true;
                ScrollPane worldScroll = new ScrollPane(world);
                worldScroll.setStyle(BGSetting);
                ZoomingPane zoomingPane = new ZoomingPane(worldScroll);
                zoomingPane.zoomFactorProperty().bind(slider.valueProperty());
                BorderPane zoomWindow = new BorderPane(zoomingPane, null, null, slider, null);
                HBox zoomOn = new HBox(tabpane,zoomWindow);
                zoomOn.setPadding(new Insets(5));
                zoomOn.setStyle(BGSetting);
                scene.setRoot(zoomOn);
            }
        });

        // wire parameters update button
        updateValues.setOnAction((ActionEvent e) ->{
            Params.WALK_ENERGY_COST = Integer.parseInt(walkTF.getText());
            Params.RUN_ENERGY_COST = Integer.parseInt(runTF.getText());
            Params.REST_ENERGY_COST = Integer.parseInt(restTF.getText());
            Params.MIN_REPRODUCE_ENERGY = Integer.parseInt(repTF.getText());
            Params.REFRESH_CLOVER_COUNT = Integer.parseInt(clovCountTF.getText());
            Params.PHOTOSYNTHESIS_ENERGY_AMOUNT = Integer.parseInt(clovEnergyTF.getText());
            Params.START_ENERGY = Integer.parseInt(startEnergyTF.getText());
            Params.LOOK_ENERGY_COST = Integer.parseInt(lookTF.getText());
        });

        // wire animation button
        animationSwitch.setOnAction((ActionEvent e) -> {
            if(animationSwitch.isSelected()) {
                makeGo.setDisable(true);
                seedGo.setDisable(true);
                stepGo.setDisable(true);
                inAnimation = true;
                Thread t1 = new Thread(new Animation());
                t1.start();
            } else {
                makeGo.setDisable(false);
                seedGo.setDisable(false);
                stepGo.setDisable(false);
                inAnimation = false;
            }
        });

        // wire Quit button
        quitButt.setOnAction((ActionEvent e) -> {
        	System.exit(0);
        });

        ///////////////////////////////////////
    }


    /**
     * A runnable class that handle the animation of the at varying speeds
     */
    class Animation implements Runnable {
        @Override
        public void run() {
            while(inAnimation) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        worldTimeStep();
                        displayWorld(world, size);
                        updateStats();
                    }
                });
                try {
                    TimeUnit.MILLISECONDS.sleep((long)(10000/(aniSlider.getValue()+1)));
                } catch (InterruptedException e) {}
            }
        }

    }


    /**
     * Checks for proper ratio of the grid's height to width vs. the window's height to width
     * Checks horizontal resizing
     *
     * @param stage Primary stage passed into Application's start method
     * @param root Node with which the scene is set
     * @param world GridPane on which the critters are displayed
     * @param controlsWidth Width of the controls TabPane
     */
    public void windowResizingX(Stage stage , HBox root, GridPane world, double controlsWidth) {
        double cellEdge = world.getChildren().get(0).getBoundsInParent().getWidth();
        double a = stage.getWidth();
        double b = root.getInsets().getBottom()*2;
        double windowWidth = a - controlsWidth - b;
        double windowHeight = stage.getHeight() - (root.getInsets().getBottom()+5)*2;
        if(!Double.isNaN(windowWidth)) {
            if ((windowHeight - 20) / windowWidth > cellEdge * Params.WORLD_HEIGHT / (cellEdge * Params.WORLD_WIDTH)) {
                size = (windowWidth - (root.getPadding().getBottom()+5)) / Params.WORLD_WIDTH - 2;
                Critter.displayWorld(world, Math.floor(size));
            }
        }
    }

    /**
     * Checks for proper ratio of the grid's height to width vs. the window's height to width
     * Checks vertical resizing
     *
     * @param stage Primary stage passed into Application's start method
     * @param root Node with which the scene is set
     * @param world GridPane on which the critters are displayed
     * @param controlsWidth Width of the controls TabPane
     */
    public void windowResizingY(Stage stage, HBox root, GridPane world, double controlsWidth) {
        double cellEdge = world.getChildren().get(0).getBoundsInParent().getWidth();
        double windowWidth = stage.getWidth() - controlsWidth;
        double windowHeight = stage.getHeight() - (root.getInsets().getBottom()+5)*2;
        if (!Double.isNaN(windowWidth)) {
            if ((windowHeight - 20) / windowWidth < cellEdge * Params.WORLD_HEIGHT / (cellEdge * Params.WORLD_WIDTH)) {
                size = (windowHeight - (root.getPadding().getBottom()+5)*2) / (Params.WORLD_HEIGHT) - 2;
                Critter.displayWorld(world, Math.floor(size));
            }
        }
    }

    /**
     * Randomly generate a background for grid every time and edit the CSS file to
     * accommodate that during zoom.
     * @throws IOException
     */
    private void makeBG() throws IOException {
    	String imgPathEntry = System.getProperty("user.dir");

    	File base = new File(imgPathEntry + File.separatorChar
    			+ "src" + File.separatorChar + "worldBGs");

    	File myBGImg = base.listFiles()
    			[Critter.getRandomInt(base.listFiles().length)];

    	BGSetting = new String("-fx-background-image: url(" + myBGImg.getName() + ")");

    	// Change CSS to edit viewport
    	File editMe = new File(imgPathEntry + File.separatorChar
    			+ "target" + File.separatorChar + "classes"
    			+ File.separatorChar + "controlstyles.css");

    	String Old = new String();
    	String changeMe = new String();

		Scanner reader = new Scanner(editMe);

		// Copy old file
    	while (reader.hasNext()) {
    		String line = reader.nextLine();

    		if(line.contains("url(")) { // Keep track of image file to change
    			changeMe = line.substring(line.indexOf('(')+1, line.lastIndexOf(')'));
    		}
    		Old = Old + line + System.lineSeparator();
    	}


    	// Replace old image with new
    	String NewFile = Old.replaceAll(changeMe,
    			BGSetting.substring(BGSetting.indexOf('(')+1, BGSetting.lastIndexOf(')')));

    	// Replace file
    	FileWriter writer = new FileWriter(editMe);
    	writer.write(NewFile);
    	writer.close();
    }

    /**
     * Returns a list of string names of all the Critter classes in the current package
     * @return
     */
    private ObservableList<String> getAllClasses(){

    	// Finding path
    	String path = myPackage.replaceAll("\\.", File.separator);
    	ObservableList<String> CrittersNames = FXCollections.observableArrayList();

    	// Finding class path
    	String classPathEntry = System.getProperty("user.dir");

    	String name;

		// go through all files in the package
        File base = new File(classPathEntry + File.separatorChar
        		+ "src" + File.separatorChar +path);

        for (File file : base.listFiles()) {
            name = file.getName();
            // Find classes
            name = name.substring(0, name.lastIndexOf('.'));  // remove file extension

            // Check if instance of critter
            Class<?> critClass = null;
            Object critInstance = null;

            try {
            critClass = Class.forName(myPackage + "." + name);
            critInstance = critClass.getConstructor().newInstance();
            }
            catch (InstantiationException | NoSuchMethodException | ClassNotFoundException e) {
            	// These are alright.
            }
        	catch (Exception e) {}
            if(Critter.class.isInstance(critInstance)) {
            	CrittersNames.add(name);
            }
        }

        return CrittersNames;
    }

    /**
     * Finds all the instances of all living critters and updates the stats label
     * in the stats tab to reflect that information.
     */
    private void updateStats() {
    	// Run stats for every class
    	ObservableList<String> classNames = getAllClasses();

    	for(int i = 0; i < classNames.size(); i++) {
	    	// Get all instances
	    	List<Critter> currCritInstances = null;
			try {
				currCritInstances = Critter.getInstances(classNames.get(i));
			} catch (InvalidCritterException e) {}

			// Get stats
			String critStats = null;
			if(currCritInstances.isEmpty())
	    		 critStats = new String("No critters of this type.");
			else {
				try {
					/* Get class object of the specified class from input */
					Class<?> critClass = Class.forName(myPackage + "." + classNames.get(i));
					/* Get runStats method of critClass class */
					Method statsMethod = critClass.getMethod("runStats", List.class);
					/* Invoke runStats for the critter instances if the class overrides Critter's runStats */
					critStats = (String)statsMethod.invoke(critClass, currCritInstances);
				} catch (NoSuchMethodException e) {
		        	/* If the class doesn't override runStats, run Critter's runStats */
		        	critStats = Critter.runStats(currCritInstances);
				} catch (Exception e) {};
			}
	    	// update the global variable column
	    	statsData.get(i).setText(critStats);
    	}
    }

    /**
     * Defines a pane with the ability to be scaled based on an input value, like a slider
     */
    private class ZoomingPane extends Pane {
        Node content;
        private DoubleProperty zoomFactor = new SimpleDoubleProperty(1);

        /**
         * Constructor for a ZoomingPane
         * @param content
         */
        private ZoomingPane(Node content) {
            this.content = content;
            getChildren().add(content);
            Scale scale = new Scale(1, 1);
            content.getTransforms().add(scale);

            zoomFactor.addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    scale.setX(newValue.doubleValue());
                    scale.setY(newValue.doubleValue());
                    requestLayout();
                }
            });
        }

        /**
         * Lays out the children within the ZoomingPane
         */
        protected void layoutChildren() {
            Pos pos = Pos.TOP_LEFT;
            double width = getWidth();
            double height = getHeight();
            double top = getInsets().getTop();
            double right = getInsets().getRight();
            double left = getInsets().getLeft();
            double bottom = getInsets().getBottom();
            double contentWidth = (width - left - right)/zoomFactor.get();
            double contentHeight = (height - top - bottom)/zoomFactor.get();
            layoutInArea(content, left, top,
                    contentWidth, contentHeight,
                    0, null,
                    pos.getHpos(),
                    pos.getVpos());
        }

        public final Double getZoomFactor() {
            return zoomFactor.get();
        }
        public final void setZoomFactor(Double zoomFactor) {
            this.zoomFactor.set(zoomFactor);
        }
        public final DoubleProperty zoomFactorProperty() {
            return zoomFactor;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
