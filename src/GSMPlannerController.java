//Класс обеспечения взаимодействия "модели" и "вида" приложения

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by Gerz on 07.06.2015.
 */
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
            }
        }
    }
}
