package net.markais.autoelytra;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Utils {
    public static final Logger LOGGER = LogManager.getLogger("AutoElytra");

    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT)
            .create();

    public static Object load(Type type, String filepath) throws IOException {
        Path path = getFullPath(filepath);
//        if (Files.exists(path)) {
        try (FileReader reader = new FileReader(path.toFile())) {
            return GSON.fromJson(reader, type);
        }
//        }
    }

    public static void save(Object object, String filepath) throws IOException {
        Path path = getFullPath(filepath);
        Path directory = path.getParent();

        if (!Files.exists(directory))
            Files.createDirectories(directory);

        Path tempPath = path.resolveSibling(path.getFileName() + ".tmp");
        Files.writeString(tempPath, GSON.toJson(object));
        Files.move(tempPath, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);

    }

    private static Path getFullPath(String name) {
        return FabricLoader.getInstance()
            .getConfigDir()
            .resolve(name);
    }

    public static String formatDecimal(double n) {
        return new DecimalFormat("#.0", DecimalFormatSymbols.getInstance(Locale.ENGLISH)).format(n);
    }
}
