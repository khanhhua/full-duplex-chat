import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {
    public static void main(String[] args) throws InterruptedException, IOException {
        if (args.length == 0) return;

        Thread worker;
        final UserInput input;
        if ("host".equals(args[0])) {
            System.out.println("Starting server app...");

            Server server = new Server();
            server.listen(8888);

            worker = server;
            input = server;
        } else if ("client".equals(args[0])) {
            System.out.println("Starting client app...");

            Client client = new Client();
            client.connect(8888);

            worker = client;
            input = client;
        } else {
            System.exit(1);
            return;
        }

        worker.start();
        service.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("Consuming user input..");

                String line;
                while (true) {
                    try {
                        line = Keyboard.readline();
                        input.write(line);

                        System.out.println("Echo: " + line);
                    } catch (IOException e) {
                        System.err.println("Could not write");
                    }
                }
            }
        });

        worker.join();
    }

    static ExecutorService service = Executors.newSingleThreadExecutor();
}
