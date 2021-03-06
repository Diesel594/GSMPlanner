//
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GSMPlannerView extends JFrame{
    private JLabel lblBrowse = new JLabel("Файл с координатами: ");
    //TODO: Убрать путь до файла с данными
    private JTextField txtBrowse = new JTextField("C:\\Users\\Gerz\\IdeaProjects\\GSMPlanner\\data.csv", 1000);
    private JButton btnBrowse = new JButton("Обзор...");
    private JButton btnCalculate = new JButton("Расчитать");
    private JTable tblResult = new JTable();
    private JPanel gsmPlannerPanel = new JPanel(new GridBagLayout());
    private JTextArea txtResult = new JTextArea();
    private JScrollPane scrlResult = new JScrollPane(txtResult);

    public GSMPlannerView() {
        //Размещаем объекты управления
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx=0.0;
        this.setLayout(new GridBagLayout());
        this.setSize(500, 400);

        gsmPlannerPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.insets = new Insets(10,5,0,0);
        constraints.ipadx = 0;
        constraints.ipady = 0;

        constraints.gridx = 0;
        constraints.gridy = 0;
        gsmPlannerPanel.add(lblBrowse, constraints);

        txtBrowse.setMinimumSize(new Dimension(250, txtBrowse.getPreferredSize().height));
        constraints.gridx = 1;
        constraints.gridy = 0;
        gsmPlannerPanel.add(txtBrowse, constraints);

        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.gridx = GridBagConstraints.RELATIVE;
        gsmPlannerPanel.add(btnBrowse, constraints);

        constraints.gridx = GridBagConstraints.RELATIVE;
        constraints.gridy = 2;
        constraints.gridwidth = 3;
        constraints.anchor = GridBagConstraints.CENTER;
        gsmPlannerPanel.add(btnCalculate, constraints);

        constraints.gridx = GridBagConstraints.RELATIVE;
        constraints.gridy = 3;
        constraints.gridwidth = 3;
        constraints.gridheight = GridBagConstraints.RELATIVE;
        constraints.insets = new Insets(15,5,0,0);
        constraints.anchor = GridBagConstraints.NORTH;
        scrlResult.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        gsmPlannerPanel.add(txtResult, constraints);



        this.add(gsmPlannerPanel,gbc);
    }

    public String getFileName() {
        return this.txtBrowse.getText();
    }

    public void setTxtBrowseText (String txtBrowseText) {
        txtBrowse.setText(txtBrowseText);
    }

    public void addPlannerViewListener(ActionListener actionListener) {
        btnBrowse.addActionListener(actionListener);
        btnCalculate.addActionListener(actionListener);
    }

    public void displayErrorMessage(String message){
        JOptionPane.showMessageDialog(this, message);
    }

    public void showResult(WorkMap workMap) {
        txtResult.append("Перечень зданий: \n");
        for (House house : workMap.getHouses()) {
            txtResult.append(String.valueOf(house.getLatitude()));
            txtResult.append(", ");
            txtResult.append(String.valueOf(house.getLongitude()));
            txtResult.append(", ");
            txtResult.append(String.valueOf(house.getPopulation()));
            txtResult.append(";\n");
        }
        txtResult.append("\tИтого зданий: "+ String.valueOf(workMap.getHouses().size() + "\n\n"));

        txtResult.append("Перечень секторов: \n");
        for (Sector sector : workMap.getSectors()){
            txtResult.append("Сектор: ");
            txtResult.append(String.valueOf(sector.getLatitude()));
            txtResult.append(", ");
            txtResult.append(String.valueOf(sector.getLongitude()));
            txtResult.append("\n");
        }
        txtResult.append("\tИтого секторов: "+ String.valueOf(workMap.getSectors().size() + "\n\n"));

        txtResult.append("Перечень станций связи: \n");
        for (ConnectionStation connectionStation : workMap.getConnectionStations()){
            txtResult.append("Станция связи: ");
            txtResult.append(String.valueOf(connectionStation.getLatitude()));
            txtResult.append(", ");
            txtResult.append(String.valueOf(connectionStation.getLongitude()));
            txtResult.append("\n");
        }
        txtResult.append("\tИтого станций связи: "+ String.valueOf(workMap.getConnectionStations()
                .size() + "\n\n"));

        txtResult.append("Перечень сотовых станций: \n");
        for (CellularStation cellularStation: workMap.getCellularStations()){
            txtResult.append("Сотовая станция: ");
            txtResult.append(String.valueOf(cellularStation.getLatitude()));
            txtResult.append(", ");
            txtResult.append(String.valueOf(cellularStation.getLongitude()));
            txtResult.append("\n");
        }
        txtResult.append("\tИтого сотовых станций: "+ String.valueOf(workMap.getCellularStations().size() + "\n\n"));

        txtResult.append("Перечень базовых станций: \n");
        for (BaseStation baseStation: workMap.getBaseStations()){
            txtResult.append("Базовая станция: ");
            txtResult.append(String.valueOf(baseStation.getLatitude()));
            txtResult.append(", ");
            txtResult.append(String.valueOf(baseStation.getLongitude()));
            txtResult.append("\n");
        }
        txtResult.append("\tИтого базовых станций: "+ String.valueOf(workMap.getBaseStations().size() + "\n\n"));
    }

    public void showInfo(String info) {
        txtResult.append(info);
    }

   /* public void updateTblResult(TblModel tblModel){
        tblResult = new JTable(tblModel);
        System.out.println("test");
    }*/
}
