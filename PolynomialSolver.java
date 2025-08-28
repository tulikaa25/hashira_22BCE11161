import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class PolynomialSolver {

    public static void main(String[] args) {
        try {
            // 1. Read the Test Case (Input) from a separate JSON file
            String content = new String(Files.readAllBytes(Paths.get("roots.json")));
            JSONObject json = new JSONObject(content);

            int n = json.getJSONObject("keys").getInt("n");
            int k = json.getJSONObject("keys").getInt("k");

            Map<BigInteger, BigInteger> roots = new HashMap<>();

            // 2. Decode the Y Values
            for (String key : json.keySet()) {
                if (!key.equals("keys")) {
                    JSONObject rootData = json.getJSONObject(key);
                    BigInteger x = new BigInteger(key);
                    String value = rootData.getString("value");
                    int base = Integer.parseInt(rootData.getString("base"));
                    BigInteger y = new BigInteger(value, base);
                    roots.put(x, y);
                }
            }

            System.out.println("Decoded Roots: " + roots);

            // 3. Find the Secret (C) - This part will be implemented using Lagrange Interpolation
            // The secret 'C' is typically the constant term of the polynomial, P(0).
            // We need 'k' points to reconstruct a polynomial of degree 'k-1'.
            // The problem states k = m + 1, where m is the degree.
            // So, we need 'k' points to find the polynomial.

            // Extract the required 'k' points for interpolation
            // The problem statement doesn't specify which 'k' points to use if n > k.
            // For simplicity, we'll use the first 'k' points available in the map.
            List<Map.Entry<BigInteger, BigInteger>> kRootsList = new ArrayList<>();
            int count = 0;
            for (Map.Entry<BigInteger, BigInteger> entry : roots.entrySet()) {
                if (count < k) {
                    kRootsList.add(entry);
                    count++;
                } else {
                    break;
                }
            }

            System.out.println("Roots used for interpolation (k=" + k + "): " + kRootsList);

            // Lagrange Interpolation to find P(0)
            BigInteger secretC = lagrangeInterpolation(kRootsList, BigInteger.ZERO);
            System.out.println("The Secret (C) is: " + secretC);


        } catch (IOException e) {
            System.err.println("Error reading roots.json: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Lagrange Interpolation method using BigInteger
    public static BigInteger lagrangeInterpolation(List<Map.Entry<BigInteger, BigInteger>> points, BigInteger x_target) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < points.size(); i++) {
            BigInteger x_i = points.get(i).getKey();
            BigInteger y_i = points.get(i).getValue();

            BigInteger term = y_i;
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < points.size(); j++) {
                if (i != j) {
                    BigInteger x_j = points.get(j).getKey();
                    numerator = numerator.multiply(x_target.subtract(x_j));
                    denominator = denominator.multiply(x_i.subtract(x_j));
                }
            }
            // term = y_i * (numerator / denominator)
            // To avoid floating point, we perform division at the end or use modular inverse if working in a finite field.
            // For this problem, assuming exact division is possible or we need to handle fractions.
            // Since the problem implies a polynomial with integer coefficients, the result should be an integer.
            // We'll perform division as BigInteger.divide, which handles exact division for integers.
            // If the result is not an integer, this approach might need adjustment (e.g., using BigDecimal or a modular arithmetic approach).
            // For now, assuming the problem implies integer results.
            term = term.multiply(numerator).divide(denominator);
            result = result.add(term);
        }
        return result;
    }
}
