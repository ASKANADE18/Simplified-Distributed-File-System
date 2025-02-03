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
import java.util.Arrays;

public class FileServer {
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
        try {
            port = Integer.parseInt(args[0]);
            String portStr = String.valueOf(port);
            if (Arrays.asList(availableServers).contains(portStr)) {
                System.out.println("Port " + port + " is available in available Servers. ");
            } else {
                System.out.println("Port " + port + " is not available in available Servers. Here is the list of available servers - " + String.join(",",availableServers));
                return;
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number. Please provide a valid integer.");
            return;
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("File server is running on port " + port + ". Make sure to use port " + port + " in your File Client.");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        ) {
            String clientCommand;

            while ((clientCommand = in.readLine()) != null) {
                String[] commandParts = clientCommand.split(" ", 2);
                String command = commandParts[0].toLowerCase();

                switch (command) {
                    case "create":
                        if (commandParts.length < 2) {
                            out.println("Error: No filename specified.");
                            break;
                        }
                        createFile(commandParts[1], out);
                        break;

                    case "write":
                        if (commandParts.length < 2 || !commandParts[1].contains(" ")) {
                            out.println("Error: No content or filename specified.");
                            break;
                        }
                        String[] writeParts = commandParts[1].split(" ", 2);
                        writeFile(writeParts[0], writeParts[1], out);
                        break;

                    case "read":
                        if (commandParts.length < 2) {
                            out.println("Error: No filename specified.");
                            break;
                        }
                        readFile(commandParts[1], out);
                        break;

                    case "delete":
                        if (commandParts.length < 2) {
                            out.println("Error: No filename specified.");
                            break;
                        }
                        deleteFile(commandParts[1], out);
                        break;

                    default:
                        out.println("Error: Invalid command.");
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("ClientHandler exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // creating file with user specified file name and file type
    private void createFile(String fileName, PrintWriter out) {
        String[] allowedExtensions = {".txt", ".jpg", ".png", ".bin"};
        boolean isAllowed = false;

        for (String ext : allowedExtensions) {
            if (fileName.toLowerCase().endsWith(ext)) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            out.println("Error: Invalid file type. Only .txt, .jpg, .png, and .bin files are allowed.");
            return;
        }

        try {
            File directory = new File("server_files");
            if (!directory.exists()) {
                directory.mkdir();
            }

            File newFile = new File(directory, fileName);
            if (newFile.createNewFile()) {
                out.println("File created: " + newFile.getAbsolutePath());
            } else {
                out.println("Error: File already exists.");
            }
        } catch (IOException e) {
            out.println("Error: Could not create file.");
        }
    }
    // write the file specified by the user 
    private void writeFile(String fileName, String content, PrintWriter out) {
        try {
            File directory = new File("server_files");
            File file = new File(directory, fileName);

            if (!file.exists()) {
                out.println("Error: File does not exist.");
                return;
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(content);
                writer.newLine();
                out.println("Content written to file: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            out.println("Error: Could not write to file.");
        }
    }
    // read the file specified by the user 
    private void readFile(String fileName, PrintWriter out) {
        try {
            File directory = new File("server_files");
            File file = new File(directory, fileName);

            if (!file.exists()) {
                out.println("Error: File does not exist.");
                return;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                out.println("File contents:");
                while ((line = reader.readLine()) != null) {
                    out.println(line);
                }
            }
        } catch (IOException e) {
            out.println("Error: Could not read file.");
        }
    }
    // delete the file specified by the user 
    private void deleteFile(String fileName, PrintWriter out) {
        File directory = new File("server_files");
        File file = new File(directory, fileName);

        if (!file.exists()) {
            out.println("Error: File does not exist.");
            return;
        }

        if (file.delete()) {
            out.println("File deleted: " + file.getAbsolutePath());
        } else {
            out.println("Error: Could not delete file.");
        }
    }
}