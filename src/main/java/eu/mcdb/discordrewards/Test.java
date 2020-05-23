package eu.mcdb.discordrewards;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public final class Test {

    public static byte[] inputStreamToByteArray(InputStream in) throws IOException {
        byte[] buff = new byte[in.available()];
        in.read(buff);
        return buff;
    }

    public static void writeToFile(byte[] b, File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(b);
        fos.flush();
        fos.close();
    }

    public static byte[] readFileBytes(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    public static InputStream readFileAsStream(File file) throws IOException {
        return new FileInputStream(file);
    }
}
