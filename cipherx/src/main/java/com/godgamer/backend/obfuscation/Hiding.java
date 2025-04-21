package com.godgamer.backend.obfuscation;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

public class Hiding {
 
    public int streamMetaData(String filename, String attr, String data) {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return writeADS(filename, attr, data);
        } else if (os.contains("linux") || os.contains("mac")) {
            return writeXAttr(filename, attr, data);
        } else {
            System.err.println("Unsupported OS: " + os);
            return 2;
        }
    }

    private int writeADS(String filename, String attr, String data) {
        try (FileOutputStream fos = new FileOutputStream(filename + ":" + attr)) {
            fos.write(data.getBytes());
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
    }

    private int writeXAttr(String filename, String attr, String data) {
        try {
            //! Requires setfattr installed on Linux
            Process process = new ProcessBuilder(
                "setfattr", "-n", "user." + attr, "-v", data, filename
            ).inheritIO().start();

            int exitCode = process.waitFor();
            return exitCode == 0 ? 0 : 1;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return 1;
        }
    }

    public String extractMetaData(String filename, String attr) {
        String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

        if (os.contains("win")) {
            return readADS(filename, attr);
        } else if (os.contains("linux") || os.contains("mac")) {
            return readXAttr(filename, attr);
        } else {
            System.err.println("Unsupported OS: " + os);
            return null;
        }
    }

    private String readADS(String filename, String attr) {
        try (FileInputStream fis = new FileInputStream(filename + ":" + attr)) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] temp = new byte[1024];
            int read;
            while ((read = fis.read(temp)) != -1) {
                buffer.write(temp, 0, read);
            }
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String readXAttr(String filename, String attr) {
        try {
            //! Requires getfattr on Linux/macOS
            ProcessBuilder builder = new ProcessBuilder("getfattr", "--only-values", "-n", "user." + attr, filename);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            int exitCode = process.waitFor();
            return exitCode == 0 ? result.toString() : null;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

}