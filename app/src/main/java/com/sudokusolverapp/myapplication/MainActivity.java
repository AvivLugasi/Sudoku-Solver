package com.sudokusolverapp.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private SudokuBoard gameBoard;
    private Solver gameBoardSolver;
    private Button solveBtn;
    private EditText text;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameBoard = findViewById(R.id.SudokuBoard);
        solveBtn = findViewById(R.id.solveBtn);
        text = findViewById(R.id.inValidText);
        text.setFocusable(false);
        gameBoardSolver = gameBoard.getSolver();
        System.out.println("testing");
    }

    public void BTNOnePress(View view){
        gameBoardSolver.setNumberPos('1');
        gameBoard.invalidate();
    }

    public void BTNTwoPress(View view){
        gameBoardSolver.setNumberPos('2');
        gameBoard.invalidate();
    }

    public void BTNThreePress(View view){
        gameBoardSolver.setNumberPos('3');
        gameBoard.invalidate();
    }

    public void BTNFourPress(View view){
        gameBoardSolver.setNumberPos('4');
        gameBoard.invalidate();
    }

    public void BTNFivePress(View view){
        gameBoardSolver.setNumberPos('5');
        gameBoard.invalidate();
    }

    public void BTNSixPress(View view){
        gameBoardSolver.setNumberPos('6');
        gameBoard.invalidate();
    }

    public void BTNSevenPress(View view){
        gameBoardSolver.setNumberPos('7');
        gameBoard.invalidate();
    }

    public void BTNEightPress(View view){
        gameBoardSolver.setNumberPos('8');
        gameBoard.invalidate();
    }

    public void BTNNinePress(View view){
        gameBoardSolver.setNumberPos('9');
        gameBoard.invalidate();
    }

    public void Solve(View view){
        if(solveBtn.getText().toString().equals(getString(R.string.Solve))){
            text.setVisibility(View.INVISIBLE);
            solveBtn.setText(getString(R.string.Solving));
            boolean flag = gameBoardSolver.solve();
            if(flag) {
                solveBtn.setText(getString(R.string.RevelAll));
            }
            else{
                solveBtn.setText(getString(R.string.Solve));
                text.setVisibility(View.VISIBLE);
            }
        }
        else if(solveBtn.getText().toString().equals(getString(R.string.RevelAll))){
            text.setVisibility(View.INVISIBLE);
            gameBoard.to_RevelAll=true;
            solveBtn.setText(getString(R.string.Clear));
        }
        else{
            text.setVisibility(View.INVISIBLE);
            solveBtn.setText(getString(R.string.Solve));
            gameBoardSolver.resetBoard();
            gameBoard.to_RevelAll=false;
            gameBoard.invalidate();
        }
    }


}