#include "matrix_calculator.h"
#include <jni.h>
#include <Eigen/Dense>
#include <vector>
#include <sstream>
#include <string>

using namespace Eigen;

// Helper: Convert Eigen matrix to string (row-major, space-separated)
std::string matrixToString(const MatrixXd& mat) {
    std::ostringstream oss;
    for (int i = 0; i < mat.rows(); ++i) {
        for (int j = 0; j < mat.cols(); ++j) {
            oss << mat(i, j);
            if (j < mat.cols() - 1) oss << " ";
        }
        if (i < mat.rows() - 1) oss << ";";
    }
    return oss.str();
}

// Helper: Convert flat vector to Eigen matrix
MatrixXd vectorToMatrix(const std::vector<double>& v, int rows, int cols) {
    MatrixXd m(rows, cols);
    for (int i = 0; i < rows; ++i)
        for (int j = 0; j < cols; ++j)
            m(i, j) = v[i * cols + j];
    return m;
}

// Matrix addition
std::string matrixAdd(const std::vector<double>& a, const std::vector<double>& b, int rows, int cols) {
    MatrixXd mA = vectorToMatrix(a, rows, cols);
    MatrixXd mB = vectorToMatrix(b, rows, cols);
    MatrixXd result = mA + mB;
    return matrixToString(result);
}

// Matrix subtraction
std::string matrixSubtract(const std::vector<double>& a, const std::vector<double>& b, int rows, int cols) {
    MatrixXd mA = vectorToMatrix(a, rows, cols);
    MatrixXd mB = vectorToMatrix(b, rows, cols);
    MatrixXd result = mA - mB;
    return matrixToString(result);
}

// Matrix multiplication
std::string matrixMultiply(const std::vector<double>& a, const std::vector<double>& b, int rowsA, int colsA, int colsB) {
    MatrixXd mA = vectorToMatrix(a, rowsA, colsA);
    MatrixXd mB = vectorToMatrix(b, colsA, colsB);
    MatrixXd result = mA * mB;
    return matrixToString(result);
}

// Matrix division (A * B^-1)
std::string matrixDivide(const std::vector<double>& a, const std::vector<double>& b, int rows, int cols) {
    MatrixXd mA = vectorToMatrix(a, rows, cols);
    MatrixXd mB = vectorToMatrix(b, rows, cols);
    if (mB.determinant() == 0) {
        return "Error: Division by non-invertible matrix";
    }
    MatrixXd result = mA * mB.inverse();
    return matrixToString(result);
}

// JNI Bindings

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_assignment3_1q1_MatrixCalculatorJNI_addMatrix(JNIEnv* env, jobject, jdoubleArray a, jdoubleArray b, jint rows, jint cols) {
    jsize size = env->GetArrayLength(a);
    std::vector<double> va(size), vb(size);
    env->GetDoubleArrayRegion(a, 0, size, va.data());
    env->GetDoubleArrayRegion(b, 0, size, vb.data());
    std::string result = matrixAdd(va, vb, rows, cols);
    return env->NewStringUTF(result.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_assignment3_1q1_MatrixCalculatorJNI_subtractMatrix(JNIEnv* env, jobject, jdoubleArray a, jdoubleArray b, jint rows, jint cols) {
    jsize size = env->GetArrayLength(a);
    std::vector<double> va(size), vb(size);
    env->GetDoubleArrayRegion(a, 0, size, va.data());
    env->GetDoubleArrayRegion(b, 0, size, vb.data());
    std::string result = matrixSubtract(va, vb, rows, cols);
    return env->NewStringUTF(result.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_assignment3_1q1_MatrixCalculatorJNI_multiplyMatrix(JNIEnv* env, jobject, jdoubleArray a, jdoubleArray b, jint rowsA, jint colsA, jint colsB) {
    jsize sizeA = env->GetArrayLength(a);
    jsize sizeB = env->GetArrayLength(b);
    std::vector<double> va(sizeA), vb(sizeB);
    env->GetDoubleArrayRegion(a, 0, sizeA, va.data());
    env->GetDoubleArrayRegion(b, 0, sizeB, vb.data());
    std::string result = matrixMultiply(va, vb, rowsA, colsA, colsB);
    return env->NewStringUTF(result.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_assignment3_1q1_MatrixCalculatorJNI_divideMatrix(JNIEnv* env, jobject, jdoubleArray a, jdoubleArray b, jint rows, jint cols) {
    jsize size = env->GetArrayLength(a);
    std::vector<double> va(size), vb(size);
    env->GetDoubleArrayRegion(a, 0, size, va.data());
    env->GetDoubleArrayRegion(b, 0, size, vb.data());
    std::string result = matrixDivide(va, vb, rows, cols);
    return env->NewStringUTF(result.c_str());
}
