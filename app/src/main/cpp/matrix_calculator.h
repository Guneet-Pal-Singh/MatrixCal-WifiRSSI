#pragma once
#include <vector>
#include <string>

std::string matrixAdd(const std::vector<double>& a, const std::vector<double>& b, int rows, int cols);
std::string matrixSubtract(const std::vector<double>& a, const std::vector<double>& b, int rows, int cols);
std::string matrixMultiply(const std::vector<double>& a, const std::vector<double>& b, int rowsA, int colsA, int colsB);
std::string matrixDivide(const std::vector<double>& a, const std::vector<double>& b, int rows, int cols);
