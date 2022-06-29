from data import Data
import numpy as np


data = Data()

lager_max_g = 200
lager_max_n = 100

decisions = 240

einheit_g = 20
einheit_n = 5

hubschrauber_max_e = 4
hubschrauber_cost_fix = 50
hubschrauber_cost_e = 5

seilbahn_max_e = 3
seilbahn_cost_fix = 0
seilbahn_cost_e = 10

kauf_cost_g_e = 20
kauf_cost_n_e = 25

gewinn_cost_g = -80 / einheit_g
gewinn_cost_n = -150 / einheit_n

p_vormittag_besucher = {0: 0.1, 25: 0.1, 50: 0.2, 75: 0.3, 100: 0.2, 125: 0.1, 150: 0.0}
p_nachmittag_besucher = {0: 0.0, 25: 0.05, 50: 0.05, 75: 0.3, 100: 0.3, 125: 0.2, 150: 0.1}

p_vormittag_g = 0.3
p_vormittag_n = 0.2

p_nachmittag_g = 0.7
p_nachmittag_n = 0.1

path_aktionen = []
path_costs = []
path_cost = np.inf



def isVormittag(t):
    return t % 2 == 0


def calculate_aktion_cost(decision, lager_g, lager_n, aktion_g, aktion_n, p_b, p_g, p_n):
    einkauf_cost_sum = kauf_cost_g_e * aktion_g + kauf_cost_n_e * aktion_n

    transport_cost_sum = 0
    if(aktion_g + aktion_n == 3 and isVormittag(decision)):
        transport_cost_sum = seilbahn_cost_fix + aktion_g * seilbahn_cost_e + aktion_n * seilbahn_cost_e
    else:
        transport_cost_sum = hubschrauber_cost_fix + aktion_g * hubschrauber_cost_e + aktion_n * hubschrauber_cost_e

    fix_cost = einkauf_cost_sum + transport_cost_sum

    besucher_costs = []
    for anzahl_b in list(p_b.keys()):
        anzahl_verkauf_g = min(lager_g, int(np.round(p_g * anzahl_b)))
        anzahl_verkauf_n = min(lager_n, int(np.round(p_n * anzahl_b)))

        # print("Beuscher und Verkäufe")
        # print(int(np.round(p_n * anzahl_b)))
        # print(anzahl_verkauf_n)

        variable_cost = anzahl_verkauf_g * gewinn_cost_g + anzahl_verkauf_n * gewinn_cost_n
        folge_cost = data.get_cost(decision+1, lager_g - anzahl_verkauf_g, lager_n - anzahl_verkauf_n)

        besucher_costs.append((fix_cost + variable_cost + folge_cost) * p_b[anzahl_b])

    return sum(besucher_costs)



for decision in range(decisions-1, -1, -1):
    # print("D: " + str(decision))
    for lager_g in range(lager_max_g):
        print("         G: " + str(lager_g))
        for lager_n in range(lager_max_n):
            # print("                             N: " + str(lager_n))
            if(isVormittag(decision)):
                min_expected_cost = np.inf
                min_cost_aktion = None

                for aktion_g in range(4+1):
                    for aktion_n in range(4-aktion_g+1):
                        for aktion_x in range(4-aktion_g-aktion_n+1):

                            # einkauf_cost_sum = kauf_cost_g_e * aktion_g + kauf_cost_n_e * aktion_n

                            # transport_cost_sum = 0
                            # if(aktion_g + aktion_n == 3):
                            #     transport_cost_sum = seilbahn_cost_fix + aktion_g * seilbahn_cost_e + aktion_n * seilbahn_cost_e
                            # else:
                            #     transport_cost_sum = hubschrauber_cost_fix + aktion_g * hubschrauber_cost_e + aktion_n * hubschrauber_cost_e

                            # fix_cost = einkauf_cost_sum + transport_cost_sum

                            # besucher_costs = []
                            # for anzahl_b in list(p_vormittag_besucher.keys()):
                            #     anzahl_verkauf_g = min(lager_g, int(np.round(p_vormittag_g * anzahl_b)))
                            #     anzahl_verkauf_n = min(lager_n, int(np.round(p_vormittag_n * anzahl_b)))

                            #     variable_cost = anzahl_verkauf_g * gewinn_cost_g + anzahl_verkauf_n * gewinn_cost_n
                            #     folge_cost = costs.get_cost(decision+1, lager_g - anzahl_verkauf_g, lager_n - anzahl_verkauf_n)

                            #     besucher_costs.append((fix_cost + variable_cost + folge_cost) * p_vormittag_besucher[anzahl_b])

                            # besucher_costs_sum = sum(besucher_costs)

                            besucher_costs_sum = calculate_aktion_cost(decision, lager_g, lager_n, aktion_g, aktion_n, p_vormittag_besucher, p_vormittag_g, p_vormittag_n)

                            if(besucher_costs_sum < min_expected_cost):
                                min_expected_cost = besucher_costs_sum
                                min_cost_aktion = [aktion_g, aktion_n]

                data.set_cost(decision, lager_g, lager_n, [min_cost_aktion, min_expected_cost])
            else:
                min_expected_cost = np.inf
                min_cost_aktion = None

                for aktion_g in range(4+1):
                    for aktion_n in range(4-aktion_g+1):
                        for aktion_x in range(4-aktion_g-aktion_n+1):

                            # einkauf_cost_sum = kauf_cost_g_e * aktion_g + kauf_cost_n_e * aktion_n
                            # transport_cost_sum = hubschrauber_cost_fix + aktion_g * hubschrauber_cost_e + aktion_n * hubschrauber_cost_e
                            # fix_cost = einkauf_cost_sum + transport_cost_sum

                            # besucher_costs = []
                            # for anzahl_b in list(p_nachmittag_besucher.keys()):
                            #     anzahl_verkauf_g = min(lager_g, int(np.round(p_nachmittag_g * anzahl_b)))
                            #     anzahl_verkauf_n = min(lager_n, int(np.round(p_nachmittag_n * anzahl_b)))

                            #     variable_cost = anzahl_verkauf_g * gewinn_cost_g + anzahl_verkauf_n * gewinn_cost_n
                            #     folge_cost = costs.get_cost(decision+1, lager_g - anzahl_verkauf_g, lager_n - anzahl_verkauf_n)

                            #     besucher_costs.append((fix_cost + variable_cost + folge_cost) * p_nachmittag_besucher[anzahl_b])

                            # besucher_costs_sum = sum(besucher_costs)
                            besucher_costs_sum = calculate_aktion_cost(decision, lager_g, lager_n, aktion_g, aktion_n, p_nachmittag_besucher, p_nachmittag_g, p_nachmittag_n)

                            if(besucher_costs_sum < min_expected_cost):
                                min_expected_cost = besucher_costs_sum
                                min_cost_aktion = [aktion_g, aktion_n]

                data.set_cost(decision, lager_g, lager_n, [min_cost_aktion, min_expected_cost])

    break

        # if(decision == 0 and lager_g == 0 and lager_n == 0):
        #     break


# for decision in range(decisions):
#     min_cost = np.inf
#     min_cost_state = None

#     for lager_g in range(lager_max_g):
#         for lager_n in range(lager_max_n):
#             if(costs.get_cost(decision, lager_g, lager_n) < min_cost):
#                 min_cost = costs.get_cost(decision, lager_g, lager_n)
#                 min_cost_state = (lager_g, lager_n)

#     for aktion_g in range(4+1):
#         for aktion_n in range(4-aktion_g+1):
#             for aktion_x in range(4-aktion_g-aktion_n+1):
#                 pass




data.print(239, 5)
data.print(238, 5)