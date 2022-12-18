package org.example;

public class OSUtil {
    public enum OS {
        WINDOWS("opencv_java400", ".dll"), LINUX("opencv_java400", ".so"), MAC, SOLARIS;
        OS(){}
        OS(String libraryName, String librarySuffix){
            this.libraryName=libraryName;
            this.librarySuffix=librarySuffix;
        }
        private String libraryName;
        private String librarySuffix;

        public String getLibraryName() {
            return libraryName;
        }

        public String getLibrarySuffix() {
            return librarySuffix;
        }
    }

    private static OS os = null;

    public static OS getOS() {
        if (os == null) {
            String operSys = System.getProperty("os.name").toLowerCase();
            if (operSys.contains("win")) {
                os = OS.WINDOWS;
            } else if (operSys.contains("nix") || operSys.contains("nux")
                    || operSys.contains("aix")) {
                os = OS.LINUX;
            } else {
                throw new IllegalStateException("Unsupported OS: " + OSUtil.getOS());
            }
        }
        return os;
    }
}