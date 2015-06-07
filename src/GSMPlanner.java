/**
 * Created by Gerz on 07.06.2015.
 */
public class GSMPlanner {
    public static void main(String[] args) {
        GSMPlannerView plannerView = new GSMPlannerView();
        GSMPlannerModel plannerModel = new GSMPlannerModel();
        GSMPlannerController plannerController = new GSMPlannerController(plannerView, plannerModel);

        plannerView.setVisible(true);
    }
}
