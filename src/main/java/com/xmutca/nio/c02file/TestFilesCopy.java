package com.xmutca.nio.c02file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author <a href="mailto:yhyang@histudy.com">yhyang</a>
 * @since 2021.12.21
 */
public class TestFilesCopy {

    public static void main(String[] args) throws IOException {
        String source = "E:\\qr\\二维码";
        String target = "E:\\qr\\二维码2";

        Files.walk(Paths.get(source)).forEach(path -> {
            String targetName = path.toString().replace(source, target);
            try {
                if (Files.isDirectory(path)) {

                    Files.createDirectory(Paths.get(targetName));

                } else if (Files.isRegularFile(path)) {
                    Files.copy(path, Paths.get(targetName));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
