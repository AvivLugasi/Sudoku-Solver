# Sudoku-Solver

A Sudoku solver app for android with gui.<br>
first the user insert the digits to the board then when clicking on the solve button the empty cells filled with<br>
? mark. When clicking on a specific cell the correct digit of this cell is revealed, the user can also reveal all the digits by 
clicking on the reveal all button, in the end he can clear the board and fill it again.<br>

The logic behind the solver function:<br>
1) For each empty cell i removed digits that it cannot be filled with(already exists in the same row/column/box).<br>
2)Using backtracking with only the possible digits for each empty cell.<br>
<br>
linkedIn of creator:<br>
Aviv Lugasi:  https://www.linkedin.com/in/aviv-lugasi-b89693235/<br>
