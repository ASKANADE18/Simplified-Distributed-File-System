/*
 * Group A
 * Members - Raj Bhinde (rmb76@njit.edu) 
             Ashwini Kanade (ak3374@njit.edu)  
             Arunav Mishra (am3945@njit.edu)  
             Danielle Scalera (dhs37@njit.edu)  
             Akash Deore (ad2386@njit.edu)  
 *                  
 */

import java.io.*;
import java.net.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class FileClient {
    private static final String[] availableServers = {
            "8080","8081","8082"
    };
    public static void main(String[] args) {


        // Getting port & hostname from user
        if (args.length == 0) {
            System.err.println("Please provide a valid port number as an argument.");
            return;
        }
        int port;
        String hostName = "localhost";
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number. Please provide a valid integer.");
            return;
        }

        boolean serverAvailable = false;
        for (String server : availableServers) {
            if (server.equals(String.valueOf(port))) {
                serverAvailable = true;
                break;
            }
        }

        if (!serverAvailable) {
            System.err.println("No server available for the specified port: " + port + ". Here is the list of available servers "  + String.join(",",availableServers));
            return;
        }
        
        try (
                // Socket Connection
                Socket socket = new Socket(hostName, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Scanner scanner = new Scanner(System.in)
        ) {

            System.out.println("Connected to the file server.");

            while (true) {
                System.out.print("Enter command (create/write/read/delete/exit): ");
                String command = scanner.nextLine();

                if (command.equalsIgnoreCase("exit")) {
                    System.out.println("Disconnecting from server.");
                    break;
                }

                out.println(command);
                String response;
                while ((response = in.readLine()) != null) {
                    System.out.println("Server response: " + response);
                    if (!in.ready()) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            // Catching exception when user disconnects FileServer
            System.err.println("Client exception, TRY ANOTHER SERVER " + e.getMessage());
            e.printStackTrace();
        }
    }
}
