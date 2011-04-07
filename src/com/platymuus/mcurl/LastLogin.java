/*
 * LastLogin.java
 *
 * Copyright (c) 2011 Tad Hardesty
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package com.platymuus.mcurl;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 *
 * @author DarkShadowz
 */
public class LastLogin {

    public static String username;
    public static String password;

    public static File getMinecraftDir() {
        String os = System.getProperty("os.name", "").toLowerCase();
        String home = System.getProperty("user.home", ".");

        if (os.contains("win")) {
            String appdata = System.getenv("APPDATA");
            if (appdata != null) {
                return new File(appdata, ".minecraft");
            } else {
                return new File(home, ".minecraft");
            }
        } else if (os.contains("mac")) {
            return new File(home, "Library/Application Support/minecraft");
        } else {
            return new File(home, ".minecraft/");
        }
    }

    public static void calculate() {
        try {
            Random random = new Random(43287234L);
            byte[] salt = new byte[8];
            random.nextBytes(salt);
            PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 5);
            SecretKey pbeKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(new PBEKeySpec("passwordfile".toCharArray()));
            Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
            cipher.init(2, pbeKey, pbeParamSpec);
            File passFile = new File(getMinecraftDir(), "lastlogin");
            if (!passFile.exists()) {
                return;
            }
            DataInputStream dis = null;
            if (cipher != null) {
                dis = new DataInputStream(new CipherInputStream(new FileInputStream(passFile), cipher));
            } else {
                dis = new DataInputStream(new FileInputStream(passFile));
            }
            username = dis.readUTF();
            password = dis.readUTF();
            dis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
