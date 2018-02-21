package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Controller {
    @FXML private TextField url;
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private TextField siteNumber;
    @FXML private TextField siteName;
    @FXML private TextArea projectNote;
    @FXML private TextArea nonProjectNote;
    @FXML private Text filename;
    @FXML private TextField startTime;
    @FXML private TextField intervals;


    @FXML protected void handleDataFileSearch (ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Data File");
        File selectedFile = fileChooser.showOpenDialog(filename.getScene().getWindow());
        filename.setText(selectedFile.getAbsolutePath());
    }
    @FXML protected void onSubmit (ActionEvent event) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate now = LocalDate.now();
        String [] input = new String [10];
        input[0]=url.getText();
        input[1]=username.getText();
        input[2]=password.getText();
        input[3]=siteNumber.getText();
        input[4]=siteName.getText();
        input[5]=projectNote.getText();
        input[6]=nonProjectNote.getText();
        input[7]=filename.getText();
        input[8]=now.format(dtf)+" "+startTime.getText()+":00";
        input[9]=intervals.getText();
        int numIntervals = Integer.parseInt(input[9]);
        String [] times = new String [numIntervals*2];
        double [] data = readCSVFile(input[7],input[8],numIntervals, times);
        /*for (int i = 0; i < data.length; i++) {
            System.out.println(times[i]);
            System.out.println(data[i]);
        }*/
        fillOutForm(data, times, input);

    }
    private static void fillOutForm(double [] data, String [] times, String [] input) {
        String exePath = "/home/trevorsimpkin/IdeaProjects/RobotNCO/selenium-java-3.9.1/chromedriver";
        System.setProperty("webdriver.chrome.driver", exePath);
        WebDriver driver = new ChromeDriver();
        driver.get(input[0]);
        login(driver, input[1], input[2]);
        driver.get(input[0]);
        WebElement moreLinesLink = driver.findElement(By.linkText("Show more lines..."));
        moreLinesLink.click();
        //moreLinesLink.click(); //extra lines -- could input if statements but only if performance suffers.
        int id = 1;
        WebElement element=driver.findElement(By.xpath("//input[@name='sitenumber" + id + "']"));
        String value = element.getAttribute("value");
        while (!value.equals("")) {
            id++;
            element=driver.findElement(By.xpath("//input[@name='sitenumber" + id + "']"));
            value = element.getAttribute("value");
        }
        for (int j = 0; j <data.length; j+=2) {
            WebElement siteNumElement=driver.findElement(By.xpath("//input[@name='sitenumber" + id + "']"));
            siteNumElement.sendKeys(input[3]);
            WebElement dropdownLink = driver.findElement(By.cssSelector("#main > div.row > div > form:nth-child(14) > table:nth-child(3) > tbody > tr:nth-child(" + (id +1) + ") > td:nth-child(2) > div > a.current"));
            dropdownLink.click();
            WebElement dropdown= driver.findElement(By.cssSelector("#main > div.row > div > form:nth-child(14) > table:nth-child(3) > tbody > tr:nth-child("+(id+1)+") > td:nth-child(2) > div > ul > li:nth-child(19)"));
            dropdown.click();
            WebElement startTimeElement=driver.findElement(By.xpath("//input[@name='locationtimeperiodstart" + id + "']"));
            startTimeElement.sendKeys(times[j]);
            WebElement endTimeElement=driver.findElement(By.xpath("//input[@name='locationtimeperiodend" + id + "']"));
            endTimeElement.sendKeys(times[j+1]);
            WebElement leqElement=driver.findElement(By.xpath("//input[@name='locationleq" + id + "']"));
            leqElement.sendKeys(""+data[j]+"-"+data[j+1]);
            WebElement projNotationElement = driver.findElement(By.xpath("//textarea[@name='notations" + id + "']"));
            projNotationElement.sendKeys(input[5]);
            WebElement nonProjNotationElement = driver.findElement(By.xpath("//textarea[@name='extrafield2" + id + "']"));
            nonProjNotationElement.sendKeys(input[6]);
            id++;

        }
    }
    private static void login(WebDriver driver, String username, String password) {
        WebElement usernameElement=driver.findElement(By.xpath("//input[@name='user_name']"));
        usernameElement.sendKeys(username);
        WebElement passwordElement=driver.findElement(By.xpath("//input[@name='user_password']"));
        passwordElement.sendKeys(password);
        WebElement button=driver.findElement(By.xpath("//input[@name='submit']"));
        button.click();
    }
    private static double [] readCSVFile(String csvFile, String startTime, int intervals, String [] times) {
        BufferedReader br = null;
        String line = "";
        int i = 0;
        double min;
        double max;
        double temp;
        boolean startReached = false;
        double [] dataToPrint = new double[2*intervals];
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while (!startReached&&(line = br.readLine())!=null) {
                String [] lineData = line.split(",");
                startReached = lineData[1].equals(startTime);
            }
            while (line!=null&&i<intervals) {
                String [] data = line.split(",");
                min = max = Double.parseDouble(data[5]);
                times[2*i]=data[1].substring(11,16);
                times[(2*i)+1]=times[2*i].substring(0,4) + "9";
                for (int j = 1; j < 10; j++) {
                    line = br.readLine();
                    data = line.split(",");
                    temp = Double.parseDouble(data[5]);
                    if (temp < min) {
                        min = temp;
                    } else if (temp > max) {
                        max = temp;
                    }
                }
                dataToPrint[2*i] = min;
                dataToPrint[(2*i)+1] = max;
                i++;
                line = br.readLine();
            };
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataToPrint;
    }
}
