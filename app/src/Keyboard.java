import java.io.*;

public class Keyboard {
    static BufferedReader buffer;
    static {
        InputStreamReader bis = new InputStreamReader(System.in);
        buffer = new BufferedReader(bis);
    }

    public static String readline() throws IOException {
        return buffer.readLine();
    }
}
