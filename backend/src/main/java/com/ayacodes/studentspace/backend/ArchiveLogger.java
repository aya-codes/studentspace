import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;

@Component
public class ArchiveLogger {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String archiveFile = "chat-archive.jsonl";

    public synchronized void logData(ArchivedChatroom archivedChatroom) {
        try (FileWriter writer = new FileWriter(archiveFile, true)) {
            String json = objectMapper.writeValueAsString(archivedChatroom);
            writer.write(json + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
