public class Main {
    public static void main(String[] args) {
        // Define a transfer function: H(s) = (s + 1) / (s^2 + 2s + 1)
        double[] num = {1, 1}; // s + 1
        double[] den = {1, 2, 1}; // s^2 + 2s + 1

        TransferFunction system = new TransferFunction(num, den);

        // Define an input signal (e.g., step input)
        double[] input = new double[100];
        for (int i = 0; i < input.length; i++) {
            input[i] = 1.0; // Step input
        }

        // Simulate the system
        double dt = 0.01; // Time step
        double[] output = system.simulate(input, dt);

        // Print the output
        for (double value : output) {
            System.out.println(value);
        }
    }
}