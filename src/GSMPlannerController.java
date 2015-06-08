//Класс обеспечения взаимодействия "модели" и "вида" приложения

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
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
                    //Получение списка домов из файла данных
                    houseList = plannerModel.parseDataFile(plannerView.getFileName());
                    //Добавление домов в карту
                    plannerModel.fillUpMapWithHouses(houseList);
                    //Вывод координат в таблицу

                }
            }
        }
    }


}
