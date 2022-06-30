import java.util.ArrayList;

public class Data {
    private ArrayList<ArrayList<ArrayList<Double>>> costs;
    private ArrayList<ArrayList<ArrayList<String>>> aktionen;
    
    public Data() {
        costs = new ArrayList<ArrayList<ArrayList<Double>>>();
        aktionen = new ArrayList<ArrayList<ArrayList<String>>>();
        for (int i = 0; i <= 240; i++) {
            ArrayList<ArrayList<Double>> row = new ArrayList<ArrayList<Double>>();
            for (int j = 0; j <= 200; j++) {
                ArrayList<Double> col = new ArrayList<Double>();
                for (int k = 0; k <= 100; k++) {
                    col.add(Double.POSITIVE_INFINITY);
                }
                row.add(col);
            }
            costs.add(row);
        }

        for (int i = 0; i <= 200; i++) {
            for(int j = 0; j <= 100; j++) {
                costs.get(240).get(i).set(j, 0.0);
            }
        }

        for (int i = 0; i <= 240; i++) {
            ArrayList<ArrayList<String>> row = new ArrayList<ArrayList<String>>();
            for (int j = 0; j <= 200; j++) {
                ArrayList<String> col = new ArrayList<String>();
                for (int k = 0; k <= 100; k++) {
                    col.add("");
                }
                row.add(col);
            }
            aktionen.add(row);
        }
    }

    public Double getCost(int decision, int g, int n){
        return costs.get(decision).get(g).get(n);
    }

    public void setCost(int decision, int g, int n, Double cost){
        costs.get(decision).get(g).set(n, cost);
    }

    public String getAktion(int decision, int g, int n){
        return aktionen.get(decision).get(g).get(n);
    }

    public void setAktion(int decision, int g, int n, String aktion){
        aktionen.get(decision).get(g).set(n, aktion);
    }

    public void print(int decision, int x){
        for(int i = 0; i < x; i++){
            for(int j = 0; j < x; j++){
                String cost = String.valueOf(Double.POSITIVE_INFINITY);
                
                if(getCost(decision, i, j) != Double.POSITIVE_INFINITY){
                    cost = String.valueOf((double) Math.round(getCost(decision, i, j) * 10) / 10);
                }
                System.out.print(cost + " ");
            }
            System.out.println();
        }

        
        for(int i = 0; i < x; i++){
            for(int j = 0; j < x; j++){
                System.out.print(getAktion(decision, i, j) + " ");
            }
            System.out.println();
        }
    }
}
