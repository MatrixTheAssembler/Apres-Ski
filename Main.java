import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static Data data = new Data();

    private static int lagerMaxG = 200;
    private static int lagerMaxN = 100;

    private static int decisions = 240;

    private static int einheitG = 20;
    private static int einheitN = 5;

    private static int hubschrauberMaxE = 4;
    private static int hubschrauberCostFix = 50;
    private static int hubschrauberCostE = 5;

    private static int seilbahnMaxE = 3;
    private static int seilbahnCostFix = 0;
    private static int seilbahnCostE = 10;

    private static int kaufCostGE = 20;
    private static int kaufCostNE = 25;

    private static int GewinnCostG = -80 / einheitG;
    private static int GewinnCostN = -80 / einheitN;

    private static Map<Integer, Double> pVormittagBesucher = new HashMap<>();
    private static Map<Integer, Double> pNachmittagBesucher = new HashMap<>();

    private static double pVormittagG = 0.3;
    private static double pVormittagN = 0.2;

    private static double pNachmittagG = 0.7;
    private static double pNachmittagN = 0.1;

    private static List<String> aktionen = new ArrayList<>();
    private static List<Double> pathCosts = new ArrayList<>();
    private static double pathCost = Double.POSITIVE_INFINITY;

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
            for (int lagerG = 0; lagerG < lagerMaxG; lagerG++) {
                // System.out.println("Decision: " + decision + " LagerG: " + lagerG);
                for (int lagerN = 0; lagerN < lagerMaxN; lagerN++) {
                    if (isVormittag(decision)) {
                        calculateMinExpectedCosts(decision, lagerG, lagerN, pVormittagBesucher,
                                pVormittagG,
                                pVormittagN);
                    } else {
                        calculateMinExpectedCosts(decision, lagerG, lagerN, pNachmittagBesucher,
                                pNachmittagG,
                                pNachmittagN);
                    }

                    if (decision == 0) {
                        break;
                    }
                }

                if (decision == 0) {
                    break;
                }
            }

            // if (decision == 238) {
            // break;
            // }
        }

        System.out.println("#################");
        // findShortestPath();

        data.print(239, 5);
        data.print(238, 5);
        data.print(1, 5);
        data.print(0, 5);
    }

    private static void findShortestPath() {
        String nextStep = data.getAktion(0, 0, 0);
        aktionen.add(nextStep);
        pathCost = data.getCost(0, 0, 0);
        pathCosts.add(data.getCost(0, 0, 0));

        int aktuellLagerG = 0;
        int aktuellLagerN = 0;

        int aktionG = lettersToAktion(aktionen.get(0))[0];
        int aktionN = lettersToAktion(aktionen.get(0))[1];

        for (int decision = 1; decision < decisions; decision++) {
            System.out.println("Decision: " + decision);
        }
    }

    private static double calculateAktionCost(int decision, int lagerG, int lagerN, int aktionG, int aktionN,
            Map<Integer, Double> pB, double pG, double pN) {
        double einkaufCostSum = kaufCostGE * aktionG + kaufCostNE * aktionN;

        double transportCostSum = 0;
        if (aktionG + aktionN == 3 && isVormittag(decision)) {
            transportCostSum = seilbahnCostFix + aktionG * seilbahnCostE + aktionN * seilbahnCostE;
        } else {
            transportCostSum = hubschrauberCostFix + aktionG * hubschrauberCostE + aktionN * hubschrauberCostE;
        }

        double fixCost = einkaufCostSum + transportCostSum;

        List<Double> besucherCosts = new ArrayList<Double>();
        pB.keySet().forEach(anzahlB -> {
            int anzahlVerkaufG = (int) Math.min(lagerG, Math.round(pG * anzahlB));
            int anzahlVerkaufN = (int) Math.min(lagerN, Math.round(pN * anzahlB));

            double variableCost = anzahlVerkaufG * GewinnCostG + anzahlVerkaufN * GewinnCostN;
            double folgeCost = data.getCost(decision + 1, lagerG - anzahlVerkaufG, lagerN - anzahlVerkaufN);

            besucherCosts.add((fixCost + variableCost + folgeCost) * pB.get(anzahlB));
        });

        return besucherCosts.stream().mapToDouble(d -> d).sum();
    }

    private static void calculateMinExpectedCosts(int decision, int lagerG, int lagerN, Map<Integer, Double> pB,
            double pG, double pN) {
        double minExpectedCost = Double.POSITIVE_INFINITY;
        String minCostAktion = "";

        for (int aktionG = 0; aktionG <= 4; aktionG++) {
            for (int aktionN = 0; aktionN <= 4 - aktionG; aktionN++) {
                double aktionCost = calculateAktionCost(decision, lagerG, lagerN, aktionG, aktionN, pB, pG, pN);

                if (aktionCost < minExpectedCost) {
                    minExpectedCost = aktionCost;
                    minCostAktion = aktionToLetters(aktionG, aktionN);
                }
            }
        }

        data.setCost(decision, lagerG, lagerN, minExpectedCost);
        data.setAktion(decision, lagerG, lagerN, minCostAktion);
    }

    private static String aktionToLetters(int aktionG, int aktionN) {
        String aktion = "";
        for (int i = 0; i < aktionG; i++) {
            aktion += "G";
        }
        for (int i = 0; i < aktionN; i++) {
            aktion += "N";
        }
        return aktion;
    }

    private static int[] lettersToAktion(String aktion) {
        int aktionG = 0;
        int aktionN = 0;

        for (int i = 0; i < aktion.length(); i++) {
            if (aktion.charAt(i) == 'G') {
                aktionG++;
            } else {
                aktionN++;
            }
        }
        return new int[] { aktionG, aktionN };
    }

    private static boolean isVormittag(int t) {
        return t % 2 == 0;
    }
}