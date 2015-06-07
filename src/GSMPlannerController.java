//Класс обеспечения взаимодействия "модели" и "вида" приложения

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GSMPlannerController {
    private GSMPlannerView plannerView;
    private GSMPlannerModel plannerModel;
    private List<House> houseList = new ArrayList<>();

    public GSMPlannerController(GSMPlannerView plannerView, GSMPlannerModel plannerModel){
        this.plannerView = plannerView;
        this.plannerModel = plannerModel;

        this.plannerView.addPlannerViewListener(new GSMViewListener());
    }

    class GSMViewListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object actionSource = e.getSource();
            if (actionSource instanceof JButton) {
                JButton button = (JButton)actionSource;
                if (button.getText().equals("Обзор...")) {
                    JFileChooser fileChooser = new JFileChooser();
                    int ret = fileChooser.showDialog(null, "Открыть файл");
                    if (ret == JFileChooser.APPROVE_OPTION){
                        File coordinatesFile =  fileChooser.getSelectedFile();
                        plannerView.setTxtBrowseText(coordinatesFile.getAbsolutePath());
                    }
                }
                if (button.getText().equals("Расчитать")) {
                    houseList = parseDataFile(plannerView.getFileName());
                }
            }
        }
    }

    private List<House> parseDataFile(String filePath) {
        List<House> houseList = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String dataLine;
            while ((dataLine = bufferedReader.readLine())!= null) {
                //Попытаться распарсить данные
                if (dataLine.contains(" ")) {
                    String[] dataLineParts = dataLine.split(" ");
                    try {
                        double latitude = Double.parseDouble(dataLineParts[0]);
                        double longitude = Double.parseDouble(dataLineParts[1]);
                        int population = Integer.parseInt(dataLineParts[2]);
                        House newHouse = new House(latitude, longitude, population);
                        houseList.add(newHouse);
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return houseList;
    }
}
