package com.sudokusolverapp.myapplication;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class SudokuBoard extends View  {

    private final int boardColor;//color of the sudoku board
    private final int cellFillColor;
    private final int cellsHighlightColor;
    private final int letterColor;
    private final int letterColorSolve;
    private final int letterColorHidden;

    private final Paint boardColorPaint = new Paint();
    private final Paint cellFillColorPaint = new Paint();
    private final Paint cellsHighlightColorPaint = new Paint();
    private final Paint letterPaint = new Paint();
    private final Rect letterPaintBounds = new Rect();
    private int cellSize;
    private final Solver solver = new Solver();//sudoku solver class instance
    public boolean to_RevelAll = false;
    public SudokuBoard(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SudokuBoard,
                0,0);

        //get board color
        try{
            boardColor = a.getInteger(R.styleable.SudokuBoard_boardColor,0);
            cellFillColor = a.getInteger(R.styleable.SudokuBoard_cellFillColor,0);
            cellsHighlightColor = a.getInteger(R.styleable.SudokuBoard_cellsHighlightColor,0);
            letterColor = a.getInteger(R.styleable.SudokuBoard_letterColor,0);
            letterColorSolve = a.getInteger(R.styleable.SudokuBoard_letterColorSolve,0);
            letterColorHidden = a.getInteger(R.styleable.SudokuBoard_letterColorHidden,0);
        }finally {
            a.recycle();
        }

    }

    @Override
    protected  void onMeasure(int width, int height){

        super.onMeasure(width,height);
        //fit to user device
        int dimension = Math.min(this.getMeasuredWidth(), this.getMeasuredHeight());
        cellSize = dimension/9;
        //define size of sudoku board
        setMeasuredDimension(dimension, dimension);
    }

    @Override
    protected  void onDraw(Canvas canvas) {

        //paint brash
        boardColorPaint.setStyle(Paint.Style.STROKE);
        boardColorPaint.setStrokeWidth(16);
        //paint bucket
        boardColorPaint.setColor(boardColor);

        boardColorPaint.setAntiAlias(true);

        cellFillColorPaint.setStyle(Paint.Style.FILL);
        cellFillColorPaint.setAntiAlias(true);
        cellFillColorPaint.setColor(cellFillColor);

        cellsHighlightColorPaint.setStyle(Paint.Style.FILL);
        cellsHighlightColorPaint.setAntiAlias(true);
        cellsHighlightColorPaint.setColor(cellsHighlightColor);

        letterPaint.setStyle(Paint.Style.FILL);
        letterPaint.setAntiAlias(true);
        letterPaint.setColor(letterColor);

        colorCell(canvas, solver.getSelectedRow(), solver.getSelectedColumn());
        //draw rectangle
        canvas.drawRect(0, 0, getWidth(), getHeight(), boardColorPaint);
        //draw board
        drawBoard(canvas);
        drawNumbers(canvas);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event){

        boolean isValid;
        //extract coordinates
        float x = event.getX();
        float y = event.getY();

        //get Tap type
        int action = event.getAction();

        if(action == MotionEvent.ACTION_DOWN){
            //transfer coordinates to selected cell in the board
            solver.setSelectedRow((int)Math.ceil(y/cellSize));
            solver.setSelectedColumn((int)Math.ceil(x/cellSize));
            isValid = true;
        }
        else{
            isValid = false;
        }

        return isValid;
    }


    private void drawNumbers(Canvas canvas){
        if(solver.board != null ) {
            letterPaint.setTextSize(cellSize);
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if ((solver.board[i][j] != '.' && solver.wasSolved == false) || (solver.board[i][j] != '.' && solver.wasSolved == true && !solver.ecSet.contains(new EmptyCell(i, j)))) {
                        letterPaint.setColor(letterColor);
                        String text = Character.toString(solver.board[i][j]);
                        float width, height;
                        letterPaint.getTextBounds(text, 0, text.length(), letterPaintBounds);
                        width = letterPaint.measureText(text);
                        height = letterPaintBounds.height();
                        canvas.drawText(text, (j * cellSize) + ((cellSize - width) / 2),
                                (i * cellSize + cellSize) - ((cellSize - height) / 2),
                                letterPaint);
                    }
                }
            }

            letterPaint.setColor(letterColorHidden);
            if (solver.wasSolved) {
                drawHidden(canvas);
            }
            if (solver.wasSolved) {
                letterPaint.setColor(letterColorSolve);
                for (EmptyCell ec : solver.emptyCells) {
                    if (solver.revelSet.contains(ec) || this.to_RevelAll) {
                        solver.revelSet.add(ec);
                        int r = ec.row;
                        int c = ec.col;

                        String text = Character.toString(solver.board[r][c]);
                        float width, height;
                        letterPaint.getTextBounds(text, 0, text.length(), letterPaintBounds);
                        width = letterPaint.measureText(text);
                        height = letterPaintBounds.height();

                        canvas.drawText(text, (c * cellSize) + ((cellSize - width) / 2),
                                (r * cellSize + cellSize) - ((cellSize - height) / 2),
                                letterPaint);
                    }

                }
            }
        }
    }

    private void drawHidden(Canvas canvas){
        letterPaint.setColor(letterColorHidden);

        for (EmptyCell ec : solver.emptyCells) {
            if(!solver.revelSet.contains(ec)) {
                int r = ec.row;
                int c = ec.col;

                String text = "?";
                float width, height;
                letterPaint.getTextBounds(text, 0, text.length(), letterPaintBounds);
                width = letterPaint.measureText(text);
                height = letterPaintBounds.height();

                canvas.drawText(text, (c * cellSize) + ((cellSize - width) / 2),
                        (r * cellSize + cellSize) - ((cellSize - height) / 2),
                        letterPaint);
            }

        }
    }

    private  void colorCell(Canvas canvas, int r, int c){

        if(solver.getSelectedColumn() != -1 && solver.getSelectedRow() != -1){
            canvas.drawRect((c-1)*cellSize,0,c*cellSize,
                    cellSize*9, cellsHighlightColorPaint);
            canvas.drawRect(0,(r-1)*cellSize,cellSize*9,
                    r*cellSize, cellsHighlightColorPaint);
            canvas.drawRect((c-1)*cellSize,(r-1)*cellSize,c*cellSize,
                    r*cellSize, cellsHighlightColorPaint);
            if(solver.wasSolved){
                letterPaint.setColor(letterColorSolve);
                String text = "";
                float width, height;
                letterPaint.getTextBounds(text, 0, text.length(), letterPaintBounds);
                width = letterPaint.measureText(text);
                height = letterPaintBounds.height();
                canvas.drawText(text, ((c-1) * cellSize) + ((cellSize - width) / 2),
                        ((r-1) * cellSize + cellSize) - ((cellSize - height) / 2),
                        letterPaint);
                text = "" + solver.board[r-1][c-1];
                letterPaint.getTextBounds(text, 0, text.length(), letterPaintBounds);
                width = letterPaint.measureText(text);
                height = letterPaintBounds.height();
                canvas.drawText(text, ((c-1) * cellSize) + ((cellSize - width) / 2),
                        ((r-1) * cellSize + cellSize) - ((cellSize - height) / 2),
                        letterPaint);
                solver.revelSet.add(new EmptyCell(r-1,c-1));
            }

        }
        invalidate();
    }
    private void drawThickLine(){

        //paint brash
        boardColorPaint.setStyle(Paint.Style.STROKE);
        boardColorPaint.setStrokeWidth(10);
        //paint bucket
        boardColorPaint.setColor(boardColor);
    }

    private void drawThinLine(){

        //paint brash
        boardColorPaint.setStyle(Paint.Style.STROKE);
        boardColorPaint.setStrokeWidth(4);
        //paint bucket
        boardColorPaint.setColor(boardColor);
    }

    private void drawBoard(Canvas canvas){

        for(int c = 0; c < 10; c++) {
            if (c % 3 == 0) {
                drawThickLine();
            } else {
                drawThinLine();
            }
            canvas.drawLine(cellSize * c, 0, cellSize * c,
                    getWidth(), boardColorPaint);
        }
        for(int r = 0; r < 10; r++){
            if(r % 3 == 0){
                drawThickLine();
            }
            else {
                drawThinLine();
            }
            canvas.drawLine(0, cellSize * r, getWidth(),
                    cellSize * r, boardColorPaint);
        }
    }

    public Solver getSolver(){
        return this.solver;
    }

}

