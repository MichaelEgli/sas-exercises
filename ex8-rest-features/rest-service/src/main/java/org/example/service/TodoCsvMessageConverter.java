package org.example.service;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class TodoCsvMessageConverter {
// public class TodoCsvMessageConverter implements HttpMessageConverter<List<Todo>> {

/*    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return List.of(MediaType.valueOf("text/csv"));
    }
    @Override
    public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
        if (mediaType.isCompatibleWith(MediaType.valueOf("text/csv"))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        if (mediaType.isCompatibleWith(MediaType.valueOf("text/csv"))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Todo> read(Class<? extends List<Todo>> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    public void write(List<Todo> todos, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        java.io.OutputStream out = outputMessage.getBody();
        java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(out, java.nio.charset.StandardCharsets.UTF_8));
        writer.write("id,title,completed");
        writer.newLine();
        for (Todo t : todos) {
            String title = t.getTitle();
            if (title == null) title = "";
            if (title.contains(",") || title.contains("\"") || title.contains("\n")) {
                title = "\"" + title.replace("\"", "\"\"") + "\"";
            }
            writer.write(String.valueOf(t.getId()));
            writer.write(",");
            writer.write(title);
            writer.write(",");
            writer.write(String.valueOf(t.isCompleted()));
            writer.newLine();
        }
        writer.flush();
    }*/
}
