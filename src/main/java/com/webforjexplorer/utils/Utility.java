package com.webforjexplorer.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webforj.component.icons.TablerIcon;
import com.webforj.component.tree.TreeNode;
import com.webforj.utilities.Assets;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Utility {
  private static final Map<String, String> iconMappings = new HashMap<>();
  private static final Map<String, String> fileLanguageMappings = new HashMap<>();

  static {
    Type type = new TypeToken<Map<String, Map<String, String>>>() {
    }.getType();

    Map<String, Map<String, String>> config = new Gson().fromJson(
        Assets.contentOf(Assets.resolveContextUrl("context://config/mapping.json")), type);
    iconMappings.putAll(config.getOrDefault("iconMappings", Collections.emptyMap()));
    fileLanguageMappings.putAll(config.getOrDefault("fileLanguageMappings", Collections.emptyMap()));
  }

  private Utility() {
    // Prevent instantiation
  }

  public static void setFileIcons(TreeNode node, File file) {
    String extension = getFileExtension(file.getName());
    String iconName = iconMappings.getOrDefault(extension, "file");

    node.setIcon(TablerIcon.create(iconName));
    node.setSelectedIcon(TablerIcon.create(iconName));
  }

  private static String getFileExtension(String fileName) {
    int lastDotIndex = fileName.lastIndexOf('.');
    return (lastDotIndex != -1) ? fileName.substring(lastDotIndex + 1).toLowerCase() : "";
  }

  public static String getFileLanguage(String fileName) {
    String extension = getFileExtension(fileName);
    return fileLanguageMappings.getOrDefault(extension, "plaintext");
  }

  public static String getFileContent(File file) throws IOException {
    return new String(Files.readAllBytes(file.toPath()));
  }

  public static String getFileTooltip(File file) {
    String type = file.isDirectory() ? "Directory" : "File";
    String lastModified = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file.lastModified());
    String size = file.isFile() ? getFileSize(file.length()) : "N/A";
    return String.format(
        "<strong>Path:</strong> %s<br><strong>Type:</strong> %s<br><strong>Last Modified:</strong> %s<br><strong>Size:</strong> %s",
        file.getAbsolutePath(),
        type,
        lastModified,
        size);
  }

  public static String getFileSize(long bytes) {
    int unit = 1024;
    if (bytes < unit)
      return bytes + " B";
    int exp = (int) (Math.log(bytes) / Math.log(unit));
    String pre = "KMGTPE".charAt(exp - 1) + "";
    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
  }

  public static void processDirectoryContents(File directory, Consumer<File> fileProcessor) {
    File[] children = directory.listFiles();
    if (children != null) {
      Arrays.stream(children)
          .filter(File::exists)
          .sorted(Utility::compareFilesByTypeAndName)
          .forEach(fileProcessor);
    }
  }

  private static int compareFilesByTypeAndName(File file1, File file2) {
    // Prioritize directories starting with "."
    if (file1.isDirectory() && file1.getName().startsWith(".")
        && !(file2.isDirectory() && file2.getName().startsWith("."))) {
      return -1;
    }
    if (file2.isDirectory() && file2.getName().startsWith(".")
        && !(file1.isDirectory() && file1.getName().startsWith("."))) {
      return 1;
    }

    // Default sorting: directories before files
    return Boolean.compare(file2.isDirectory(), file1.isDirectory());
  }
}
