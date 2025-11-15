package com.ovvium.services.util.util.basic;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Path implements Comparable<Path>, Iterable<Path>, Serializable {

    private static final long serialVersionUID = -6294704811087757864L;

    public static final char SEPARATOR = '/';
    public static final char EXTENSION_SEPARATOR = '.';

    public static final Path ROOT = new Path(null, "");

    private final Path parent;
    private final String name;

    public static Path of(String s) {
        Path path = ROOT;
        for (String name : s.split("" + SEPARATOR)) {
            name = name.trim();
            if (!name.equals("")) {
                path = new Path(path, name);
            }
        }
        return path;
    }

    public static Path of(String[] parts) {
        Path path = ROOT;
        for (String s : parts) {
            path = new Path(path, s);
        }
        return path;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public int depth() {
        return isRoot() ? 0 : parent.depth() + 1;
    }

    public Path add(String path) {
        return add(of(path));
    }

    public Path add(Path path) {
        Path current = this;
        for (path = path.reverse(); !path.equals(ROOT); current = new Path(current, path.getName()), path = path.parent) {
            ;
        }
        return current;
    }

    public Path reverse() {
        Path p = ROOT;
        for (Path current = this; !current.equals(ROOT); p = new Path(p, current.name), current = current.parent) {
            ;
        }
        return p;
    }

    /**
     * depth = 1 => /a/b/c --> /a depth = 2 => /a/b/c --> /a/b depth = 3 => /a/b/c --> /a/b/c depth >= 4 OR depth < 1 => /a/b/c --> /
     */
    public Path subpath(int depth) {
        String[] parts = toString().split("" + SEPARATOR);
        Path relative = ROOT;
        for (int i = 1; i <= depth && i <= parts.length; i++) {
            relative = relative.add(parts[i - 1]);
        }
        return relative;
    }

    /**
     * /a/b/c --> /b/c
     */
    public Path shiftRoot() {
        val s = toString();
        val parts = asList(s.split("" + SEPARATOR));
        return parts.size() < 1 ? ROOT : of(join(parts.subList(2, parts.size()), SEPARATOR));
    }

    /**
     * nshifts=2 => /a/b/c/d --> /b/c
     */
    public Path shiftLeft(int nshifts) {
        Path p = this;
        for (int i = 0; i < nshifts; i++) {
            p = p.shiftRoot();
        }
        return p;
    }

    /**
     * /a/b/c --> /a
     */
    public Path first() {
        val it = iterator();
        return it.hasNext() ? it.next() : ROOT;
    }

    public String getExtension() {
        val p = name.lastIndexOf(EXTENSION_SEPARATOR);
        return p < 0 || p == name.length() - 1 ? null : name.substring(p + 1);
    }

    @Override
    public String toString() {
        val sb = new StringBuilder();
        for (Path p = this; !p.equals(ROOT); p = p.parent) {
            sb.insert(0, SEPARATOR + p.name);
        }
        val s = sb.toString();
        return s.equals("") ? "/" : s;
    }

    @Override
    public int compareTo(Path other) {
        return toString().compareTo(other.toString());
    }

    public URI toUri() {
        return URI.create(this.toString());
    }

    public File toFile() {
        return new File(this.toString());
    }

    public boolean contains(Path other) {
        return enclose(this).contains(enclose(other));
    }

    public boolean startsWith(Path other) {
        return enclose(this).startsWith(enclose(other));
    }
    
    public boolean endsWith(Path other) {
        return enclose(this).endsWith(enclose(other));
    }

    public List<String> toList() {
        val list = new ArrayList<String>();
        for (val p : this) {
            list.add(p.getName());
        }
        return list;
    }

    @Override
    public Iterator<Path> iterator() {
        return new PathIterator(this);
    }

    public static class PathIterator implements Iterator<Path> {

        private Path current;

        public PathIterator(Path path) {
            current = path.reverse();
        }

        @Override
        public boolean hasNext() {
            return !current.isRoot();
        }

        @Override
        public Path next() {
            val c = current;
            current = current.getParent();
            return c;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private String enclose(Path p) {
        val sep = valueOf(SEPARATOR);
        return p.toString().endsWith(sep) ? p.toString() : p.toString().concat(sep);
    }
}
