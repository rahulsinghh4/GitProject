package gitlet;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Rahul Singh
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    private static Scanner _reader = new Scanner(System.in);
    private String[] _userInputs;

    public static void main(String... args) throws IOException, ParseException {
        // FILL THIS IN
        String[] _userInputs = {"init", "add", "commit", "rm", "log", "global-log", "checkout", "find", "status", "branch", "rm-branch", "merge"};
        Repo temp = new Repo();
        int lenArgs = args.length;
        if (lenArgs == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        if (args[0].equals("init")) {
            if (checkArgs(1, args)) {
                temp.init();
            }
        }

        File repo = new File(".gitlet");
        if (!repo.exists()) {
            System.exit(0);
        }
        switch (args[0]) {

            case "add":
                if (checkArgs(2, args)) {
                    temp.add(args[1]);
                }
                break;
            case "rm":
                if (checkArgs(2, args)) {
                    temp.rm(args[1]);
                }
                break;
            case "commit":
                if (args.length == 2) {
                    temp.commit(args[1]);
                } else if (args.length == 1) {
                    temp.commit("");
                } else {
                    checkArgs(2, args);
                }
                break;
            case "checkout":
                if (args.length == 3) {
                    if (args[1].equals("--")) {
                        temp.checkoutFile(args[2], "head");
                    } else {
                        checkArgs(2, args);
                    }
                } else if (args.length == 4) {
                    if (args[2].equals("--")) {
                        temp.checkoutFile(args[3], args[1]);
                    } else {
                        checkArgs(3, args);
                    }
                } else if (args.length == 2) {
                    temp.checkoutBranch(args[1]);
                } else {
                    System.out.println("Incorrect Operands");
                }
                break;
            case "log":
                temp.log();
        }

    }
    private static boolean checkArgs(int length, String... args) {
        if (args.length == length) {
            return true;
        } else {
            System.out.println("Incorrect Operands");
            return false;
        }
    }

    private static String getNextInput() {
        return _reader.next();
    }

    private boolean inputHasNext() {
        return _reader.hasNext();
    }

    private  void newScanner() {
        _reader = new Scanner(System.in);
    }

}
