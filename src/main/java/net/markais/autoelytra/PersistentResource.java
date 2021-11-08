package net.markais.autoelytra;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.lang3.NotImplementedException;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public abstract class PersistentResource {

    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .excludeFieldsWithModifiers(Modifier.PRIVATE)
            .create();

    private Path filePath;

    protected static PersistentResource load(Class<? extends PersistentResource> type, String filePath) {
        Path path = getConfigPath(filePath);
        PersistentResource resource;

        if (Files.exists(path)) {
            try (FileReader reader = new FileReader(path.toFile())) {
                resource = GSON.fromJson(reader, type);
            } catch (IOException e) {
                throw new RuntimeException("Could not parse resource", e);
            }
        } else {
            try {
                resource = type.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                return null;
            }
        }

        resource.filePath = path;

        return resource;
    }

    public void save() {
        Path dir = this.filePath.getParent();

        try {
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
//        else if (!Files.isDirectory(dir)) {
//            throw new IOException("Not a directory: " + dir);
//        }

            // Use a temporary location next to the config's final destination
            Path tempPath = this.filePath.resolveSibling(this.filePath.getFileName() + ".tmp");

            // Write the file to our temporary location
            Files.writeString(tempPath, GSON.toJson(this));

            // Atomically replace the old config file (if it exists) with the temporary file
            Files.move(tempPath, this.filePath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            AutoElytra.LOGGER.error("Could not save resource");
            e.printStackTrace();
        }
    }

    private static Path getConfigPath(String name) {
        return FabricLoader.getInstance()
                .getConfigDir()
                .resolve(name);
    }
}
