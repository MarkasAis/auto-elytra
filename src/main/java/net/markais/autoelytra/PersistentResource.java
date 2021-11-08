package net.markais.autoelytra;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public abstract class PersistentResource {

    private transient String filePath;

    protected static Object load(Class<? extends PersistentResource> type, String filePath) {
        PersistentResource resource = null;

        try {
            resource = (PersistentResource) Utils.load(type, filePath);
        } catch (IOException e1) {
            try {
                resource = type.getConstructor().newInstance();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        if (resource != null)
            resource.filePath = filePath;

        return resource;
    }

    public void save() {
        try {
            Utils.save(this, filePath);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.LOGGER.error("Could not save resource");
        }
    }

}
