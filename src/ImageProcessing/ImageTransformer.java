package ImageProcessing;

public class ImageTransformer {

    private static float gaussFunction(int x, int y, float sigma) {
        double pow_denom = (double)sigma;
        pow_denom = 2 * pow_denom * pow_denom;
        double pow = -(double)(x*x + y*y) / pow_denom;
        return (float)(Math.exp(pow) / (2 * Math.PI * sigma));
    }

    private static float[][] buildGaussKernel(int kernel_size, float sigma) {
        if (kernel_size % 2 == 0) {
            kernel_size++;
        }
        int center = kernel_size / 2;
        float[][] kernel = new float[kernel_size][kernel_size];
        for (int i = 0; i < kernel_size; ++i) {
            for (int j = 0; j < kernel_size; ++j) {
                kernel[i][j] = gaussFunction(i - center, j - center, sigma);
            }
        }
        return kernel;
    }

    private static float[][] buildSobelKernel(int axis) {
        float[][] kernel = new float[3][3];
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

    private static float[][] buildScharrKernel(int axis) {
        float[][] kernel = new float[3][3];
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

    private static float applyKernelToCell(float[][] image, float[][] kernel, int x, int y) {
        float res = 0;
        float kernel_sum = 0;
        float missed_sum = 0;
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

    private static float[][] applyKernel(float[][] image, float[][] kernel) {
        float[][] result = new float[image.length][image[0].length];
        for (int y = 0; y < result.length; ++y) {
            for (int x = 0; x < result[0].length; ++x) {
                result[y][x] = applyKernelToCell(image, kernel, x, y);
            }
        }
        return result;
    }

    public static float[][] gaussBlur(float[][] image, int kernel_size, float sigma) {
        float[][] kernel = buildGaussKernel(kernel_size, sigma);
        return applyKernel(image, kernel);
    }

    public static float[][] sobel(float[][] image, int axis) {
        float[][] kernel = buildSobelKernel(axis);
        return applyKernel(image, kernel);
    }

    public static float[][] scharr(float[][] image, int axis) {
        float[][] kernel = buildScharrKernel(axis);
        return applyKernel(image, kernel);
    }
}
