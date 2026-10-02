package com.group12.ripperdoc;

import com.group12.ripperdoc.api.ApiServer;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        ApiServer server = new ApiServer(port, Path.of("web"));
        server.start();
        System.out.println("Ripperdoc clinic running at http://localhost:" + port);
    }
}
