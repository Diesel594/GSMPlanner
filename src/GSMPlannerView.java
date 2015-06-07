//
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GSMPlannerView extends JFrame{
    private JLabel lblBrowse = new JLabel("Файл с координатами: ");
    private JTextField txtBrowse = new JTextField("", 1000);
    private JButton btnBrowse = new JButton("Обзор...");
    private JButton btnCalculate = new JButton("Расчитать");
    private JTable tblResult = new JTable();

    public GSMPlannerView() {
        //Размещаем объекты управления
        JPanel gsmPlannerPanel = new JPanel(new GridBagLayout());
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx=0.0;
        this.setLayout(new GridBagLayout());
        this.setSize(500,400);

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

        this.add(gsmPlannerPanel,gbc);
    }

    public String getFileName() {
        return this.txtBrowse.getText();
    }

    public void setTxtBrowseText (String txtBrowseText) {
        txtBrowse.setText(txtBrowseText);
    }

    void addPlannerViewListener(ActionListener actionListener) {
        btnBrowse.addActionListener(actionListener);
    }

    void displayErrorMessage(String meessage){
        JOptionPane.showMessageDialog(this, meessage);
    }
}
