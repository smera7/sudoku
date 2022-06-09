location class
class Location: 
    """A Location is the row, column, subgrid position of the number within a 9x9 sudoku grid"""
    def __init__(self, x, y, z, value=None):
        self.x = x
        self.y = y
        self.z = z
