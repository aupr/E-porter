package com.sincos;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        String input;
        Encryptor encryptor = new Encryptor("FR38_SH1PP2NG_N8W");

        System.out.println("Write E for encrypt or D for decrypt:");
        input = scan.nextLine();

        if (input.equals("E")){

            System.out.println("Enter Additional title to show on top bar: ");
            input = scan.nextLine();
            System.out.println("Enter Device ID: ");
            input = input + "<>" + scan.nextLine();
            System.out.println("Enter Vendor Info: ");
            input = input + "<>" + scan.nextLine();
            input = encryptor.encrypt(input);
            System.out.println("License Code:\n"+input+"\n\n");

        } else if (input.equals("D")){
            System.out.println("Enter license code: ");
            input = scan.nextLine();

            String decodedText = encryptor.decrypt(input);
            System.out.println("Decoded output:\n"+decodedText+"\n\n");
        } else {
            System.out.println("Invalid input");
        }
    }
}
