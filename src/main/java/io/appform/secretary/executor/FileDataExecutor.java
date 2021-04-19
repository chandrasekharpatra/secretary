package io.appform.secretary.executor;

import com.google.inject.Singleton;
import io.appform.secretary.model.DataEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FileDataExecutor implements DataExecutor {

    @Override
    public void processFile(InputStream dataStream, String workflow) {
        try {
            //TODO: Add entry in DB for file with state as ACCEPTED and send event
            //TODO: Send event about file acceptance

            List<DataEntry> dataEntries = getRows(dataStream);

            List<DataEntry> validEntries = dataEntries.stream()
                    .map(this::validateRow)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            //TODO: Remove temporary log
            validEntries.forEach(entry -> {log.info("Entry: {}", entry);});

            //TODO: Update file state in DB to PARSED and send event

            //TODO: Push valid entries to Kafka

            //TODO: Update file state in DB to CONSUMED and send event
        } catch (Exception ex) {
            log.warn("Hit exception : {}", ex.getMessage());
        }
    }

    private DataEntry validateRow(DataEntry entry) {
        if (Objects.isNull(entry)) {
            return null;
        }

        boolean emptyValues = entry.getEntryList().stream()
                .anyMatch(String::isEmpty);
        if (emptyValues) {
            return null;
        }

        //TODO: Add schema validation
        return entry;

    }

    private List<DataEntry> getRows(InputStream dataStream) {
        return new BufferedReader(new InputStreamReader(dataStream))
                .lines()
                .map(entry -> DataEntry.builder()
                        .entryList(getTokens(entry))
                        .build())
                .collect(Collectors.toList());
    }

    private List<String> getTokens(String input) {
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .map(entry -> entry.replace(" ", "_"))
                .collect(Collectors.toList());
    }
}
