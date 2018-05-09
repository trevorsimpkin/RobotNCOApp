package sample;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;

public class FormRobot {
    private final String SLMMODEL = "Rion NL-52";
    private final String SLMSERIAL = "01243609";
    private final String STARTTIME = "07:00";
    private final String ENDTIME = "07:30";
    private final String STARTCALIBRATE = "07:27;";
    private final String STARTNOTATION = "NCO preps, calibrates, and sets up monitor. ";
    private final String ENDCALIBRATE = " 14:55;";
    private final String SUMMARY = "NCO monitored noise levels at 85 Newark Ave related to a front end loader, crane, manual hammer, manlift, generator and hand tools. Acoustic blankets lined the eastern and western perimeters of the work site. No project related exceedances were documented.";
    private String username = "trevor";
    private String password;
    private String url;
    private String exePath = "/home/trevorsimpkin/IdeaProjects/RobotNCO/selenium-java-3.9.1/chromedriver";
    private WebDriver driver;

    public FormRobot () {
        System.setProperty("webdriver.chrome.driver", exePath);
    }

    protected void startDay(String startUrl, String startUsername, String startPassword) {
        driver = new ChromeDriver();
        setUrl(startUrl);
        setUsernamePassword(startUsername, startPassword);
        driver.get(url);
        login();
        driver.get(url);
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
        calibrationElement.sendKeys(STARTCALIBRATE);

    }
    protected void endDay() {
        driver = new ChromeDriver();
        driver.get(url);
        login();
        driver.get(url);
        String[] weather = getWeather();
        WebElement temperatureElement = driver.findElement(By.xpath("//input[@name='weathertemperature']"));
        temperatureElement.sendKeys(weather[0]);
        WebElement humidityElement = driver.findElement(By.xpath("//input[@name='weatherhumidity']"));
        humidityElement.sendKeys(weather[1]);
        WebElement windspeedElement = driver.findElement(By.xpath("//input[@name='weatherwindspeed']"));
        windspeedElement.sendKeys(weather[2]);
        WebElement calibrateElement = driver.findElement(By.xpath("//input[@name='calibrationtime']"));
        calibrateElement.sendKeys(ENDCALIBRATE);
        WebElement summaryElement = driver.findElement(By.xpath("//textarea[@name='summary']"));
        summaryElement.sendKeys(SUMMARY);

    }
        private void setUsernamePassword (String initusername, String initpassword) {
        username = initusername;
        password = initpassword;
    }
    private void setUrl(String initUrl) {
        url = initUrl;
    }
    private void login() {
        WebElement usernameElement=driver.findElement(By.xpath("//input[@name='user_name']"));
        usernameElement.sendKeys(username);
        WebElement passwordElement=driver.findElement(By.xpath("//input[@name='user_password']"));
        passwordElement.sendKeys(password);
        WebElement button=driver.findElement(By.xpath("//input[@name='submit']"));
        button.click();
    }
    protected void fillOutForm(Queue<String> data, String [] times, String [] input, boolean isExceedance) {
        driver = new ChromeDriver();
        driver.get(url);
        login();
        driver.get(url);
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
        int startingID = id;
        while (data.peek()!=null) {
            WebElement siteNumElement=driver.findElement(By.xpath("//input[@name='sitenumber" + id + "']"));
            siteNumElement.sendKeys(input[3]);
            WebElement dropdownLink = driver.findElement(By.cssSelector("#main > div.row > div > form:nth-child(14) > table:nth-child(3) > tbody > tr:nth-child(" + (id +1) + ") > td:nth-child(2) > div > a.current"));
            dropdownLink.click();
            WebElement dropdown= driver.findElement(By.cssSelector("#main > div.row > div > form:nth-child(14) > table:nth-child(3) > tbody > tr:nth-child("+(id+1)+") > td:nth-child(2) > div > ul > li:nth-child("+input[4]+" )"));
            dropdown.click();
            if(input[4].equals("20")) {
                WebElement otherSiteNameElement=driver.findElement(By.xpath("//input[@name='monitoringlocationother" + id + "']"));
                otherSiteNameElement.sendKeys(input[10]);
            }
            WebElement startTimeElement=driver.findElement(By.xpath("//input[@name='locationtimeperiodstart" + id + "']"));
            startTimeElement.sendKeys(data.remove());
            WebElement endTimeElement=driver.findElement(By.xpath("//input[@name='locationtimeperiodend" + id + "']"));
            endTimeElement.sendKeys(data.remove());
            WebElement leqElement=driver.findElement(By.xpath("//input[@name='locationleq" + id + "']"));
            leqElement.sendKeys(data.remove());
            WebElement projNotationElement = driver.findElement(By.xpath("//textarea[@name='notations" + id + "']"));
            projNotationElement.sendKeys(input[5]);
            WebElement nonProjNotationElement = driver.findElement(By.xpath("//textarea[@name='extrafield2" + id + "']"));
            if(!isExceedance && data.peek()!=null && data.peek().equals("non")) {
               nonProjNotationElement.sendKeys("Non-project exceedance due to heavy truck passby on Newark Avenue.");
               data.remove();
            }
            else {
                nonProjNotationElement.sendKeys(input[6]);
            }
            id++;
            System.out.println("------------------------------------------------");
            System.out.println(id);
            System.out.println(startingID);
            if (id-startingID==10) {
                id++;
            }
            if (id % 5 == 0 ) {
                moreLinesLink = driver.findElement(By.linkText("Show more lines..."));
                moreLinesLink.click();
            }
        }
    }
    //Eventually make own weather object
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
                System.out.println(tableData[k].substring(i, i+9));
                if(tableData[k].substring(i, i+9).equals("td>Calm</"))
                {
                    temp = 0;
                }
                else {
                    temp = Integer.parseInt(tableData[k].substring(i, i+11).replaceAll("\\D+",""));
                }


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
    public static Queue<String> readCSVFile(String csvFile, String startTime, int intervals, String [] times, boolean isExceedance) {
        BufferedReader br;
        String line = "";
        int i = 0;
        double min = 200.0;
        double max = 0.0;
        double exceedanceMin = 200.0;
        double exceedanceMax = 85.0;
        double temp;
        boolean startReached = false;
        boolean exceedanceInterval = false;
        //double[] dataToPrint = new double[2 * intervals];
        Queue<String> toPrint = new LinkedList<String>();
        //Queue<Double> exceedances = new LinkedList<Double>();
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while (!startReached&&(line = br.readLine())!=null) {
                String [] lineData = line.split(",");
                startReached = lineData[1].equals(startTime);
            }
            while (line!=null&&i<intervals) {
                String [] data = line.split(",");
                temp = Double.parseDouble(data[5]);
                if (isExceedance && temp > 85.0) {
                    exceedanceMin = exceedanceMax = temp;
                    exceedanceInterval = true;
                }
                else {
                    min = max = temp;
                }
                toPrint.add(data[1].substring(11,16)); //start time
                //times[2*i]=data[1].substring(11,16);
                //times[(2*i)+1]=times[2*i].substring(0,4) + "9";
                for (int j = 1; j < 10; j++) {
                    line = br.readLine();
                    data = line.split(",");
                    temp = Double.parseDouble(data[5]);
                    if(exceedanceInterval&temp<85.0) {
                        min = max = temp;
                        exceedanceInterval = false;
                        toPrint.add(data[1].substring(11,16));
                        if (exceedanceMin==exceedanceMax) {
                            toPrint.add(""+exceedanceMin);
                        }
                        else {
                            toPrint.add("" + exceedanceMin + "-" + exceedanceMax);
                        }
                        toPrint.add(data[1].substring(11,16));
                    }
                    if (isExceedance && temp > 85.0) {
                        if(!exceedanceInterval) {
                            exceedanceMin = exceedanceMax = temp;
                            toPrint.add(data[1].substring(11,16));
                            if(min==max) {
                                toPrint.add("" + min);
                            }
                            else {
                                toPrint.add("" + min + "-" + max);
                            }
                            toPrint.add(data[1].substring(11,16));
                            exceedanceInterval = true;
                        }
                        if (temp < exceedanceMin) {
                            exceedanceMin = temp;
                        }
                        else if (temp > exceedanceMax) {
                            exceedanceMax = temp;
                        }
                    }
                    else if (temp < min) {
                        min = temp;
                    }
                    else if (temp > max) {
                        max = temp;
                    }
                }
                toPrint.add(data[1].substring(11,15) + "9");
                if (exceedanceInterval) {
                    if(exceedanceMin==exceedanceMax) {
                        toPrint.add("" + exceedanceMin);
                    }
                    else {
                        toPrint.add("" + exceedanceMin + "-" + exceedanceMax);
                    }
                }
                else {
                    if(min==max) {
                        toPrint.add("" + min);
                    }
                    else {
                        toPrint.add("" + min + "-" + max);
                    }
                    if (max > 85.0) {
                        toPrint.add("non");
                    }

                }
                i++;
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return toPrint;
    }
}
