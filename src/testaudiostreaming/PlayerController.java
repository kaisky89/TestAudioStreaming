/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudiostreaming;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaErrorEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 *
 * @author kaisky89
 */
public class PlayerController implements Initializable {
    
    @FXML private Text status;
    @FXML private Text properties;
    @FXML private AnchorPane anchorPane;
    @FXML private TextField field_url;
    @FXML private Slider slider;
    @FXML private AreaChart chart;
    
    private MediaView mediaView;
    
    
    @FXML
    private void handleButtonLeft(ActionEvent event){
        Duration currentDuration = mediaView.getMediaPlayer().getCurrentTime();
        //Duration newDuration = currentDuration.add(Duration.seconds(-10.0));
        Duration newDuration = new Duration(-1000.0);
        mediaView.getMediaPlayer().seek(newDuration);
    }
    
    @FXML
    private void handleButtonRight(ActionEvent event){
        Duration currentDuration = mediaView.getMediaPlayer().getCurrentTime();
        Duration newDuration = currentDuration.add(Duration.seconds(10.0));
        mediaView.getMediaPlayer().seek(newDuration);
    }
    
    @FXML
    private void handleButtonProperties(ActionEvent event){
        Object object = mediaView.getMediaPlayer().getMedia().durationProperty();
        
        String string = "media.keySet: " + (object == null ? "null" : object.toString());
        System.out.println(string);
        properties.setText(string);
    }
    
    @FXML
    private void handleButtonLaden(ActionEvent event) {
        // clean up current players
        if(mediaView != null){
            mediaView.getMediaPlayer().stop();
            mediaView.setMediaPlayer(null);
            mediaView = null;
        }
        
        
        // create new player from given url
        mediaView = buildMediaView(field_url.getText());
        
        
        // set all statusChanges to given method
        setAllStatusChanges(mediaView.getMediaPlayer(), new Runnable() {
            @Override
            public void run() {
                reloadStatus();
            }
        });
        
        
        // add Eventlistener for changing CurrentTime
        mediaView.getMediaPlayer().currentTimeProperty().addListener(new ChangeListener<Duration>() {
            
            @Override
            public void changed(
                    ObservableValue<? extends Duration> observableValue, 
                    Duration duration, 
                    Duration current) {
                slider.setValue(current.toSeconds());
                System.out.println("changed:"
                        + "\n(  observableValue:    " + observableValue
                        + "\n   duration:           " + duration
                        + "\n   current:            " + current
                        + "\n)");
            }
        });
        
        
        // add player to scenegraph
        anchorPane.getChildren().add(mediaView);
    }
    
    @FXML
    private void handleButtonAbspielen(ActionEvent event) {
        mediaView.getMediaPlayer().play();
    }
    
    @FXML
    private void handleButtonStop(ActionEvent event) {
        mediaView.getMediaPlayer().stop();
    }
    
    @FXML
    private void handleButtonShowStatus(ActionEvent event) {
        reloadStatus();
    }
    
    @FXML
    private void handleButtonPause(ActionEvent event) {
        mediaView.getMediaPlayer().pause();
    }
    
    private void setAllStatusChanges(MediaPlayer mediaPlayer, Runnable r){
        mediaPlayer.setOnEndOfMedia(r);
        mediaPlayer.setOnError(r);
        mediaPlayer.setOnHalted(r);
        mediaPlayer.setOnPaused(r);
        mediaPlayer.setOnPlaying(r);
        mediaPlayer.setOnReady(r);
        mediaPlayer.setOnRepeat(r);
        mediaPlayer.setOnStalled(r);
        mediaPlayer.setOnStopped(r);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    private MediaView buildMediaView(String url){
        
        final Media media;
        final MediaPlayer mediaPlayer;
        MediaView tmpMediaView = null;
        try {
            media = new Media(url);
            if (media.getError() == null) {
                media.setOnError(new Runnable() {
                    @Override
                    public void run() {
                        // Handle asynchronous error in Media object.
                        System.err.println(media.getError().toString());
                    }
                });
                try {
                    mediaPlayer = new MediaPlayer(media);
                    if (mediaPlayer.getError() == null) {
                        mediaPlayer.setOnError(new Runnable() {
                            @Override
                            public void run() {
                                // Handle asynchronous error in MediaPlayer object.
                                System.err.println(mediaPlayer.getError().toString());
                            }
                        });
                        tmpMediaView = new MediaView(mediaPlayer);
                        tmpMediaView.setOnError(new EventHandler() {
                            public void handle(MediaErrorEvent t) {
                                // Handle asynchronous error in MediaView.
                                System.err.println(t.toString());
                            }
                            
                            @Override
                            public void handle(Event t) {
                                System.err.println(t.toString());
                                System.err.println(t.getEventType());
                            }
                        });
                    } else {
                        // Handle synchronous error creating MediaPlayer.
                        System.err.println(mediaPlayer.getError().toString());
                    }
                } catch (Exception mediaPlayerException) {
                    // Handle exception in MediaPlayer constructor.
                    System.err.println(mediaPlayerException.toString());
                }
            } else {
                // Handle synchronous error creating Media.
                System.err.println(media.getError().toString());
            }
        } catch (Exception mediaException) {
            // Handle exception in Media constructor.
            System.err.println(mediaException.toString());
        }
        
        return tmpMediaView;
    }

    private void reloadStatus() {
        status.setText(mediaView.getMediaPlayer().getStatus().toString());
    }
}
