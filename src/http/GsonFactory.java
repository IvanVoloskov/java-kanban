package http;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GsonFactory {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Duration.class, new JsonSerializer<Duration>() {
                    @Override
                    public JsonElement serialize(Duration src, Type typeOfSrc, JsonSerializationContext context) {
                        return new JsonPrimitive(src.toMinutes());
                    }
                })
                .registerTypeAdapter(Duration.class, new JsonDeserializer<Duration>() {
                    @Override
                    public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return Duration.ofMinutes(json.getAsLong());
                    }
                })

                .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                    @Override
                    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                        return new JsonPrimitive(src.format(FORMATTER));
                    }
                })
                .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                    @Override
                    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return LocalDateTime.parse(json.getAsString(), FORMATTER);
                    }
                })
                .create();
    }

    /* данную реализацию нашел в интернете, тк тесты не проходили и оказывалось, что проблема в том, что gson не может
    обрабатывать объекты localTime, Duration и тд. Потом нашел такую реализацию gson, которая уже способна обрабатывать
    эти типы данных, а точнее их переводить в понятный формат. Поэтому хотел бы у вас поинтересоваться, как можно было
    бы по-другому решить эту проблему? Просто закомметить поля Duration и тд на время тестов или есть явно более
    подходящий вариант? Заранее спасибо)
     */
}
