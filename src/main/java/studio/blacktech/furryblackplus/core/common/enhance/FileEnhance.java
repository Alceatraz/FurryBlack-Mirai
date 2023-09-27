package studio.blacktech.furryblackplus.core.common.enhance;

import studio.blacktech.furryblackplus.core.common.annotation.Comment;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.WRITE;

@Comment("文件工具")
public class FileEnhance {

  //= ==================================================================================================================

  public static Path get(Path path, String name) {
    return Paths.get(path.toAbsolutePath().toString(), name);
  }

  public static Path get(File path, String name) {
    return Paths.get(path.getAbsolutePath(), name);
  }

  //= ==================================================================================================================

  public static long lastModifyEpoch(Path path) {
    try {
      return Files.getLastModifiedTime(path).toMillis();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  //= ==================================================================================================================

  @Comment("检查目标存在文件")
  public static Path checkFile(Path path) {
    if (Files.exists(path)) {
      if (!Files.isRegularFile(path)) {
        throw new RuntimeException("目标非文件 -> " + path);
      }
    } else {
      throw new RuntimeException("文件不存在 -> " + path.toFile().getAbsolutePath());
    }
    return path;
  }

  @Comment("检查目标存在可读文件")
  public static Path checkReadable(Path path) {
    checkFile(path);
    if (!Files.isReadable(path)) {
      throw new RuntimeException("文件不可读 -> " + path);
    }
    return path;
  }

  @Comment("检查目标存在可写文件")
  public static Path checkWritable(Path path) {
    checkFile(path);
    if (!Files.isWritable(path)) {
      throw new RuntimeException("文件不可写 -> " + path);
    }
    return path;
  }

  //= ==================================================================================================================

  @Comment("确保目标存在可读写文件")
  public static Path ensureFile(Path path) {
    if (Files.exists(path)) {
      if (!Files.isRegularFile(path)) {
        throw new RuntimeException("目标非文件 -> " + path);
      }
    } else {
      try {
        Files.createDirectories(path.getParent());
        Files.createFile(path);
      } catch (IOException exception) {
        throw new RuntimeException("创建文件失败 -> " + path, exception);
      }
    }
    if (!Files.isReadable(path)) {
      throw new RuntimeException("文件不可读 -> " + path);
    }
    if (!Files.isWritable(path)) {
      throw new RuntimeException("文件不可写 -> " + path);
    }
    return path;
  }

  @Comment("确保目标存在可读写目录")
  public static Path ensureFolder(Path path) {
    if (Files.exists(path)) {
      if (!Files.isDirectory(path)) {
        throw new RuntimeException("目标非目录 -> " + path);
      }
    } else {
      try {
        Files.createDirectories(path);
      } catch (IOException exception) {
        throw new RuntimeException("创建目录失败 -> " + path, exception);
      }
    }
    if (!Files.isReadable(path)) {
      throw new RuntimeException("目录不可读取 -> " + path);
    }
    if (!Files.isWritable(path)) {
      throw new RuntimeException("目录不可写入 -> " + path);
    }
    if (!Files.isExecutable(path)) {
      throw new RuntimeException("目录不可进入 -> " + path);
    }
    return path;
  }

  @Comment("确保目标存在可读写文件")
  public static String ensureFileSafe(Path path) {
    if (Files.exists(path)) {
      if (!Files.isRegularFile(path)) {
        return "目标非文件";
      }
    } else {
      try {
        Files.createDirectories(path.getParent());
        Files.createFile(path);
      } catch (IOException exception) {
        return "创建文件失败 -> " + exception.getMessage();
      }
    }
    if (!Files.isReadable(path)) {
      return "文件不可读";
    }
    if (!Files.isWritable(path)) {
      return "文件不可写";
    }
    return null;
  }

  @Comment("确保目标存在可读写目录")
  public static String ensureFolderSafe(Path path) {
    if (Files.exists(path)) {
      if (!Files.isDirectory(path)) {
        return "目标非目录";
      }
    } else {
      try {
        Files.createDirectories(path);
      } catch (IOException exception) {
        return "创建目录失败 -> " + exception.getMessage();
      }
    }
    if (!Files.isReadable(path)) {
      return "目录不可读取";
    }
    if (!Files.isWritable(path)) {
      return "目录不可写入";
    }
    if (!Files.isExecutable(path)) {
      return "目录不可进入";
    }
    return null;
  }

  //= ==================================================================================================================

  @Comment("检查并读取文件")
  public static String read(Path path) {
    checkReadable(path);
    try {
      return Files.readString(path, StandardCharsets.UTF_8);
    } catch (IOException exception) {
      throw new RuntimeException("读取失败 -> ", exception);
    }
  }

  @Comment("检查并按行读取文件")
  public static List<String> readLine(Path path) {
    checkReadable(path);
    try {
      return Files.readAllLines(path, StandardCharsets.UTF_8);
    } catch (IOException exception) {
      throw new RuntimeException("读取失败 -> ", exception);
    }
  }

  //= ==================================================================================================================

  @Comment("确保并写入文件")
  public static void write(Path path, String content) {
    ensureFile(path);
    try {
      Files.writeString(path, content, StandardCharsets.UTF_8, WRITE);
    } catch (IOException exception) {
      throw new RuntimeException("写入失败 -> ", exception);
    }
  }

  @Comment("确保并写入文件")
  public static void write(Path path, List<String> content) {
    ensureFile(path);
    try {
      Files.write(path, content, StandardCharsets.UTF_8, WRITE);
    } catch (IOException exception) {
      throw new RuntimeException("写入失败 -> ", exception);
    }
  }

  @Comment("确保并写入文件")
  public static void append(Path path, String content) {
    ensureFile(path);
    try {
      Files.writeString(path, content, StandardCharsets.UTF_8, APPEND);
    } catch (IOException exception) {
      throw new RuntimeException("写入失败 -> ", exception);
    }
  }

  @Comment("确保并写入文件")
  public static void append(Path path, List<String> content) {
    ensureFile(path);
    try {
      Files.write(path, content, StandardCharsets.UTF_8, APPEND);
    } catch (IOException exception) {
      throw new RuntimeException("写入失败 -> ", exception);
    }
  }

  //= ==================================================================================================================

  public static Path checkFile(Path path, String name) {
    return checkFile(get(path, name));
  }

  public static Path checkReadable(Path path, String name) {
    return checkReadable(get(path, name));
  }

  public static Path checkWritable(Path path, String name) {
    return checkWritable(get(path, name));
  }

  public static Path ensureFile(Path path, String name) {
    return ensureFile(get(path, name));
  }

  public static Path ensureFolder(Path path, String name) {
    return ensureFolder(get(path, name));
  }

  public static String read(Path path, String name) {
    return read(get(path, name));
  }

  public static List<String> readLine(Path path, String name) {
    return readLine(get(path, name));
  }

  public static void write(Path path, String name, String content) {
    write(get(path, name), content);
  }

  public static void write(Path path, String name, List<String> content) {
    write(get(path, name), content);
  }

  public static void append(Path path, String name, String content) {
    append(get(path, name), content);
  }

  public static void append(Path path, String name, List<String> content) {
    append(get(path, name), content);
  }
}
