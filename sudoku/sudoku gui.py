sudoku gui
class GameState:
    """An ant collective that manages global game state and simulates time."""

class State:
    """A State holds a current game state and all of its attributes"""

    def __init__(self):
        """Create a new gamestate"""
        self.gs = {}

    def getState(self, key=None):
        if key:
            return self.gs[key]
        return self.gs

    def updateState(self, key, val):
        self.gs[key] = valdef _init_places(self, gamestate):
        """Calculate all of our place data"""
        self.places = {};
        #self.images = {'AntQueen': dict()}
        rows = 9
        cols = 9
        subgrid = 9
        for name, place in gamestate.places.items():
            if place.name == 'Hive':
                continue
            pCol = self.get_place_column(name)
            pRow = self.get_place_row(name)
            pSubgrid = self.get_subgrid(name)