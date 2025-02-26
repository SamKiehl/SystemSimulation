public class TransferFunction {
    private double[] num; // Coefficients of TF numerator coefficients in decreasing degree of s
    private double[] den; // . . .              denominator   . . .

    public TransferFunction(double[] num, double[] den) {
        this.num = num;
        this.den = den;
    }

    public double[] getNum() { return this.num; }
    public void setNum(double[] num) { this.num = num; }

    public double[] getDen() { return this.den; }
    public void setDen(double[] den) { this.den = den; }

    public static TransferFunction lowPassFilter(double omega_c) {
        double[] numL = {omega_c};
        double[] denL = {1, omega_c};
        TransferFunction lpf = new TransferFunction(numL, denL);
        return lpf;
    }

    public static TransferFunction highPassFilter(double omega_c) {
        double[] numL = {1, 0};
        double[] denL = {1, omega_c};
        TransferFunction lpf = new TransferFunction(numL, denL);
        return lpf;
    }

    public double[] simulate(double[] input, double dt) {
        int n = den.length - 1; // Order of the system
        int m = num.length - 1;

        // Pad numerator with zeros if necessary
        if (m < n) {
            double[] paddedNum = new double[n + 1];
            System.arraycopy(num, 0, paddedNum, n - m, num.length);
            num = paddedNum;
        }

        // State-space matrices
        double[][] A = new double[n][n];
        double[] B = new double[n];
        double[] C = new double[n];
        double D = num[0] / den[0];

        // Construct A matrix (companion matrix)
        for (int i = 0; i < n - 1; i++) {
            A[i][i + 1] = 1.0;
        }
        for (int i = 0; i < n; i++) {
            A[n - 1][i] = -den[n - i] / den[0];
        }

        // Construct B matrix
        B[n - 1] = 1.0 / den[0];

        // Construct C matrix
        for (int i = 0; i < n; i++) {
            C[i] = num[n - i] - D * den[n - i];
        }

        // Initialize state vector
        double[] x = new double[n];

        // Initialize output array
        double[] output = new double[input.length];

        // Simulate the system using Runge-Kutta 4th order
        for (int i = 0; i < input.length; i++) {
            // Compute output at current time step
            output[i] = 0.0;
            for (int j = 0; j < n; j++) {
                output[i] += C[j] * x[j];
            }
            output[i] += D * input[i];

            // Runge-Kutta 4th order integration for next time step
            double[] k1 = computeDerivatives(A, B, x, input[i]);
            double[] k2 = computeDerivatives(A, B, addVectors(x, scaleVector(k1, dt / 2)), input[i]);
            double[] k3 = computeDerivatives(A, B, addVectors(x, scaleVector(k2, dt / 2)), input[i]);
            double[] k4 = computeDerivatives(A, B, addVectors(x, scaleVector(k3, dt)), input[i]);

            // Update state
            for (int j = 0; j < n; j++) {
                x[j] += (k1[j] + 2 * k2[j] + 2 * k3[j] + k4[j]) * dt / 6;
            }
        }

        return output;
    }

    private double[] computeDerivatives(double[][] A, double[] B, double[] x, double u) {
        int n = x.length;
        double[] dx = new double[n];
        for (int i = 0; i < n; i++) {
            dx[i] = 0.0;
            for (int j = 0; j < n; j++) {
                dx[i] += A[i][j] * x[j];
            }
            dx[i] += B[i] * u;
        }
        return dx;
    }

    private double[] addVectors(double[] a, double[] b) {
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] + b[i];
        }
        return result;
    }

    private double[] scaleVector(double[] a, double scale) {
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] * scale;
        }
        return result;
    }

    public String toString() {
        String output = "", numS = "", denS = "";
        int nLen = this.num.length;
        int dLen = this.den.length;

        for(int i = 0; i < nLen; i++) {
            if(this.num[i] == 0) continue;
            if(i != 0) numS += "+ ";
            numS += this.num[i];
            if(i != nLen-1) numS += "s^" + (nLen-i-1) + " ";
        } 
        for(int i = 0; i < dLen; i++) {
            if(this.den[i] == 0) continue;
            if(i != 0) denS += "+ ";
            denS += this.den[i];
            if(i != dLen-1) denS += "s^" + (dLen-i-1) + " ";
        }
        
        output += "\n" + numS;
        if(dLen == 0) return output;

        output += "\n";
        for(int i = 0; i < (numS.length() > denS.length() ? numS.length() : denS.length()); i++) output += "-";
        output += "\n" + denS + "\n";

        return output;

    }

}