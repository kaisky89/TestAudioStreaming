/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudiostreaming;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaErrorEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;

/**
 *
 * @author kaisky89
 */
public class PlayerController implements Initializable {
    
    @FXML private Text status;
    @FXML private AnchorPane anchorPane;
    @FXML private TextField field_url;
    
    private MediaView mediaView;
    
    @FXML
    private void handleButtonLaden(ActionEvent event) {
        // TODO: clean up current players
        // ??
        // create new player from given url
        mediaView = buildMediaView(field_url.getText());
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
    private void handleButtonZeigeStatus(ActionEvent event) {
        status.setText(mediaView.getMediaPlayer().getStatus().toString());
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
}
