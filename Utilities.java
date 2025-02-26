public class Utilities {

    public static String arrToString(double[] arr) {
        String output = "[";
        if(arr.length == 0) return output;


        for(int i = 0; i < arr.length; i++) {
            output += arr[i];
            if(arr.length > 1 && i != arr.length-1) output += ", ";
        }

        output += "]";
        return output;
    }

    public static void printArr(double[] arr) { System.out.println(arrToString(arr)); }
}