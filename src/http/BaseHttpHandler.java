package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    protected void sendText (HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(200, response.length);
        httpExchange.getResponseBody().write(response);
        httpExchange.close();
    }

    protected void sendNotFound (HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(404, response.length);
        httpExchange.getResponseBody().write(response);
        httpExchange.close();
    }

    protected void sendHasOverlaps (HttpExchange httpExchange, String text) throws IOException {
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(406, response.length);
        httpExchange.getResponseBody().write(response);
        httpExchange.close();
    }

    protected void internalServerError (HttpExchange httpExchange, String text) throws IOException {
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(500, response.length);
        httpExchange.getResponseBody().write(response);
        httpExchange.close();
    }
}