package com.surrealdb;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Locale;
import java.util.stream.Stream;

class Loader {

    static String SURREALDB = "surrealdb";
    static String SURREALDB_LIBNAME = System.mapLibraryName(SURREALDB);

    static void loadNative() throws RuntimeException {
        try {
            System.loadLibrary(SURREALDB);
        } catch (final UnsatisfiedLinkError e) {
            try {
                System.load(extract(getPath()).getAbsolutePath());
            } catch (Exception e2) {
                throw new RuntimeException("Couldn't load " + SURREALDB, e2);
            }
        }
    }

    private static String getPath() {
        final String vendor = System.getProperty("java.vendor").toLowerCase(Locale.ENGLISH);
        final String arch = System.getProperty("os.arch").toLowerCase(Locale.ENGLISH);
        final String name = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        final boolean android = vendor.contains("android");
        final boolean linux = name.contains("linux");
        final boolean windows = name.contains("win");
        final boolean osx = name.contains("mac");
        final boolean intel = arch.contains("x86_64") || arch.contains("amd64");
        final boolean arm = arch.contains("aarch64") || arch.contains("arm64");
        if (android) {
            if (arm)
                return "android_arm64";
            else if (intel)
                return "android_64";
        } else if (linux) {
            if (arm)
                return "linux_arm64";
            else if (intel)
                return "linux_64";
        } else if (windows) {
            if (intel)
                return "windows_64";
        } else if (osx) {
            if (arm)
                return "osx_arm64";
            else if (intel)
                return "osx_64";
        }
        throw new RuntimeException("Unsupported architecture: " + arch + " - name: " + name);
    }

    private static void copy(InputStream input, OutputStream output) throws IOException {
        final byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    private static File extract(String path) throws IOException {
        final String resourcePath = "natives/" + path + "/" + SURREALDB_LIBNAME;
        final URL resource = Surreal.class.getClassLoader().getResource(resourcePath);
        if (resource == null) {
            throw new RuntimeException("Couldn't find resource: " + resourcePath);
        }
        final Path tempDir = Files.createTempDirectory("surrealdb-jni");

        // Add a hook to delete the temporary files on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                deleteDirectory(tempDir);
            } catch (IOException e) {
                // Safe to ignore
            }
        }));

        final URLConnection connection = resource.openConnection();
        connection.setUseCaches(false);
        try (InputStream in = new BufferedInputStream(connection.getInputStream())) {
            final File outfile = new File(tempDir.toFile(), SURREALDB_LIBNAME);
            try (FileOutputStream out = new FileOutputStream(outfile)) {
                copy(in, out);
            }
            return outfile;
        }
    }

    private static void deleteDirectory(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (Stream<Path> walker = Files.walk(path)) {
                walker.sorted(Comparator.reverseOrder())
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                // Safe to ignore
                            }
                        });
            }
        }
    }

}