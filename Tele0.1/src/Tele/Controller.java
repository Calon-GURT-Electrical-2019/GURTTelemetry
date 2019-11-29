package Tele;

import com.fazecast.jSerialComm.SerialPort;
import eu.hansolo.medusa.Gauge;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Ellipse;
import javafx.stage.Stage;

import java.util.Scanner;

public class Controller {

    private SerialPort portPilih;

    @FXML
    ComboBox portList;

    @FXML
    Ellipse closeApps;

    @FXML
    Gauge speed, durasiInjeksi, waktu, energi, jarak, konsumsi;

    @FXML
    LineChart ketinggian;

    @FXML
    NumberAxis xAxis, yAxis;

    String[] input;

    public  void initialize(){
        initGauge();
        grafik();
        //Menambah item di combobox
        SerialPort[] listPort = SerialPort.getCommPorts();
        for(int i = 0; i < listPort.length; i++)
            portList.getItems().add(listPort[i].getSystemPortName());

    }

    public void connectPort(MouseEvent event) {
        System.out.println("Coneecting to " + portList.getValue().toString());
        portPilih = SerialPort.getCommPort(portList.getValue().toString());
        portPilih.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);

        if (portPilih.openPort()) {
            portList.setDisable(true);
            System.out.println("Connected to port " + portPilih.getDescriptivePortName());
        }
        else
            speed.setValue(100);
        getValue();
    }

    public void getValue(){
        Thread getValueThread = new Thread() {
            @Override
            public void run(){
                Scanner getScannedValue = new Scanner(portPilih.getInputStream());
                while (getScannedValue.hasNextLine()){
                    try {
                        input = getScannedValue.nextLine().split(" ");
                        System.out.println(input[0]);
                        speed.setValue(Double.parseDouble(input[0]));
                        durasiInjeksi.setValue(Double.parseDouble(input[1]));
                        jarak.setValue(Double.parseDouble(input[2]));
                        energi.setValue(Double.parseDouble(input[3]));
                        waktu.setValue(Double.parseDouble(input[4]));

                    } catch (Exception e){}
                }
                portPilih.closePort();
            }
        };
        getValueThread.start();
    }

    public void grafik(){
        yAxis.autoRangingProperty().setValue(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(100);
        yAxis.setTickUnit(20);

        XYChart.Series series = new XYChart.Series();
        series.getData().add(new XYChart.Data<Number, Number>(1, 2));
        series.getData().add(new XYChart.Data<Number, Number>(20, 40));
        series.getData().add(new XYChart.Data<Number, Number>(50, 20));
    }

    public void initGauge(){
        speed.setUnit("kmph");
        speed.setTitle("Kecepatan");
        durasiInjeksi.setUnit("ms");
        durasiInjeksi.setTitle("Durasi Injeksi");
    }

    public void closeApp(javafx.scene.input.MouseEvent event) {
        Stage s = (Stage) ((Node)event.getSource()).getScene().getWindow();
        s.close();
        System.exit(0);
    }

    public void minimizedApp(MouseEvent event) {
        Stage s = (Stage) ((Node)event.getSource()).getScene().getWindow();
        s.setIconified(true);
    }

    public void settingPort(MouseEvent event) {
        System.out.println("Disconnecting");
        portPilih.closePort();
        portList.setDisable(false);
        System.out.println("Diconnected");
    }


}
