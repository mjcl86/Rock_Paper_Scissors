package org.academiadecodigo.argicultores;

import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.menu.MenuInputScanner;
import org.academiadecodigo.bootcamp.scanners.string.StringInputScanner;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public final static int DEFAULT_PORT = 6660;
    private Vector<User> users = new Vector<>();
    private User user;

    public void start() {

        System.out.println("DEBUG: Server instance is : " + this);

        try {
            ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT);
            ExecutorService fixedPool = Executors.newFixedThreadPool(2);

            while (users.size() < 2) {

                Socket userSocket = serverSocket.accept();
                user = new User(userSocket);
                fixedPool.submit(user);
                users.addElement(user);

                System.out.println("player. " + users.size() + " connected.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void gameWin() throws IOException {

        if (users.get(0).userHand == users.get(1).userHand) {
            try {
                notifyAll(users.get(0).name + " plays " + users.get(0).userHand + " && " + users.get(1).name + " plays " + users.get(1).userHand + "\n" + "==>  DRAW");
                System.exit(1);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        User winner = users.get(0);

        switch (users.get(0).userHand) {
            case 1:

                if (users.get(1).userHand == 2) {
                    winner = users.get(1);
                    notifyAll(users.get(0).name + " plays " + users.get(0).userHand + " && " + users.get(1).name + " plays " + users.get(1).userHand + "\n" + "==>  " + winner.name + " win!");
                    System.exit(1);
                }
                break;

            case 2:

                if (users.get(1).userHand == 3) {
                    winner = users.get(1);
                    notifyAll(users.get(0).name + " plays " + users.get(0).userHand + " && " + users.get(1).name + " plays " + users.get(1).userHand + "\n" + "==>  " + winner.name + " win!");
                    System.exit(1);
                }
                break;

            case 3:
                if (users.get(1).userHand == 1) {
                    winner = users.get(1);
                    notifyAll(users.get(0).name + " plays " + users.get(0).userHand + " && " + users.get(1).name + " plays " + users.get(1).userHand + "\n" + "==>  " + winner.name + " win!");
                    System.exit(1);
                }
                break;
        }

        if (winner == users.get(0)) {
            notifyAll(users.get(0).name + " plays " + users.get(0).userHand + " && " + users.get(1).name + " plays " + users.get(1).userHand + "\n" + "==>  " + winner.name + " win!");
            System.exit(1);
        }

    }


    public void notifyAll(String str) throws IOException {
        for (User user : users) {
            PrintStream outUser = new PrintStream(user.userSocket.getOutputStream());
            outUser.println(str);
            outUser.flush();
        }
    }

    public class User implements Runnable {

        private String name = "";
        private int wins = 0;
        private Socket userSocket;
        private Integer userHand;
        public boolean play = false;
        Prompt prompt = null;
        String[] options = {"ROCK", "PAPER", "SCISSORS"};
        private BufferedReader in;
        private PrintStream printStream;

        public User(Socket userSocket) throws IOException {
            this.userSocket = userSocket;
            in = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
        }

        @Override
        public synchronized void run() {

            try {
                printStream = new PrintStream(userSocket.getOutputStream());
                prompt = new Prompt(userSocket.getInputStream(), new PrintStream(userSocket.getOutputStream()));
                StringInputScanner userName = new StringInputScanner();
                userName.setMessage("Insert your name: " + "\n");
                name = prompt.getUserInput(userName);

                //Thread.currentThread().setName(name);

                while (true) {
                    if (users.size() > 1) {
                        if (!users.get(0).name.equals("") && !users.get(1).name.equals("")) {
                            break;
                        }
                    }
                }

                printStream.println("\n" +
                        "   ======================================================" + "\n" +
                        "           Welcome to Rock Paper Scissors " + name + "\n" +
                        "   ======================================================" + "\n" +
                        "\n" +
                        "                   ╱╱╭╮╱╱╭╮╱╱╱╱╱╱╱╭━━━╮" + "\n" +
                        "                   ╱╱┃┃╱╱┃┃╱╱╱╱╱╱╱┃╭━╮┃" + "\n" +
                        "                   ╱╱┃┣━━┫┃╭┳━━┳━╮┃╰━╯┣━━╮" + "\n" +
                        "                   ╭╮┃┃╭╮┃╰╯┫┃━┫╭╮┫╭━━┫╭╮┃" + "\n" +
                        "                   ┃╰╯┃╰╯┃╭╮┫┃━┫┃┃┃┃╱╱┃╰╯┃" + "\n" +
                        "                   ╰━━┻━━┻╯╰┻━━┻╯╰┻╯╱╱╰━━╯" + "\n" +
                        "   ======================================================" + "\n");
                System.out.println(name);
                hand();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public synchronized void hand() throws IOException {

            MenuInputScanner scanner = new MenuInputScanner(options);
            scanner.setMessage("Choose your hand");
            userHand = prompt.getUserInput(scanner);
            //Thread.currentThread().setName(userHand.toString());
            System.out.println(name + " " + options[userHand - 1]);
            System.out.println(users.get(0).name);
            System.out.println(users.get(1).name);
            while (users.get(0).userHand > 0 && users.get(1).userHand > 0) {
                gameWin();
                users.get(0).userHand = 0;
                users.get(1).userHand = 0;

                break;
            }
        }

        public void win() {
            wins++;
        }
    }
}

