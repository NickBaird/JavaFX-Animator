package controllers;

import org.apache.commons.math3.distribution.NormalDistribution;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.scene.text.Text;

public class MainController {
	
	@FXML
	private AnchorPane background, viewportBackground;
	
	@FXML
	private ScrollPane viewportBackgroundScroll, viewportSliderScroll;
	
	@FXML
	private Slider viewportSlider;
	
	@FXML
	private Rectangle viewport;
		
	
	public static double VIEWPORT_WIDTH = 800, VIEWPORT_HEIGHT = 400;
	Scale viewportScale;
	Translate viewportTranslate;
	public static boolean PANNING = false;
	public static boolean SCROLLING = false;
	
	Group viewportGroup = new Group();
	
	public static double SCALE_DELTA = 1.2;
	public static double MIN_SCALE = 0.2, MAX_SCALE = 5.0;	
	
	public void initialize() {
		
		
		viewportScale = new Scale(1.0, 1.0, 0, 0);
		viewportTranslate = new Translate(0.0, 0.0);
		viewportGroup.setStyle("-fx-background: red");
		
		AnchorPane groupPlaceholder = new AnchorPane();
		groupPlaceholder.setMinWidth(100000);
		groupPlaceholder.setMinHeight(100000);
		groupPlaceholder.setTranslateX(-50000);
		groupPlaceholder.setTranslateY(-50000);
		
		
		Platform.runLater(() -> {
			viewportSliderScroll.setHvalue(viewportBackgroundScroll.getHvalue());
			viewportSliderScroll.hvalueProperty().bind(viewportBackgroundScroll.hvalueProperty());
			
			centerViewport();
			//centerViewportGroup();
			viewportGroup.getChildren().addAll(groupPlaceholder, viewport);
			viewportGroup.getTransforms().addAll(viewportScale, viewportTranslate);
			viewportBackground.getChildren().addAll(viewportGroup);
			viewportScale.setPivotX(viewportBackground.getWidth() / 2);
			viewportScale.setPivotY(viewportBackground.getHeight() / 2);
			
			
			
			//viewport.getTransforms().add(viewportScale);
			
			
			viewportBackgroundScroll.getScene().setOnKeyPressed(event -> {
				switch(event.getCode()) {
					case KeyCode.CONTROL:
						PANNING = true;
						viewportBackgroundScroll.setPannable(true);
						viewportBackgroundScroll.setCursor(Cursor.MOVE);
						break;
					default: break;
				}
			});
			
			viewportBackgroundScroll.getScene().setOnKeyReleased(event -> {
				PANNING = false;
				viewportBackgroundScroll.setPannable(false);
				viewportBackgroundScroll.setCursor(Cursor.DEFAULT);
			});
			
			viewportBackgroundScroll.setOnMouseClicked(event -> {
				
				System.out.println("CLICKED");
				
				System.out.println(event);
				System.out.println(event.getPickResult());
				System.out.println(event.getTarget());
				
				if (event.getPickResult().getIntersectedNode().getId() != null && event.getPickResult().getIntersectedNode().getId().equals("viewportBackground")) {
					Point3D point = event.getPickResult().getIntersectedPoint();
					Rectangle rect = new Rectangle(point.getX() - 50050, point.getY() - 50050, 100, 100);
					viewportGroup.getChildren().add(rect);
					//rect.getTransforms().add(viewportScale);
				} else {
//					Point3D point = event.getPickResult().getIntersectedPoint();
//					Bounds bounds = event.getPickResult().getIntersectedNode().getBoundsInParent();
//					viewportBackground.getChildren().add(new Rectangle(bounds.getMinX() + event.getX() - 50, bounds.getMinY() + event.getY() - 50, 100, 100));
					Point3D point = event.getPickResult().getIntersectedPoint();
					Rectangle rect = new Rectangle(point.getX() - 50050, point.getY() - 50050, 100, 100);
					rect.setDisable(true);
					viewportGroup.getChildren().add(rect);
				}
			});
		
			//viewportSlider.valueProperty().bind(viewportBackgroundScroll.hvalueProperty().multiply(10000));
			
		});
		
		viewportBackgroundScroll.addEventFilter(ScrollEvent.ANY, event -> {
		    if (event.isControlDown()) {
		        event.consume();
				zoomViewport(event);		
		    }
		});
		
		
		
		

		
		
//		viewportBackground.setOnScroll(event -> {
//			double zoomFactor = (event.getDeltaY() > 0) ? 1.1 : 0.9;
//			scale.setX(scale.getX() * zoomFactor);
//			scale.setY(scale.getY() * zoomFactor);
//		});
		
		
	}
	
