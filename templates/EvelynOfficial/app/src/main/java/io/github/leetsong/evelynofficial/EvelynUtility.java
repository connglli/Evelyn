package io.github.leetsong.evelynofficial;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class EvelynUtility {


    /* Random Things */

    private static Random random = new Random(System.currentTimeMillis());

    public static boolean randomBoolean() {
        return random.nextBoolean();
    }

    public static boolean randomBoolean(double pTrue) {
        float x = random.nextFloat();
        if (x > pTrue) {
            return false;
        } else {
            return true;
        }
    }

    public static double randomDouble() {
        return random.nextDouble();
    }

    /* javascript asset */

    public static String assetJs2String(Context c, String urlStr){
        InputStream in = null;

        try{
            in = c.getAssets().open(urlStr);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

            StringBuilder sb = new StringBuilder();
            String line;
            do {
                line = bufferedReader.readLine();
                if (line != null && !line.matches("^\\s*\\/\\/.*")) {
                    sb.append(line);
                }
            } while (line != null);

            bufferedReader.close();

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}
