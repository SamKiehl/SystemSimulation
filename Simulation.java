import java.awt.Color;
public class Simulation {
    public static void main(String[] args) {
        double m = 5.0;  // [kg]
        double c = 5; // [Ns/m]
        double k = 300; // [N/m]

        double dt = 0.01; // [s]

        double[] num = {1};
        double[] den = {m, c, k};

        TransferFunction T_f = new TransferFunction(num, den);

        double[] f = new double[1000];
        for(int i = 0; i < f.length; i++) f[i] = 0;
        f[0] = 100;

        double[] x_t = T_f.simulate(f, dt);

        double[] t = new double[f.length];
        for(int i = 0; i < t.length; i++) t[i] = i*dt; 


        Utilities.printArr(x_t);
        System.out.println("\n\n");

        Plot fig1 = new Plot();
        fig1.plot();
        fig1.add(t, x_t, new Color(20, 180, 100), "x [m]");
        //fig1.add(t, f, new Color(180, 100, 20), "F [N]");
        fig1.xLabel("t [s]");
        fig1.title("Displacement versus Time");
    }
}
