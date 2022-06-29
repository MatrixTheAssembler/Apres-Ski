import numpy as np

class Data:
    _costs = [[[]]]
    _aktionen = [[[]]]

    def __init__(self):
        self._costs = np.full((241, 200, 100), np.inf)
        self._aktionen = np.full((241, 200, 100), "")
        self._costs[-1, :, :] = 0

    def get_cost(self, decision, g, n):
        return self._costs[decision, g, n]

    def set_cost(self, decision, g, n, cost):
        self._costs[decision, g, n] = cost

    def get_aktion(self, decision, g, n):
        return self._aktionen[decision, g, n]

    def set_aktion(self, decision, g, n, aktion):
        self._aktionen[decision, g, n] = aktion

    def print(self, decision, x):
        print(self._costs[decision, :x, :x])
        print(self._aktionen[decision, :x, :x])