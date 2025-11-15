package com.ovvium.services.util.util.velocity;

import java.io.File;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

public class PrefixResourceLoader extends ResourceLoader {

    private static final String BASE_PATH = "base.path";

    public static final String NAME = "prefix";
    public static final String RESOURCE_LOADER = NAME + ".resource.loader";
    public static final String RESOURCE_LOADER_CLASS = RESOURCE_LOADER + ".class";
    public static final String RESOURCE_LOADER_CACHE = RESOURCE_LOADER + ".cache";
    public static final String RESOURCE_BASE_PATH = RESOURCE_LOADER + "." + BASE_PATH;

    private List<String> basePaths;

    @Override
    public void init(ExtendedProperties configuration) {
        String paths = configuration.getString(BASE_PATH);
        basePaths = new ArrayList<String>();
        for (String path : paths.split(",")) {
            path = path.trim();
            if (!path.endsWith("/")) {
                path = path + "/";
            }
            basePaths.add(path);
        }
    }

    @Override
    @SneakyThrows
    public InputStream getResourceStream(String source) {
        URL url = findResource(source);
        if (url == null) {
            throw new ResourceNotFoundException("Could not fount resource [" + source + "]");
        }
        return url.openStream();
    }

    @Override
    public boolean isSourceModified(Resource resource) {
        return resource.getLastModified() == getLastModified(resource);
    }

    @Override
    @SneakyThrows
    public long getLastModified(Resource resource) {
        URL url = findResource(resource.getName());
        if (url == null) {
            return 0;
        }
        String fileName;
        if (url.getProtocol().equals("file")) {
            fileName = url.getFile();
        } else if (url.getProtocol().equals("jar")) {
            JarURLConnection jarUrl = (JarURLConnection) url.openConnection();
            fileName = jarUrl.getJarFile().getName();
        } else {
            throw new IllegalArgumentException("Not a file");
        }
        File file = new File(fileName);
        return file.lastModified();
    }

    protected URL findResource(String path) {
        for (String base : basePaths) {
            URL url = this.getClass().getResource(base + path);
            if (url != null) {
                return url;
            }
        }
        return null;
    }

}
