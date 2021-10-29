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

    public static byte[] serialize(Object obj) throws IOException{
        ByteArrayOutputStream out= new ByteArrayOutputStream();
        ObjectOutputStream o= new ObjectOutputStream(out);
        o.writeObject(obj);
        return out.toByteArray();
    }

    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException{
        ByteArrayInputStream in= new ByteArrayInputStream(data);
        ObjectInputStream is= new ObjectInputStream(in);
        return is.readObject();
    }

    public static byte[] toByteArray(List<Byte> lst){
        byte[] ret= new byte[lst.size()];
        for(int i=0; i<lst.size(); i++)
            ret[i]=lst.get(i);
        return ret;
    }

    public static String byteToBase64(byte[] b){
        Encoder e= Base64.getEncoder();
        return new String(e.encode(b), StandardCharsets.ISO_8859_1);
    }

    public static byte[] base64ToByte (String str) throws IOException{
        Decoder dec= Base64.getDecoder();
        return dec.decode(str);
    }

    public static String sha512 (String msg, String saltKey){
        MessageDigest digest=null;
        byte[] salt= new byte[0];

        try{
            salt=base64ToByte(saltKey);
        }catch(IOException e){
            e.printStackTrace();
        }

        try{
            digest=MessageDigest.getInstance("SHA-512");
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }

        assert digest!= null;
        digest.reset();
        digest.update(salt);

        byte[]btPass= digest.digest(msg.getBytes(StandardCharsets.UTF_8));
        digest.reset();
        btPass=digest.digest(btPass);

        return byteToBase64(btPass);
    }

    public static boolean deleteDir(File directory){
        File[] allContents= directory.listFiles();
        if(allContents != null){
            for(File file : allContents){
                deleteDir(file);
            }
        }
        return directory.delete();
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
