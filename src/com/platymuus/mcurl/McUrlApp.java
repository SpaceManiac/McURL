/*
 * McUrlApp.java
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

import java.io.*;
import java.net.*;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class McUrlApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        debug("Showing view.");
        show(new McUrlView(this));
    }
    static String address, username, password;

    public String getAddress() {
        return address;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of McUrlApp
     */
    public static McUrlApp getApplication() {
        return Application.getInstance(McUrlApp.class);
    }

    public static boolean debugging;
    public static PrintWriter debugLog;

    public static void debug(String s) {
        if (debugging) {
            debugLog.println(s);
            debugLog.flush();
        }
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        System.out.println("McURL v1.0 by Tad Hardesty");
        
        username = "";
        password = "";
        address = "";

        if (args.length != 1) {
            /*
            debugging = true;
            try {
            debugLog = new PrintWriter(new FileWriter(new File("mcurl.log")));
            debug("Asdf, the URL isn't being passed!");
            }
            catch (IOException ex) {
                debugging = false;
            }
            */
            return;
        }

        String text = args[0];
        if (text.indexOf("debug") == text.length() - 5) {
            text = text.substring(0, text.length() - 5);
            debugging = true;
            try {
                debugLog = new PrintWriter(new FileWriter(new File("mcurl.log")));
                debug("== Debug log begin ==");
            }
            catch (IOException ex) {
                debugging = false;
            }
        }
        
        debug("Url: " + text + "debug");

        if (text.indexOf("minecraft:") == 0) {
            text = text.substring(10);
        }
        while (text.charAt(0) == '/') {
            text = text.substring(1);
        }
        while (text.charAt(text.length() - 1) == '/') {
            text = text.substring(0, text.length() - 1);
        }
        debug("Extracted server: " + text);

        int at = text.indexOf("@");
        if (at >= 0) {
            address = text.substring(at + 1);
            String prior = text.substring(0, at);
            int colon = prior.indexOf(":");
            if (colon >= 0) {
                username = prior.substring(0, colon);
                password = prior.substring(colon + 1);
            } else {
                username = prior;
            }
        } else {
            address = text;
        }
        debug("Username from URL: " + username);
        debug("Password from URL: " + password);

        // Grab minecraft.jar
        File launcher = new File("minecraft.jar");
        if (!launcher.exists()) {
            debug("Downloading launcher jar.");
            try {
                String url = "https://s3.amazonaws.com/MinecraftDownload/launcher/minecraft.jar";
                BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(launcher), 1024);
                byte data[] = new byte[1024];
                int count;
                while ((count = in.read(data, 0, 1024)) >= 0) {
                    out.write(data, 0, count);
                }
                out.close();
                in.close();
                debug("Success.");
            } catch (IOException ex) {
                debug("Failure.");
                return;
            }
        } else {
            debug("Launcher jar already exists.");
        }

        debug("Grabbing login info");
        LastLogin.calculate();

        if (username.equals("")) {
            username = LastLogin.username;
        }
        if (password.equals("") && username.equalsIgnoreCase(LastLogin.username)) {
            password = LastLogin.password;
        }
        username = username.trim();
        debug("Lastlogin username: " + LastLogin.username);
        debug("Lastlogin password: " + LastLogin.password);
        debug("Resultant username: " + username);
        debug("Resultant password: " + password);

        debug("Launching app.");
        launch(McUrlApp.class, args);
    }
}
