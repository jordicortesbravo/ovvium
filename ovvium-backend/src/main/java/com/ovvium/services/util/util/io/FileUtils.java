package com.ovvium.services.util.util.io;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

import lombok.*;

/**
 * Clase de utilidades sobre ficheros y directorios.
 */
public final class FileUtils {

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    /**
     * Al ser una clase con solo métodos estáticos, debe esconderse el constructor.
     */
    private FileUtils() {}

    /**
     * Método diseñado para sugerir nombres alternativos en el caso de que un archivo con ese nombre ya exista.
     * 
     * @param name
     *            Nombre original
     * @return Nombre sugerido
     */
    public static String autoRenaming(String name) {
        // Fichero no numerado
        Pattern patterNumber = Pattern.compile("([a-zA-Z0-9\\s\\-]+)_([0-9]+)\\.([\\w]+)");
        Matcher matcherNumber = patterNumber.matcher(name);

        // Fichero ya numerado
        Pattern p2 = Pattern.compile("([a-zA-Z0-9\\s\\-]+)\\.([\\w]+)");
        Matcher m2 = p2.matcher(name);

        String autoName = name;

        if (matcherNumber.find()) {
            String originalName = matcherNumber.group(1);
            String suffix = matcherNumber.group(3);
            String currentNumberString = matcherNumber.group(2);
            Integer nextNumber = Integer.parseInt(currentNumberString) + 1;
            autoName = originalName + "_" + nextNumber + "." + suffix;
        } else if (m2.find()) {
            String originalName = m2.group(1);
            String suffix = m2.group(2);
            autoName = originalName + "_0." + suffix;
        }

        return autoName;
    }

    /**
     * Devuelve el directorio especificado, creándolo en caso de que no exista.
     */
    public static File getDirectory(String name) throws IOException {
        File directory = new File(name);
        if (!directory.exists() || !directory.isDirectory() || !directory.mkdirs()) {
            throw new IOException("Can't create " + directory.getAbsolutePath());
        }
        return directory;
    }

    public static File getDirectory(String path, String name) throws IOException {
        return getDirectory(pathConcat(path, name));
    }

    /**
     * Construye un String que contiene todos los paths separados por una única barra '/'. No se modifica el inicio del primer argumento ni
     * el final del último. Si no se entra ningún argumento, se devuelve un String vacío.
     */
    public static String pathConcat(String... paths) {

        StringBuffer sb = new StringBuffer("");

        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];

            if (path.startsWith("/") && i > 0) {
                path = path.substring(1);
            }
            if (!path.endsWith("/") && i < paths.length - 1) {
                path = path + "/";
            }

            sb.append(path);
        }

        return sb.toString();
    }

    /**
     * Unpack an archive from a URL
     * 
     * @param url
     * @param targetDir
     * @return the file to the url
     * @throws IOException
     */
    public static File unpackArchive(URL url, File targetDir) throws IOException {
        if (!targetDir.exists() || !targetDir.isDirectory() || !targetDir.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta " + targetDir.toString());
        }

        InputStream in = new BufferedInputStream(url.openStream(), DEFAULT_BUFFER_SIZE);

        // make sure we get the actual file
        File zip = File.createTempFile("tmp", ".zip", targetDir);
        @Cleanup OutputStream out = new BufferedOutputStream(new FileOutputStream(zip));
        IOUtils.copy(in, out);

        File retFile = unpackArchive(zip, targetDir);

        if (!zip.delete()) {
            throw new IOException("No se pudo borrar el archivo temporal " + targetDir.toString());
        }

        return retFile;
    }

    /**
     * Unpack a zip file
     * 
     * @param sourceFile
     * @param targetDir
     * @return the file
     * @throws IOException
     */
    @SuppressWarnings("rawtypes")
    public static File unpackArchive(File sourceFile, File targetDir) throws IOException {
        if (!sourceFile.exists()) {
            throw new IOException(sourceFile.getAbsolutePath() + " does not exist");
        }

        if (!buildDirectory(targetDir)) {
            throw new IOException("Could not create directory: " + targetDir);
        }

        @Cleanup ZipFile zipFile = new ZipFile(sourceFile);
        for (Enumeration entries = zipFile.entries(); entries.hasMoreElements();) {
            ZipEntry entry = (ZipEntry) entries.nextElement();

            File file = new File(targetDir, File.separator + entry.getName());

            if (!buildDirectory(file.getParentFile())) {
                zipFile.close();
                throw new IOException("Could not create directory: " + file.getParentFile());
            }
            if (!entry.isDirectory()) {
                IOUtils.copy(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(file)));
            } else {
                if (!buildDirectory(file)) {
                    throw new IOException("Could not create directory: " + file);
                }
            }
        }

        zipFile.close();
        return sourceFile;
    }

    public static boolean buildDirectory(File file) {
        return file.exists() || file.mkdirs();
    }

    /**
     * Creates a file and its parent folders. It does nothing if they exist.
     */
    @SneakyThrows
    public static void create(File file) {
        if (!file.exists()) {
            val folder = file.getParentFile();
            if (!folder.exists() && !folder.mkdirs()) {
                throw new IOException("Couldn't create folder for file " + file);
            }
            file.createNewFile();
        }
    }

    @SneakyThrows
    public static void delete(File file) {
        if (file.isDirectory()) {
            for (val children : file.listFiles()) {
                delete(children);
            }
        }
        if (file.exists() && !file.delete()) {
            throw new IOException("Error deleting " + file.getAbsolutePath());
        }
    }

    @SneakyThrows
    public static File recreate(File folder) {
        FileUtils.delete(folder);
        if (!folder.mkdirs()) {
            throw new IOException("Error creating " + folder);
        }
        return folder;
    }

    @SneakyThrows
    public static void write(File file, byte[] bytes) {
        val dir = file.getParentFile();
        if (dir != null) {
            dir.mkdirs();
        }
        Files.write(file.toPath(), bytes);
    }

    @SneakyThrows
    public static byte[] read(File file) {
        return Files.readAllBytes(file.toPath());
    }

}
