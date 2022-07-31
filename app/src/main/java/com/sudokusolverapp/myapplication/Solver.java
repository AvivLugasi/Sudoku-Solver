package com.sudokusolverapp.myapplication;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class Solver {

     //coordinates of selected cell by the user
     int selected_Row;
     int selected_Column;
    //store the sudoku board, empty cell value is '.'
     char[][] board;
    //store indexes and possibilities of the empty cells
    ArrayList<EmptyCell> emptyCells;

    HashSet<EmptyCell> ecSet;
    HashSet<EmptyCell>revelSet;

    boolean wasSolved;

    public Solver(){

        selected_Row = -1;
        selected_Column = -1;
        this.wasSolved = false;
        board = new char[9][9];
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                board[i][j] = '.';
            }
        }
        this.emptyCells = new ArrayList<>();
        this.ecSet = new HashSet<>();
        this.revelSet = new HashSet<>();
    }

    public  int getSelectedRow(){
        return selected_Row;
    }

    public int getSelectedColumn(){
        return selected_Column;
    }

    public void setSelectedRow(int r){
        selected_Row = r;
    }

    public void setSelectedColumn(int c){
        selected_Column = c;
    }

    //set new value to selected cell by the user
    public void setNumberPos(char num){
        //if valid
        if(this.selected_Row != -1 && this.selected_Column != -1){
            //if the user made a mistake
            if(this.board[this.selected_Row-1][this.selected_Column-1] == num){
                this.board[this.selected_Row-1][this.selected_Column-1] = '.';
            }
            else{
                this.board[this.selected_Row-1][this.selected_Column-1] = num;
            }
        }
    }


    public void resetBoard(){
        board = new char[9][9];
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                board[i][j] = '.';
            }
        }
        this.wasSolved=false;
        this.emptyCells = new ArrayList<>();
        this.ecSet = new HashSet<>();
        this.revelSet = new HashSet<>();
    }
    public boolean solve(){

        Object flag = this.solveSudoku(this.board, this.emptyCells);
        if(flag != null)
            return true;
        this.wasSolved=false;
        this.emptyCells = new ArrayList<>();
        this.ecSet = new HashSet<>();
        this.revelSet = new HashSet<>();
        return false;
    }

    //solve a sudoku of 9X9, it assumed that the sudoku is valid(has only one solution)
    private  char[][] solveSudoku(char[][] board, ArrayList<EmptyCell> emptyCells) {

        int[][]rowsHash = new int[9][10];//array of arrays for checking duplicates in each row
        int[][]colHash = new int[9][10];//array of arrays for checking duplicates in each column
        int[][]subBoxHash = new int[9][10];//array of arrays for checking duplicates in each sub box
        //check validity and fill row, col and sub box hash arrays
        // with the digits that are already exists
        if(!isValidFullSudoku(board, rowsHash, colHash, subBoxHash)){
            //sudoku is not valid
            return null;
        }

        //update Possibilities for each empty cell
        updatePossiblites(board, rowsHash, colHash, subBoxHash, emptyCells);
        //get the empty cells and update their axis, possibilities
        //fill the empty cells
        Backtracking(board, emptyCells);
        this.wasSolved = true;
        return board;
    }

    //fill the empty cells
    private  void Backtracking(char[][] board, ArrayList<EmptyCell> emptyCells) {

        int iti = 0;
        while(iti != emptyCells.size()) {

            EmptyCell ec = emptyCells.get(iti);
            boolean flag = false;
            while(!flag) {
                //if we checked all the possibilities in the current cell
                if(ec.iter==ec.array.size()) {
                    ec.iter=0;
                    emptyCells.set(iti, ec);
                    break;
                }
                //get the possibility
                int val = ec.array.get(ec.iter);
                //get the next possibility
                ec.iter++;
                emptyCells.set(iti, ec);
                //set value in the current cell
                board[ec.row][ec.col] = (char) (48 + val);
                //display.invalidate();
                //check if valid
                if(isValidSudoku(board,ec.row, ec.col, (char) (48 + val))) {
                    flag = true;

                }

            }
            //if valid move to next cell else go back to the previous one
            if(flag)
                iti++;
            else {
                board[emptyCells.get(iti).row][emptyCells.get(iti).col] = '.';
                iti--;
            }
        }
    }


    //retain for each empty cell the Possibilities
    private void updatePossiblites(char[][] board, int[][]rowsHash, int[][]colHash, int[][]subBoxHash, ArrayList<EmptyCell> emptyCells) {

        //Iterating on each cell
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                //if the current cell is empty
                if(board[i][j] == '.') {
                    HashSet<Integer> cellPossiblites = getPossiblites(rowsHash, colHash, subBoxHash, i, j);//Possibilities for cell i,j
                    //update the Possibilities map
                    EmptyCell ec = new EmptyCell(i,j,cellPossiblites);
                    emptyCells.add(ec);
                    this.ecSet.add(ec);
                }

            }
        }
    }

    //get Possibilities set for cell i,j
    private HashSet<Integer> getPossiblites(int[][]rowsHash, int[][]colHash, int[][]subBoxHash, int i, int j){

        //get the sub box number(0-8)
        int subBox = determineSubBox(i , j);
        HashSet<Integer> rowSet = new HashSet<>();//row Possibilities set
        HashSet<Integer> colSet = new HashSet<>();//col Possibilities set
        HashSet<Integer> boxSet = new HashSet<>();//sub box Possibilities set
        for(int k = 1; k < 10; k++) {
            //if the digit K is not in row i
            if(rowsHash[i][k] == 0)
                rowSet.add(k);
            //if the digit K is not in col i
            if(colHash[j][k] == 0)
                colSet.add(k);
            //if digit k is not in the sub Box of cell i,j
            if(subBoxHash[subBox][k] == 0)
                boxSet.add(k);
            //get the intersection of the three sets
            rowSet.retainAll(colSet); rowSet.retainAll(boxSet);
        }
        return rowSet;
    }

    //return the sub box number of the current cell in the sudoku matrix
    private int determineSubBox(int i , int j) {
        if(i >= 0 && i < 3) {
            if(j >= 0 && j < 3)
                return 0;
            else if(j >= 3 && j < 6)
                return 1;
            return 2;
        }
        else if(i >= 3 && i < 6) {
            if(j >= 0 && j < 3)
                return 3;
            else if(j >= 3 && j < 6)
                return 4;
            return 5;
        }
        else {
            if(j >= 0 && j < 3)
                return 6;
            else if(j >= 3 && j < 6)
                return 7;
            return 8;
        }
    }

    //return if a sudoku is valid or not
    private boolean isValidSudoku(char[][] board, int row, int col,  char val) {

        //check row and col
        for(int i = 0; i < 9; i++) {
            if( (board[row][i] == val && i != col) || (board[i][col] == val && i != row)) {
                return false;
            }
        }

        int r = row < 3 ?0: row < 6 ?3:6;
        int c = col < 3 ?0: col < 6 ?3:6;
        int initialC = c;
        int initialR = r;
        //check sub box of the cell
        for(r = initialR;r < initialR + 3; r++) {
            for(c = initialC; c < initialC + 3; c++) {
                if(board[r][c] == val && (r != row && c != col)) {
                    return false;
                }
            }
        }
        return true;
    }

    //return if a sudoku is valid or not
    private  boolean isValidFullSudoku(char[][] board, int[][]rowsHash, int[][]colHash, int[][]subBoxHash) {

        //Iterating on each cell
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                //if there is a digit in the current cell
                if(board[i][j] != '.') {
                    int val = board[i][j];//the ASCII value of the character
                    val = val - 48;//get the digit(1-9)value of the character
                    //if the digit already exist in the column or row
                    if(rowsHash[i][val] !=  0 || colHash[j][val] !=  0)
                        return false;
                    //get the sub box number(0-8)
                    int subBox = determineSubBox(i , j);
                    //if the digit already exist in the sub box
                    if(subBoxHash[subBox][val] != 0)
                        return false;
                    //insert the digit to the Hash arrays
                    rowsHash[i][val] = val; colHash[j][val] = val; subBoxHash[subBox][val] = val;
                }

            }
        }
        //if a valid sudoku
        return true;
    }

}
//this class present an empty cell
class EmptyCell{
    protected int row;//row of the cell
    protected int col;//col of the cell
    protected int iter = 0;//the index of the next possibility digit we want to check
    protected ArrayList<Integer>array;//array of all the possibilities digit of the current cell

    protected EmptyCell(int row, int col,HashSet<Integer> q) {
        this.row = row;
        this.col = col;
        array = new ArrayList<>(q);
    }

    protected EmptyCell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmptyCell emptyCell = (EmptyCell) o;
        return row == emptyCell.row && col == emptyCell.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @NonNull
    @Override
    public String toString() {
        return row+ " " + col;
    }
}

