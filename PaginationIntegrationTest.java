import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PaginationIntegrationTest {

    private static final String BASE_URL = "http://example.com/api/items";
    private static final int PAGE_SIZE = 10;

    public static void main(String[] args) {
        try {
            PaginationIntegrationTest test = new PaginationIntegrationTest();
            test.testPagination();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testPagination() throws Exception {
        int pageNumber = 1;
        List<String> allItems = new ArrayList<>();

        while (true) {
            String url = BASE_URL + "?page=" + pageNumber + "&size=" + PAGE_SIZE;
            HttpURLConnection connection = createConnection(url);
            String response = getResponse(connection);

            List<String> items = parseItems(response);
            if (items.isEmpty()) {
                break;
            }
            allItems.addAll(items);
            pageNumber++;
        }

        verifyPagination(allItems);
    }

    private HttpURLConnection createConnection(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        return connection;
    }

    private String getResponse(HttpURLConnection connection) throws Exception {
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    private List<String> parseItems(String response) {
        // Assume response is a JSON array of strings, e.g., ["item1", "item2", ...]
        // This is a simple parser for demonstration purposes.
        List<String> items = new ArrayList<>();
        String[] split = response.replace("[", "").replace("]", "").replace("\"", "").split(",");
        for (String item : split) {
            if (!item.trim().isEmpty()) {
                items.add(item.trim());
            }
        }
        return items;
    }

    private void verifyPagination(List<String> allItems) {
        // Perform verification logic to ensure all items are present and correct
        System.out.println("Total items retrieved: " + allItems.size());
        for (String item : allItems) {
            System.out.println("Item: " + item);
        }

        // Example assertion
        int expectedItemCount = 50; // Replace with your expected count
        if (allItems.size() != expectedItemCount) {
            throw new AssertionError("Pagination test failed: expected " + expectedItemCount + " items, but got " + allItems.size());
        }
    }
}
