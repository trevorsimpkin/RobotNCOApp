package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Queue;

public class Controller {
    @FXML private TextField url;
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private TextField siteNumber;
    @FXML private ComboBox siteName;
    @FXML private TextArea projectNote;
    @FXML private TextArea nonProjectNote;
    @FXML private Text filename;
    @FXML private TextField startTime;
    @FXML private TextField intervals;
    @FXML private TextField otherSiteName;
    @FXML private CheckBox otherSite;
    @FXML private CheckBox isExceedance;
    private FormRobot robot = new FormRobot();


    @FXML protected void handleDataFileSearch (ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Data File");
        File selectedFile = fileChooser.showOpenDialog(filename.getScene().getWindow());
        filename.setText(selectedFile.getAbsolutePath());
    }
    @FXML protected void selectOtherSiteName(ActionEvent event){
        if(otherSite.isSelected()) {
            otherSiteName.setVisible(true);
            siteName.setDisable(true);
        }
        else {
            otherSiteName.setVisible(false);
            siteName.setDisable(false);
        }

    }
    @FXML protected void onSubmit (ActionEvent event) {
        System.out.println("Inside onSubmit");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate now = LocalDate.now();
        String [] input = new String [11];
        input[0]=url.getText();
        input[1]=username.getText();
        input[2]=password.getText();
        input[3]=siteNumber.getText();
        if (otherSite.isSelected()){
            input[4]="20";
        }
        else {
            input[4]=siteName.getValue().toString().substring(0,4).replaceAll("\\D+","");
        }

        input[5]=projectNote.getText();
        input[6]=nonProjectNote.getText();
        input[7]=filename.getText();
        input[8]=now.format(dtf)+" "+startTime.getText()+":00";
        input[9]=intervals.getText();
        input[10]=otherSiteName.getText();
        int numIntervals = Integer.parseInt(input[9]);
        String [] times = new String [numIntervals*2];
        Queue<String> data = robot.readCSVFile(input[7],input[8],numIntervals, times, isExceedance.isSelected());
        robot.fillOutForm(data, times, input, isExceedance.isSelected());

    }
    @FXML protected void startDay(ActionEvent event) {

        robot.startDay(url.getText(),username.getText(),password.getText());
    }

    @FXML protected void endDay(ActionEvent event) {
        //eventually add parameters so summary is correct.
        robot.endDay();
    }
}
