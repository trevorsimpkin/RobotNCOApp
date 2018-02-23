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
import java.net.HttpURLConnection;
import java.net.URL;
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
    @FXML protected void startDay(ActionEvent event) {
        final String SLMMODEL = "Rion NL-52";
        final String SLMSERIAL = "01243609";
        final String STARTTIME = "07:00";
        final String ENDTIME = "07:30";
        final String CALIBRATE = "07:27;";
        final String STARTNOTATION = "NCO preps, calibrates, and sets up monitor. ";
        String exePath = "/home/trevorsimpkin/IdeaProjects/RobotNCO/selenium-java-3.9.1/chromedriver";
        System.setProperty("webdriver.chrome.driver", exePath);
        WebDriver driver = new ChromeDriver();
        driver.get(url.getText());
        login(driver, username.getText(), password.getText());
        driver.get(url.getText());

        WebElement slmElement=driver.findElement(By.xpath("//input[@name='soundlevelmeter']"));
        slmElement.clear();
        slmElement.sendKeys(SLMMODEL);
        WebElement slmSerialElement=driver.findElement(By.xpath("//input[@name='serialnumber']"));
        slmSerialElement.sendKeys(SLMSERIAL);
        WebElement startTimeElement=driver.findElement(By.xpath("//input[@name='locationtimeperiodstart0']"));
        startTimeElement.sendKeys(STARTTIME);
        WebElement endTimeElement=driver.findElement(By.xpath("//input[@name='locationtimeperiodend0']"));
        endTimeElement.sendKeys(ENDTIME);
        WebElement projNotationElement = driver.findElement(By.xpath("//textarea[@name='notations0']"));
        projNotationElement.sendKeys(STARTNOTATION);
        WebElement calibrationElement=driver.findElement(By.xpath("//input[@name='calibrationtime']"));
        calibrationElement.sendKeys(CALIBRATE);

    }

    @FXML protected void endDay(ActionEvent event) {
        final String CALIBRATE = " 14:55;";
        final String SUMMARY = "NCO monitored noise levels at 85 Newark Ave related to a front end loader, crane, manual hammer, manlift, generator and hand tools. Acoustic blankets lined the eastern and western perimeters of the work site. No project related exceedances were documented.";
        //weathertemperature, weatherhumidity, weatherwindspeed
        String exePath = "/home/trevorsimpkin/IdeaProjects/RobotNCO/selenium-java-3.9.1/chromedriver";
        System.setProperty("webdriver.chrome.driver", exePath);
        WebDriver driver = new ChromeDriver();
        driver.get(url.getText());
        login(driver, username.getText(), password.getText());
        driver.get(url.getText());
        String [] weather = getWeather();
        WebElement temperatureElement=driver.findElement(By.xpath("//input[@name='weathertemperature']"));
        temperatureElement.sendKeys(weather[0]);
        WebElement humidityElement=driver.findElement(By.xpath("//input[@name='weatherhumidity']"));
        humidityElement.sendKeys(weather[1]);
        WebElement windspeedElement=driver.findElement(By.xpath("//input[@name='weatherwindspeed']"));
        windspeedElement.sendKeys(weather[2]);
        WebElement calibrateElement=driver.findElement(By.xpath("//input[@name='calibrationtime']"));
        calibrateElement.sendKeys(CALIBRATE);
        WebElement summaryElement=driver.findElement(By.xpath("//textarea[@name='summary']"));
        summaryElement.sendKeys(SUMMARY);


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
        BufferedReader br;
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
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return dataToPrint;
    }

    private static String [] getWeather() {
        String [] weather= new String[3];
        String [] tableData = new String[9];
        int maxWind = -1;
        int minWind = 110;
        int maxHumidity = -1;
        int minHmidity = 110;
        int maxTemp = -1;
        int minTemp = 100;
        int temp;

        String endRow = "</tr>";
        try {
            URL ur = new URL("http://w1.weather.gov/data/obhistory/KEWR.html");
            HttpURLConnection yc = (HttpURLConnection) ur.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputline;
            for (int i =0; i < 23; i++) {
                in.readLine();
            }
            inputline= in.readLine();
            int i =0;
            while (!inputline.substring(i, i+5).equals(endRow)) {
                i++;
            }
            tableData[0]=inputline.substring(i+5);
            int j =0;
            while(j < 8 && (inputline= in.readLine()) != null) {
                i =0;
                while (!inputline.substring(i, i+5).equals(endRow)) {
                    i++;
                }
                tableData[j] = tableData[j] + inputline.substring(0,i+5);
                j++;
                tableData[j]=inputline.substring(i+5);

            }
            for (int k =0; k < tableData.length-1; k++) {
                i =0;
                int count = 0;
                while (count < 2) {
                    if (tableData[k].substring(i, i+4).equals("<td>")) {
                        count++;
                    }
                    i++;
                }
                temp = Integer.parseInt(tableData[k].substring(i, i+9).replaceAll("\\D+",""));
                if(temp < minWind) {
                    minWind = temp;
                }
                else if (temp > maxWind) {
                    maxWind = temp;
                }
                i+=7;
                while (count < 5) {
                    if (tableData[k].substring(i, i+4).equals("<td>")) {
                        count++;
                    }
                    i++;
                }
                temp = Integer.parseInt(tableData[k].substring(i, i+9).replaceAll("\\D+",""));
                if(temp < minTemp) {
                    minTemp = temp;
                }
                else if (temp > maxTemp) {
                    maxTemp = temp;
                }
                i+=7;
                while (count < 9) {
                    if (tableData[k].substring(i, i+4).equals("<td>")) {
                        count++;
                    }
                    i++;
                }
                temp = Integer.parseInt(tableData[k].substring(i, i+9).replaceAll("\\D+",""));
                if(temp < minHmidity) {
                    minHmidity = temp;
                }
                else if (temp > maxHumidity) {
                    maxHumidity = temp;
                }
            }
            weather[0] = minTemp+"-"+maxTemp;
            weather[1] = minHmidity + "-" + maxHumidity;
            weather[2] = minWind+"-"+maxWind;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return weather;
    }
}
