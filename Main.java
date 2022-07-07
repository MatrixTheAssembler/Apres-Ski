import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static Data data = new Data();
    
    private static final int decisions = 242;

    private static final int einheitG = 20;
    private static final int einheitN = 5;
    
    private static final int lagerMaxG = 10 * einheitG;
    private static final int lagerMaxN = 20 * einheitN;

    private static final int hubschrauberMaxE = 4;
    private static final int hubschrauberCostFix = 50;
    private static final int hubschrauberCostE = 5;

    private static final int seilbahnMaxE = 3;
    private static final int seilbahnCostFix = 0;
    private static final int seilbahnCostE = 10;

    private static final int kaufCostGE = 20;
    private static final int kaufCostNE = 25;

    private static final int gewinnCostG = -80 / einheitG;
    private static final int gewinnCostN = -150 / einheitN;

    private static final Map<Integer, Double> pVormittagBesucher = new HashMap<>();
    private static final Map<Integer, Double> pNachmittagBesucher = new HashMap<>();

    private static final double pVormittagG = 0.3;
    private static final double pVormittagN = 0.2;

    private static final double pNachmittagG = 0.7;
    private static final double pNachmittagN = 0.1;

    
    public static void main(String[] args) {
        pVormittagBesucher.put(0, 0.1);
        pVormittagBesucher.put(25, 0.1);
        pVormittagBesucher.put(50, 0.2);
        pVormittagBesucher.put(75, 0.3);
        pVormittagBesucher.put(100, 0.2);
        pVormittagBesucher.put(125, 0.1);
        pVormittagBesucher.put(150, 0.0);

        pNachmittagBesucher.put(0, 0.0);
        pNachmittagBesucher.put(25, 0.05);
        pNachmittagBesucher.put(50, 0.05);
        pNachmittagBesucher.put(75, 0.3);
        pNachmittagBesucher.put(100, 0.3);
        pNachmittagBesucher.put(125, 0.2);
        pNachmittagBesucher.put(150, 0.1);

        for (int decision = decisions - 1; decision >= 0; decision--) {
            System.out.println("Decision: " + decision);

            for (int lagerG = 0; lagerG <= lagerMaxG; lagerG++) {
                for (int lagerN = 0; lagerN <= lagerMaxN; lagerN++) {

                    if (isVormittag(decision)) {
                        calculateMinExpectedCosts(decision, lagerG, lagerN, pVormittagBesucher, pVormittagG, pVormittagN);
                    } else {
                        calculateMinExpectedCosts(decision, lagerG, lagerN, pNachmittagBesucher, pNachmittagG, pNachmittagN);
                    }
                }
            }
        }

        System.out.println();

        data.print(239, 15);
        data.print(238, 15);
        data.print(1, 15);
        data.print(0, 15);
    }


    private static void calculateMinExpectedCosts(int decision, int lagerG, int lagerN, Map<Integer, Double> pB, double pG, double pN) {
        double minExpectedCost = Double.POSITIVE_INFINITY;
        String minCostAktion = "";

        for (int aktionG = 0; aktionG <= hubschrauberMaxE; aktionG++) {
            for (int aktionN = 0; aktionN <= hubschrauberMaxE - aktionG; aktionN++) {

                int newLagerG = lagerG + aktionG * einheitG;
                int newLagerN = lagerN + aktionN * einheitN;

                if (newLagerG > lagerMaxG || newLagerN > lagerMaxN) {
                    continue;
                }

                double aktionCost = calculateAktionCost(decision, lagerG, newLagerG, lagerN, newLagerN, aktionG, aktionN, pB, pG, pN);

                if (aktionCost < minExpectedCost) {
                    minExpectedCost = aktionCost;
                    minCostAktion = aktionToLetters(aktionG, aktionN);
                }
            }
        }

        data.setCost(decision, lagerG, lagerN, minExpectedCost);
        data.setAktion(decision, lagerG, lagerN, minCostAktion);
    }


    private static double calculateAktionCost(int decision, int lagerG, int newLagerG, int lagerN, int newLagerN, int aktionG, int aktionN, Map<Integer, Double> pB, double pG, double pN) {
        double einkaufCostSum = kaufCostGE * aktionG + kaufCostNE * aktionN;

        double transportCostSum = 0;
        if (aktionG != 0 || aktionN != 0) {
            if (aktionG + aktionN <= seilbahnMaxE && isVormittag(decision)) {
                transportCostSum = seilbahnCostFix + aktionG * seilbahnCostE + aktionN * seilbahnCostE;
            } else {
                transportCostSum = hubschrauberCostFix + aktionG * hubschrauberCostE + aktionN * hubschrauberCostE;
            }
        }

        double fixCost = einkaufCostSum + transportCostSum;

        List<Double> besucherCosts = new ArrayList<Double>();
        pB.keySet().forEach(anzahlB -> {
            int anzahlVerkaufG = (int) Math.min(newLagerG, Math.round(pG * anzahlB));
            int anzahlVerkaufN = (int) Math.min(newLagerN, Math.round(pN * anzahlB));

            double variableCost = anzahlVerkaufG * gewinnCostG + anzahlVerkaufN * gewinnCostN;
            double folgeCost = data.getCost(decision + 1, newLagerG - anzahlVerkaufG, newLagerN - anzahlVerkaufN);

            besucherCosts.add((fixCost + variableCost + folgeCost) * pB.get(anzahlB));
        });

        double expectedCost = besucherCosts.stream().mapToDouble(d -> d).sum();

        return expectedCost;
    }

    
    private static String aktionToLetters(int aktionG, int aktionN) {
        String aktion = "";
        for (int i = 0; i < aktionG; i++) {
            aktion += "G";
        }
        for (int i = 0; i < aktionN; i++) {
            aktion += "N";
        }
        for (int i = aktionG + aktionN; i < 4; i++) {
            aktion += "X";
        }
        return aktion;
    }

    private static boolean isVormittag(int t) {
        return t % 2 == 0;
    }
}