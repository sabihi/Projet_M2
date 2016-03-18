package controller;
 
import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Named;
import org.primefaces.model.chart.PieChartModel;
import service.ChartInformations;
 
@Named("ahartView")
@SessionScoped
public class ChartView implements Serializable {
 
    private ChartInformations chartInformations= new ChartInformations();
    private PieChartModel pieModel1;
    private PieChartModel pieModel2;
    
    @PostConstruct
    public void init() {
        createPieModels();
    }
 
    public PieChartModel getPieModel1() {
        return pieModel1;
    }
     
    public PieChartModel getPieModel2() {
        return pieModel2;
    }
     
    private void createPieModels() {
        createPieModel1();
    }
 
    private void createPieModel1() {
        pieModel1 = new PieChartModel();
        List<Integer> list=getnbrServices();
        
         
        pieModel1.set("Services", list.get(0));
        pieModel1.set("Utilisateurs", list.get(1));
        pieModel1.set("Catégories", list.get(2));
        pieModel1.set("Clusters", list.get(3));
         
        pieModel1.setTitle("Simple Pie");
        pieModel1.setLegendPosition("e");
        pieModel1.setShowDataLabels(true);
        pieModel1.setDiameter(250);

    }
     
    private void createPieModel2() {
        pieModel2 = new PieChartModel();
         
        pieModel2.set("Brand 1", 540);
        pieModel2.set("Brand 2", 325);
        pieModel2.set("Brand 3", 702);
        pieModel2.set("Brand 4", 421);
         
        pieModel2.setTitle("Custom Pie");
        pieModel2.setLegendPosition("e");
        pieModel2.setFill(false);
        pieModel2.setShowDataLabels(true);
        pieModel2.setDiameter(150);
    }
     
    
        public List<Integer> getnbrServices() {
            return chartInformations.getnbrServices();
        }

}