import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

/**
 * A simple HTTP server for the loop tuning web page
 */
public abstract class WebServer {

    /**
     * Starts a web HTTP server with routes to the loop tuning page
     */
    public WebServer() {
        try {
            // Create an HttpServer server on port 8080
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 8);

            // Create a static path to the loop-tuner.html file
            httpServer.createContext("/", httpExchange -> {
                byte[] response = readAllBytes(WebServer.class.getResourceAsStream("loop-tuner.html"));
                httpExchange.sendResponseHeaders(200, response.length);
                httpExchange.getResponseBody().write(response);
                httpExchange.close();
            });

            // Create a request to get the current value for a tuning parameter
            httpServer.createContext("/get", httpExchange -> {
                byte[] response = String.valueOf(getValue(new String(readAllBytes(httpExchange.getRequestBody())))).getBytes();
                httpExchange.sendResponseHeaders(200, response.length);
                httpExchange.getResponseBody().write(response);
                httpExchange.close();
            });

            // Create a request to set the value for a tuning parameter
            httpServer.createContext("/set", httpExchange -> {
                String data = new String(readAllBytes(httpExchange.getRequestBody()));
                String[] parts = data.split(":");
                setValue(parts[0], Integer.parseInt(parts[1]));
                httpExchange.sendResponseHeaders(200, 0);
                httpExchange.close();
            });

            // Start the web server
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used by the web server to get a loop tuning value
     *
     * @param key the loop tuning value to return
     * @return the current value associated with the loop tuning key
     */
    public abstract int getValue(String key);


    /**
     * Used by the web server to set a loop tuning value
     *
     * @param key the value to set
     * @param value what to set the key to
     */
    public abstract void setValue(String key, int value);

    /**
     * Reads all bytes from an {@code InputStream}
     *
     * @param inputStream the {@code InputStream} to read all the bytes from
     * @return all the bytes from the {@code InputStream}
     * @throws IOException if there is an issue with the {@code InputStream}
     */
    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        final int bufLen = 4 * 0x400;
        byte[] buf = new byte[bufLen];
        int readLen;
        IOException exception = null;

        try {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
                    outputStream.write(buf, 0, readLen);

                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            exception = e;
            throw e;
        } finally {
            if (exception == null) inputStream.close();
            else try {
                inputStream.close();
            } catch (IOException e) {
                exception.addSuppressed(e);
            }
        }
    }
}
