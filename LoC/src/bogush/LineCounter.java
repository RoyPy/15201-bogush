package bogush;

import java.io.*;
import java.nio.file.Path;

class LineCounter {
    static int count(Path file) throws IOException {
        try (InputStream is = new BufferedInputStream(new FileInputStream(file.toFile()))) {
            boolean empty = true;
            byte bytes[] = new byte[1024];
            int readChars;
            int count = 1;
            while ((readChars = is.read(bytes)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (bytes[i] == '\n') {
                        ++count;
                    }
                }
            }
            if (empty) {
                return 0;
            } else {
                return count;
            }
        }
    }
}
