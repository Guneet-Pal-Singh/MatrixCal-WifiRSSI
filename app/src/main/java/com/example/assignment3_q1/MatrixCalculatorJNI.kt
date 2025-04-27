package com.example.assignment3_q1

object MatrixCalculatorJNI {
    init {
        System.loadLibrary("matrix_calculator")
    }

    external fun addMatrix(a: DoubleArray, b: DoubleArray, rows: Int, cols: Int): String
    external fun subtractMatrix(a: DoubleArray, b: DoubleArray, rows: Int, cols: Int): String
    external fun multiplyMatrix(a: DoubleArray, b: DoubleArray, rowsA: Int, colsA: Int, colsB: Int): String
    external fun divideMatrix(a: DoubleArray, b: DoubleArray, rows: Int, cols: Int): String
}
