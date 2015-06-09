//Класс обеспечения взаимодействия "модели" и "вида" приложения

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GSMPlannerController {
    private GSMPlannerView plannerView;
    private GSMPlannerModel plannerModel;

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
                    if (!plannerView.getFileName().isEmpty()) {
                        //Получение списка домов из файла данных
                        List<House> houseList = plannerModel.parseDataFile(plannerView.getFileName());
                        //Добавление домов в карту
                        plannerModel.fillUpMapWithHouses(houseList);
                    }
                    //Вывод координат
                    // TblModel filledModel = plannerModel.fillUpTable();
                    //plannerView.updateTblResult(filledModel);
                    plannerView.showResult(plannerModel.getWorkMap());
                    //TODO:убрать тестирование getDistance
                    //Тестирование getDistance
                    if (plannerModel.getWorkMap().getHouses().size() > 1)
                        plannerView.showInfo(String.valueOf(plannerModel.getDistance(
                                plannerModel.getWorkMap().getHouses().get(0).getPosX(),
                                plannerModel.getWorkMap().getHouses().get(0).getPosY(),
                                plannerModel.getWorkMap().getHouses().get(1).getPosX(),
                                plannerModel.getWorkMap().getHouses().get(1).getPosY())));
                }
            }
        }
    }

    //TODO: расчет координат
    //x = R * cos(lat) * cos(lon)
    //y = R * cos(lat) * sin(lon)
    // z = R *sin(lat)
}