	public void centerViewport() {
		
		double centerX = viewportBackground.getWidth() / 2;
		double centerY = viewportBackground.getHeight() / 2;
		
		Bounds bounds = viewport.getBoundsInParent();
		
		double transX = centerX - bounds.getCenterX();
		double transY = centerY - bounds.getCenterY();
		
		viewport.setTranslateX(transX);
		viewport.setTranslateY(transY);
		
		//AnchorPane.setTopAnchor(viewport, (double) transX);
		//AnchorPane.setLeftAnchor(viewport, (double) transY);
		
		//viewportTranslate.setX(viewportTranslate.getX() + transX);
		//viewportTranslate.setY(viewportTranslate.getY() + transY);
		
		viewportBackgroundScroll.setHvalue(0.5);
		viewportBackgroundScroll.setVvalue(0.5);
	}
	
	public void centerViewportGroup() {
		double centerX = viewportBackground.getWidth() / 2;
		double centerY = viewportBackground.getHeight() / 2;
		
		Bounds bounds = viewportGroup.getBoundsInParent();
		
		double transX = centerX - bounds.getCenterX();
		double transY = centerY - bounds.getCenterY();
		
		viewportTranslate.setX(viewportTranslate.getX() + transX);
		viewportTranslate.setY(viewportTranslate.getY() + transY);
		
		viewportBackgroundScroll.setHvalue(0.5);
		viewportBackgroundScroll.setVvalue(0.5);
	}
	
	public void zoomViewport(ScrollEvent event) {
		double x = event.getX();
		double y = event.getY();
		double dy = event.getDeltaY();
		
		double scaleX = viewportScale.getX();
		double scaleY = viewportScale.getY();
		
		if (dy < 0) {
			scaleX /= SCALE_DELTA;
			scaleY /= SCALE_DELTA;
		} else {
			scaleX *= SCALE_DELTA;
			scaleY *= SCALE_DELTA;
		}
		
		scaleX = Math.clamp(scaleX, MIN_SCALE, MAX_SCALE);
		scaleY = Math.clamp(scaleY, MIN_SCALE, MAX_SCALE);
		
	    viewportScale.setX(scaleX);
	    viewportScale.setY(scaleY);
	    
	    System.out.println(viewportGroup.getBoundsInParent());
	    
	    // Gaussian Scrolling -- Maybe remove?
//		double centerX = viewportBackgroundScroll.getWidth() / 2;
//		double centerY = viewportBackgroundScroll.getHeight() / 2;
//		
//		NormalDistribution nx = new NormalDistribution(centerX, viewportBackgroundScroll.getWidth() / 4);
//		NormalDistribution ny = new NormalDistribution(centerY, viewportBackgroundScroll.getHeight() / 4);
//	
//		double maxX = nx.density(centerX);
//		double maxY = ny.density(centerY);
//
//		double px = nx.density(x) / maxX;
//		double py = ny.density(y) / maxY;
//		
//		viewportTranslate.setX(viewportTranslate.getX() + ((centerX - x) / 8));
//		viewportTranslate.setY(viewportTranslate.getY() + ((centerY - y) / 8));
	}
}


/**
 * Mouse drag context used for scene and nodes.
 */
class DragContext {
	
	Node node;

    double mouseAnchorX;
    double mouseAnchorY;

    double translateAnchorX;
    double translateAnchorY;
    
    public DragContext(Node node, double mouseAnchorX, double mouseAnchorY, double translateAnchorX, double translateAnchorY) {
    	this.node = node;
    	this.mouseAnchorX = mouseAnchorX;
    	this.mouseAnchorY = mouseAnchorY;
    	this.translateAnchorX = translateAnchorX;
    	this.translateAnchorY = translateAnchorY;
    }
}