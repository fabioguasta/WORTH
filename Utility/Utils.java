package Utility;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.List;

public class Utils {
    
    //Converte un array di 4 elementi in dodici caratteri decimali
    private static String format(final int[] octets) {
        final StringBuilder str = new StringBuilder();
        for (int i =0; i < octets.length; ++i){
            str.append(octets[i]);
            if (i != octets.length - 1) {
                str.append(".");
            }
        }
        return str.toString();
    }

    public static String randomMulticastipv4(){
        int[] ipArray = new int[4];
        ipArray[0] = ThreadLocalRandom.current().nextInt(224, 240);
        ipArray[1] = ThreadLocalRandom.current().nextInt(0, 256);
        ipArray[2] = ThreadLocalRandom.current().nextInt(0, 256);
        ipArray[3] = ThreadLocalRandom.current().nextInt(0, 256);
        return format(ipArray);
    }

}
