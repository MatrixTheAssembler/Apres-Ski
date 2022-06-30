import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainDebug {
    private static Data data = new Data();

    private static final int lagerMaxG = 200;
    private static final int lagerMaxN = 100;

    private static final int decisions = 240;

    private static final int einheitG = 20;
    private static final int einheitN = 5;

    private static final int hubschrauberMaxE = 4;
    private static final int hubschrauberCostFix = 50;
    private static final int hubschrauberCostE = 5;

    private static final int seilbahnMaxE = 3;
    private static final int seilbahnCostFix = 0;
    private static final int seilbahnCostE = 10;

    private static final int kaufCostGE = 20;
    private static final int kaufCostNE = 25;

    private static final int GewinnCostG = -80 / einheitG;
    private static final int GewinnCostN = -80 / einheitN;

    private static Map<Integer, Double> pVormittagBesucher = new HashMap<>();
    private static Map<Integer, Double> pNachmittagBesucher = new HashMap<>();

    private static final double pVormittagG = 0.3;
    private static final double pVormittagN = 0.2;

    private static final double pNachmittagG = 0.7;
    private static final double pNachmittagN = 0.1;

    private static final int aktionenStateDecisionDebug = 120;
    private static final int aktionenStateLagerGDebug = 100;
    private static final int aktionenStateLagerNDebug = 50;

    private static int currentDecisionDebug = 0;
    private static int currentLagerGDebug = 0;
    private static int currentLagerNDebug = 0;
    private static Map<String, Double> aktionenStateDebug = new HashMap<>();

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
            currentDecisionDebug = decision;

            for (int lagerG = 0; lagerG <= lagerMaxG; lagerG++) {
                // System.out.println("Decision: " + decision + " LagerG: " + lagerG);
                for (int lagerN = 0; lagerN <= lagerMaxN; lagerN++) {

                    currentLagerGDebug = lagerG;
                    currentLagerNDebug = lagerN;

                    if (isVormittag(decision)) {
                        calculateMinExpectedCosts(decision, lagerG, lagerN, pVormittagBesucher, pVormittagG, pVormittagN);
                    } else {
                        calculateMinExpectedCosts(decision, lagerG, lagerN, pNachmittagBesucher, pNachmittagG, pNachmittagN);
                    }

                    // if (decision == 0) break;

                    // if(lagerN == 0) break;
                    // if(lagerN == 1) break;
                }

                // if (decision == 0) break;

                // if(lagerG == 0) break;
                // if(lagerG == 1) break;
            }

            // if (decision == 238) break;
            // if (decision == 239) break;
        }

        System.out.println();

        data.print(239, 15);
        data.print(238, 15);
        data.print(1, 15);
        data.print(0, 15);

        System.out.println();

        
        Map<String, Double> sortedAktionenStateDebug = aktionenStateDebug.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        for (String key : sortedAktionenStateDebug.keySet()) {
            System.out.println(key + ": " + sortedAktionenStateDebug.get(key));
        }

        System.out.println("\nMinimal cost and aktion for " + aktionenStateDecisionDebug + " " + aktionenStateLagerGDebug + " " + aktionenStateLagerNDebug + ": " + data.getCost(aktionenStateDecisionDebug, aktionenStateLagerGDebug, aktionenStateLagerNDebug) + " " + data.getAktion(aktionenStateDecisionDebug, aktionenStateLagerGDebug, aktionenStateLagerNDebug));
    }

    private static double calculateAktionCost(int decision, int lagerG, int newLagerG, int lagerN, int newLagerN, int aktionG, int aktionN, Map<Integer, Double> pB, double pG, double pN) {
        if (aktionG == 0 && aktionN == 0) {
            return 0;
        }

        double einkaufCostSum = kaufCostGE * aktionG + kaufCostNE * aktionN;
        // System.out.println("KaufCostGE: " + kaufCostGE + " * AktionG: " + aktionG + " + KaufCostNE: " + kaufCostNE + " * AktionN: " + aktionN + " = EinkaufCostSum: " + einkaufCostSum);

        double transportCostSum = 0;
        if (aktionG + aktionN <= seilbahnMaxE && isVormittag(decision)) {
            transportCostSum = seilbahnCostFix + aktionG * seilbahnCostE + aktionN * seilbahnCostE;
            // System.out.println("SeilbahnCostFix: " + seilbahnCostFix + " + AktionG: " + aktionG + " * SeilbahnCostE: " + seilbahnCostE + " + AktionN: " + aktionN + " * SeilbahnCostE: " + seilbahnCostE + " = TransportCostSum: " + transportCostSum);
        } else {
            transportCostSum = hubschrauberCostFix + aktionG * hubschrauberCostE + aktionN * hubschrauberCostE;
            // System.out.println("HubschrauberCostFix: " + hubschrauberCostFix + " + AktionG: " + aktionG + " * HubschrauberCostE: " + hubschrauberCostE + " + AktionN: " + aktionN + " * HubschrauberCostE: " + hubschrauberCostE + " = TransportCostSum: " + transportCostSum);
        }

        double fixCost = einkaufCostSum + transportCostSum;
        // System.out.println("EinkaufCostSum: " + einkaufCostSum + " + TransportCostSum: " + transportCostSum + " = FixCost: " + fixCost);

        List<Double> besucherCosts = new ArrayList<Double>();
        pB.keySet().forEach(anzahlB -> {
            int anzahlVerkaufG = (int) Math.min(newLagerG, Math.round(pG * anzahlB));
            int anzahlVerkaufN = (int) Math.min(newLagerN, Math.round(pN * anzahlB));

            double variableCost = anzahlVerkaufG * GewinnCostG + anzahlVerkaufN * GewinnCostN;
            // System.out.println("AnzahlVerkaufG: " + anzahlVerkaufG + " * GewinnCostG: " + GewinnCostG + " + AnzahlVerkaufN: " + anzahlVerkaufN + " * GewinnCostN: " + GewinnCostN + " = VariableCost: " + variableCost);
            double folgeCost = data.getCost(decision + 1, newLagerG - anzahlVerkaufG, newLagerN - anzahlVerkaufN);
            if (folgeCost == Double.POSITIVE_INFINITY) {
                // System.out.println("FolgeCost: " + folgeCost + " Decision: " + decision + " NewLagerG - AnzahlVerkaufG: " + (newLagerG - anzahlVerkaufG) + " + NewLagerN - AnzahlVerkaufN: " + (newLagerN - anzahlVerkaufN) + " = 0");
            }

            besucherCosts.add((fixCost + variableCost + folgeCost) * pB.get(anzahlB));
            // System.out.println("FixCost: " + fixCost + " + VariableCost: " + variableCost + " + FolgeCost: " + folgeCost + " * PB: " + pB.get(anzahlB) + " = BesucherCost: " + besucherCosts.get(besucherCosts.size() - 1));
            // System.out.println();
        });

        double expectedCost = besucherCosts.stream().mapToDouble(d -> d).sum();
        // System.out.println("ExpectedCost: " + expectedCost);
        // System.out.println("#################");

        return expectedCost;
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

                if (currentDecisionDebug == aktionenStateDecisionDebug && currentLagerGDebug == aktionenStateLagerGDebug && currentLagerNDebug == aktionenStateLagerNDebug) {
                    aktionenStateDebug.put(aktionToLetters(aktionG, aktionN), aktionCost);
                }

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
        for (int i = aktionG + aktionN; i < 4; i++) {
            aktion += "X";
        }
        return aktion;
    }

    private static boolean isVormittag(int t) {
        return t % 2 == 0;
    }
}