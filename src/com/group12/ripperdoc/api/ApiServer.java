package com.group12.ripperdoc.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiServer {

    private final HttpServer server;
    private final ApiRouter router = new ApiRouter();
    private final Path webRoot;
    private final ExecutorService executor;

    public ApiServer(int port, Path webRoot) throws IOException {
        this.webRoot = webRoot.toAbsolutePath().normalize();
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.executor = Executors.newFixedThreadPool(8, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });
        server.createContext("/api", this::handleApi);
        server.createContext("/", this::handleStatic);
        server.setExecutor(executor);
    }

    public void start() {
        server.start();
    }

    public int getPort() {
        return server.getAddress().getPort();
    }

    public void stop() {
        server.stop(0);
        executor.shutdownNow();
    }

    private void handleApi(HttpExchange exchange) throws IOException {
        ApiResponse response = router.route(
                exchange.getRequestMethod(),
                exchange.getRequestURI().getPath(),
                query(exchange.getRequestURI().getRawQuery()));
        send(exchange, response.status(), response.contentType(), response.body().getBytes(StandardCharsets.UTF_8));
    }

    private void handleStatic(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.isEmpty() || path.equals("/")) {
            path = "/index.html";
        }
        Path file = webRoot.resolve(path.substring(1)).normalize();
        if (!file.startsWith(webRoot) || !Files.isRegularFile(file)) {
            send(exchange, 404, "text/plain; charset=utf-8", "404 - Not found".getBytes(StandardCharsets.UTF_8));
            return;
        }
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        send(exchange, 200, contentType(file.toString()), Files.readAllBytes(file));
    }

    private static Map<String, String> query(String rawQuery) {
        Map<String, String> params = new HashMap<>();
        if (rawQuery == null || rawQuery.isBlank()) {
            return params;
        }
        for (String pair : rawQuery.split("&")) {
            int equals = pair.indexOf('=');
            if (equals <= 0) {
                continue;
            }
            String key = URLDecoder.decode(pair.substring(0, equals), StandardCharsets.UTF_8);
            String value = URLDecoder.decode(pair.substring(equals + 1), StandardCharsets.UTF_8);
            params.put(key, value);
        }
        return params;
    }

    private static void send(HttpExchange exchange, int status, String contentType, byte[] body) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(status, body.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(body);
        }
    }

    private static String contentType(String name) {
        if (name.endsWith(".html")) {
            return "text/html; charset=utf-8";
        }
        if (name.endsWith(".css")) {
            return "text/css; charset=utf-8";
        }
        if (name.endsWith(".js")) {
            return "application/javascript; charset=utf-8";
        }
        if (name.endsWith(".svg")) {
            return "image/svg+xml";
        }
        if (name.endsWith(".json")) {
            return "application/json; charset=utf-8";
        }
        if (name.endsWith(".png")) {
            return "image/png";
        }
        return "application/octet-stream";
    }
}
