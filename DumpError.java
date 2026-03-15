import java.net.*;
import java.io.*;
import java.util.regex.*;

public class DumpError {
    public static void main(String[] args) throws Exception {
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        HttpURLConnection con = (HttpURLConnection) new URL("http://localhost:8080/login").openConnection();
        con.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) content.append(inputLine);
        in.close();

        Matcher m = Pattern.compile("name=\"_csrf\"\\s+value=\"([^\"]+)\"").matcher(content.toString());
        m.find();
        String csrf = m.group(1);

        String postData = "username=student1&password=123&_csrf=" + csrf;
        con = (HttpURLConnection) new URL("http://localhost:8080/login").openConnection();
        con.setRequestMethod("POST");
        con.setInstanceFollowRedirects(false);
        con.setDoOutput(true);
        con.getOutputStream().write(postData.getBytes());
        System.out.println("Login POST status: " + con.getResponseCode());
        System.out.println("Redirect to: " + con.getHeaderField("Location"));
        con.getInputStream().close();

        con = (HttpURLConnection) new URL("http://localhost:8080/student/dashboard").openConnection();
        con.setRequestMethod("GET");
        int status = con.getResponseCode();
        System.out.println("Status: " + status);
        InputStream is = status >= 400 ? con.getErrorStream() : con.getInputStream();
        if (is != null) {
            in = new BufferedReader(new InputStreamReader(is));
            while ((inputLine = in.readLine()) != null) System.out.println(inputLine);
            in.close();
        }
    }
}
