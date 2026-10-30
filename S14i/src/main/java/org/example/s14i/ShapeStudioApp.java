package org.example.s14i;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ShapeStudioApp extends Application {

    private final Group emblemGroup = new Group();
    private RotateTransition rotateTrans;
    private ScaleTransition scaleTrans;
    private boolean isPlaying = false;

    @Override
    public void start(Stage stage) {
        Text header = new Text("APTECH VECTOR STUDIO");
        header.setFont(new Font("Arial Black", 24));

        LinearGradient headerGrad = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#38bdf8")),   // Sky Blue
                new Stop(0.5, Color.web("#a855f7")), // Purple
                new Stop(1, Color.web("#f43f5e"))    // Rose
        );
        header.setFill(headerGrad);
        Polygon star = createStar(8, 75, 55); // 8 points, outer radius 75, inner 55
        star.setStroke(Color.web("#1e3a8a"));
        star.setStrokeWidth(2);

        Circle shield = new Circle(80); // Slightly larger than star to encompass it
        shield.setStroke(Color.web("#1e3a8a"));
        shield.setStrokeWidth(2);

        Shape badgeShape = Shape.union(star, shield);

        LinearGradient badgeGrad = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#3b82f6")),
                new Stop(1, Color.web("#1d4ed8"))
        );
        badgeShape.setFill(badgeGrad);
        badgeShape.setEffect(null); // Ensure no default effects interfere

        Circle coreOrb = new Circle(40);
        RadialGradient coreGrad = new RadialGradient(0, 0, 0.3, 0.3, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#fef3c7")), // Highlight (Top-Left)
                new Stop(0.4, Color.web("#d97706")), // Gold
                new Stop(0.8, Color.web("#b45309")), // Bronze
                new Stop(1, Color.web("#78350f"))    // Dark Shadow
        );
        coreOrb.setFill(coreGrad);
        coreOrb.setStroke(Color.web("#92400e"));
        coreOrb.setStrokeWidth(1.5);

        Text monogram = new Text("A");
        monogram.setFont(new Font("Serif", 40));
        monogram.setFill(Color.web("#fffbeb")); // Cream white
        monogram.setStroke(Color.web("#78350f"));
        monogram.setStrokeWidth(1);

        monogram.setX(-monogram.getLayoutBounds().getWidth() / 2);
        monogram.setY(monogram.getLayoutBounds().getHeight() / 4);

        emblemGroup.getChildren().addAll(badgeShape, coreOrb, monogram);

        emblemGroup.setTranslateX(0);
        emblemGroup.setTranslateY(0);

        Slider rotSlider = new Slider(0, 360, 0);
        rotSlider.setShowTickLabels(true);
        rotSlider.setShowTickMarks(true);
        rotSlider.setMajorTickUnit(45);

        Slider scaleSlider = new Slider(0.5, 2.0, 1.0);
        scaleSlider.setShowTickLabels(true);
        scaleSlider.setShowTickMarks(true);
        scaleSlider.setMajorTickUnit(0.5);

        rotSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            emblemGroup.setRotate(newVal.doubleValue());
            if (isPlaying) stopAnimation(); // Pause manual control if animating
        });

        scaleSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            emblemGroup.setScaleX(newVal.doubleValue());
            emblemGroup.setScaleY(newVal.doubleValue());
            if (isPlaying) stopAnimation();
        });
        Button playPauseBtn = new Button("▶ Play Animation");
        rotateTrans = new RotateTransition(Duration.seconds(2), emblemGroup);
        rotateTrans.setByAngle(360);
        rotateTrans.setCycleCount(Transition.INDEFINITE);
        rotateTrans.setAutoReverse(false);

        scaleTrans = new ScaleTransition(Duration.seconds(1.5), emblemGroup);
        scaleTrans.setByX(0.2);
        scaleTrans.setByY(0.2);
        scaleTrans.setCycleCount(Transition.INDEFINITE);
        scaleTrans.setAutoReverse(true); // Pulse effect

        playPauseBtn.setOnAction(e -> {
            if (isPlaying) {
                stopAnimation();
                playPauseBtn.setText("▶ Play Animation");
            } else {
                startAnimation();
                playPauseBtn.setText("⏸ Pause Animation");
            }
        });
        Label rotLabel = new Label("Rotation:");
        Label scaleLabel = new Label("Scale:");

        HBox controlsRow = new HBox(20,
                new VBox(5, rotLabel, rotSlider),
                new VBox(5, scaleLabel, scaleSlider),
                playPauseBtn
        );
        controlsRow.setAlignment(Pos.CENTER);
        controlsRow.setPadding(new Insets(20));

        VBox root = new VBox(20, header, emblemGroup, controlsRow);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #0f172a; -fx-padding: 20;");

        Scene scene = new Scene(root, 600, 500);

        stage.setTitle("Aptech Vector Shape Studio");
        stage.setScene(scene);
        stage.show();
    }
    private Polygon createStar(int points, double outerRadius, double innerRadius) {
        Polygon star = new Polygon();
        double angleStep = Math.PI / points;

        for (int i = 0; i < points * 2; i++) {
            double r = (i % 2 == 0) ? outerRadius : innerRadius;
            double angle = i * angleStep - Math.PI / 2;

            double x = Math.cos(angle) * r;
            double y = Math.sin(angle) * r;

            star.getPoints().addAll(x, y);
        }
        return star;
    }

    private void startAnimation() {
        rotateTrans.play();
        scaleTrans.play();
        isPlaying = true;
    }

    private void stopAnimation() {
        rotateTrans.stop();
        scaleTrans.stop();
        isPlaying = false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
