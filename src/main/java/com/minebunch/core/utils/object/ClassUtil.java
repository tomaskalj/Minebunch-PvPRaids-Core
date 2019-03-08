package com.minebunch.core.utils.object;

import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import lombok.experimental.UtilityClass;
import org.bukkit.plugin.Plugin;

@UtilityClass
public class ClassUtil {
    public Collection<Class<?>> getClassesInPackage(Plugin plugin, String packageName) {
        Collection<Class<?>> classes = new ArrayList<>();
        CodeSource codeSource = plugin.getClass().getProtectionDomain().getCodeSource();
        URL resource = codeSource.getLocation();
        String resPath = resource.getPath().replace("%20", " ");
        String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
        String relPath = packageName.replace('.', '/');

        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                String className = null;

                if (entryName.endsWith(".class") && entryName.startsWith(relPath) && entryName.length() > (relPath.length() + "/".length())) {
                    className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
                }

                if (className != null) {
                    Class<?> clazz = null;

                    try {
                        clazz = Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    if (clazz != null) {
                        classes.add(clazz);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Unexpected IOException reading JAR file '" + jarPath + "'", e);
        }

        return ImmutableSet.copyOf(classes);
    }
}
