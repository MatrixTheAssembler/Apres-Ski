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

                    // if (decision == 0) break;

                    // if(lagerN == 0) break;
                    if(lagerN == 1) break;
                }

                // if (decision == 0) break;

                // if(lagerG == 0) break;
                if(lagerG == 1) break;
            }

            // if (decision == 238) break;
            if (decision == 239) break;
        }

        System.out.println("#################");
        // findShortestPath();

        data.print(240, 15);
        data.print(239, 15);
        data.print(238, 15);
        data.print(1, 15);
        data.print(0, 15);
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
        if(aktionG == 0 && aktionN == 0) {
            return 0;
        }

        double einkaufCostSum = kaufCostGE * aktionG + kaufCostNE * aktionN;
        System.out.println("KaufCostGE: " + kaufCostGE + " * AktionG: " + aktionG + " + KaufCostNE: " + kaufCostNE + " * AktionN: " + aktionN + " = EinkaufCostSum: " + einkaufCostSum);

        double transportCostSum = 0;
        if (aktionG + aktionN == 3 && isVormittag(decision)) {
            transportCostSum = seilbahnCostFix + aktionG * seilbahnCostE + aktionN * seilbahnCostE;
            System.out.println("SeilbahnCostFix: " + seilbahnCostFix + " + AktionG: " + aktionG + " * SeilbahnCostE: " + seilbahnCostE + " + AktionN: " + aktionN + " * SeilbahnCostE: " + seilbahnCostE + " = TransportCostSum: " + transportCostSum);
        } else {
            transportCostSum = hubschrauberCostFix + aktionG * hubschrauberCostE + aktionN * hubschrauberCostE;
            System.out.println("HubschrauberCostFix: " + hubschrauberCostFix + " + AktionG: " + aktionG + " * HubschrauberCostE: " + hubschrauberCostE + " + AktionN: " + aktionN + " * HubschrauberCostE: " + hubschrauberCostE + " = TransportCostSum: " + transportCostSum);
        }

        double fixCost = einkaufCostSum + transportCostSum;
        System.out.println("EinkaufCostSum: " + einkaufCostSum + " + TransportCostSum: " + transportCostSum + " = FixCost: " + fixCost);

        int newLagerG = lagerG + aktionG;
        int newLagerN = lagerN + aktionN;

        List<Double> besucherCosts = new ArrayList<Double>();
        pB.keySet().forEach(anzahlB -> {
            // int anzahlVerkaufG = (int) Math.min(lagerG, Math.round(pG * anzahlB));
            // int anzahlVerkaufN = (int) Math.min(lagerN, Math.round(pN * anzahlB));
            int anzahlVerkaufG = (int) Math.min(newLagerG, Math.round(pG * anzahlB));
            int anzahlVerkaufN = (int) Math.min(newLagerN, Math.round(pN * anzahlB));

            double variableCost = anzahlVerkaufG * GewinnCostG + anzahlVerkaufN * GewinnCostN;
            System.out.println("AnzahlVerkaufG: " + anzahlVerkaufG + " * GewinnCostG: " + GewinnCostG + " + AnzahlVerkaufN: " + anzahlVerkaufN + " * GewinnCostN: " + GewinnCostN + " = VariableCost: " + variableCost);
            double folgeCost = data.getCost(decision + 1, newLagerG - anzahlVerkaufG, newLagerN - anzahlVerkaufN);
            if(folgeCost == Double.POSITIVE_INFINITY) {
                System.out.println("FolgeCost: " + folgeCost + " Decision: " + decision + " NewLagerG - AnzahlVerkaufG: " + (newLagerG - anzahlVerkaufG) + " + NewLagerN - AnzahlVerkaufN: " + (newLagerN - anzahlVerkaufN) + " = 0");
            }

            besucherCosts.add((fixCost + variableCost + folgeCost) * pB.get(anzahlB));
            System.out.println("FixCost: " + fixCost + " + VariableCost: " + variableCost + " + FolgeCost: " + folgeCost + " * PB: " + pB.get(anzahlB) + " = BesucherCost: " + besucherCosts.get(besucherCosts.size() - 1));
            System.out.println();
        });

        double expectedCost = besucherCosts.stream().mapToDouble(d -> d).sum();
        System.out.println("ExpectedCost: " + expectedCost);
        System.out.println("#################");

        return expectedCost;
    }

    private static void calculateMinExpectedCosts(int decision, int lagerG, int lagerN, Map<Integer, Double> pB,
            double pG, double pN) {
        double minExpectedCost = Double.POSITIVE_INFINITY;
        String minCostAktion = "";

        for (int aktionG = 0; aktionG <= 4; aktionG++) {
            for (int aktionN = 0; aktionN <= 4 - aktionG; aktionN++) {
                if(lagerG + aktionG > lagerMaxG || lagerN + aktionN > lagerMaxN) {
                    continue;
                }

                double aktionCost = calculateAktionCost(decision, lagerG, lagerN, aktionG, aktionN, pB, pG, pN);

                // System.out.println("Aktion: " + aktionG + " " + aktionN + " Cost: " + aktionCost);

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
        if(aktionG == 0) aktion += "X";
        if(aktionN == 0) aktion += "X";
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