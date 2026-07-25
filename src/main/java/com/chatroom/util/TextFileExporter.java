package com.chatroom.util;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Writes arbitrary plain text (chat transcripts, server logs) out to a
 * file chosen by the user - no external library required.
 */
public class TextFileExporter {

    public void export(String content, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
        }
    }
}
