package ImageProcessing;

public class ImageTransformer {

    private static double gaussFunction(int x, int y, double sigma) {
        double pow_denom = sigma;
        pow_denom = 2 * pow_denom * pow_denom;
        double pow = -(double)(x*x + y*y) / pow_denom;
        return (Math.exp(pow) / (2 * Math.PI * sigma));
    }

    private static double[][] buildGaussKernel(int kernel_size, double sigma) {
        if (kernel_size % 2 == 0) {
            kernel_size++;
        }
        int center = kernel_size / 2;
        double[][] kernel = new double[kernel_size][kernel_size];
        for (int i = 0; i < kernel_size; ++i) {
            for (int j = 0; j < kernel_size; ++j) {
                kernel[i][j] = gaussFunction(i - center, j - center, sigma);
            }
        }
        return kernel;
    }

    private static double[][] buildSobelKernel(int axis) {
        double[][] kernel = new double[3][3];
        if (axis == 0) {
            kernel[0][0] = -1;
            kernel[0][1] = 0;
            kernel[0][2] = 1;
            kernel[1][0] = -2;
            kernel[1][1] = 0;
            kernel[1][2] = 2;
            kernel[2][0] = -1;
            kernel[2][1] = 0;
            kernel[2][2] = 1;
        }
        else {
            kernel[0][0] = -1;
            kernel[0][1] = -2;
            kernel[0][2] = -1;
            kernel[1][0] = 0;
            kernel[1][1] = 0;
            kernel[1][2] = 0;
            kernel[2][0] = 1;
            kernel[2][1] = 2;
            kernel[2][2] = 1;
        }
        return kernel;
    }

    private static double[][] buildScharrKernel(int axis) {
        double[][] kernel = new double[3][3];
        if (axis == 0) {
            kernel[0][0] = -3;
            kernel[0][1] = 0;
            kernel[0][2] = 3;
            kernel[1][0] = -10;
            kernel[1][1] = 0;
            kernel[1][2] = 10;
            kernel[2][0] = -3;
            kernel[2][1] = 0;
            kernel[2][2] = 3;
        }
        else {
            kernel[0][0] = -3;
            kernel[0][1] = -10;
            kernel[0][2] = -3;
            kernel[1][0] = 0;
            kernel[1][1] = 0;
            kernel[1][2] = 0;
            kernel[2][0] = 3;
            kernel[2][1] = 10;
            kernel[2][2] = 3;
        }
        return kernel;
    }

    private static double applyKernelToCell(double[][] image, double[][] kernel, int x, int y) {
        double res = 0;
        double kernel_sum = 0;
        double missed_sum = 0;
        int half_kernel_size = kernel.length / 2;
        for (int i = 0; i < kernel.length; ++i) {
            for (int j = 0; j < kernel[0].length; ++j) {
                int px = x - half_kernel_size + j;
                int py = y - half_kernel_size + i;
                if (px >= 0 && px < image[0].length && py >= 0 && py < image.length) {
                    res += image[py][px] * kernel[i][j];
                }
                else {
                    missed_sum += kernel[i][j];
                }
                kernel_sum += kernel[i][j];
            }
        }
        if (missed_sum == 0) {
            return res;
        }
        return res * kernel_sum / (kernel_sum - missed_sum);
    }

    private static double[][] applyKernel(double[][] image, double[][] kernel) {
        double[][] result = new double[image.length][image[0].length];
        for (int y = 0; y < result.length; ++y) {
            for (int x = 0; x < result[0].length; ++x) {
                result[y][x] = applyKernelToCell(image, kernel, x, y);
            }
        }
        return result;
    }

    public static double[][] gaussBlur(double[][] image, int kernel_size, double sigma) {
        double[][] kernel = buildGaussKernel(kernel_size, sigma);
        return applyKernel(image, kernel);
    }

    public static double[][] sobel(double[][] image, int axis) {
        double[][] kernel = buildSobelKernel(axis);
        return applyKernel(image, kernel);
    }

    public static double[][] scharr(double[][] image, int axis) {
        double[][] kernel = buildScharrKernel(axis);
        return applyKernel(image, kernel);
    }
}
