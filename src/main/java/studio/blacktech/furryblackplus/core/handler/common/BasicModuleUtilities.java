/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the BTS Anti-Commercial & GNU Affero General
 * Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * BTS Anti-Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy of the BTS Anti-Commercial & GNU Affero
 * General Public License along with this program.
 *
 */


package studio.blacktech.furryblackplus.core.handler.common;

import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.common.exception.ModuleException;
import studio.blacktech.furryblackplus.core.common.exception.moduels.load.MisConfigException;
import studio.blacktech.furryblackplus.core.common.logger.LoggerXFactory;
import studio.blacktech.furryblackplus.core.common.logger.base.LoggerX;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;


public class BasicModuleUtilities {


    protected final LoggerX logger;


    public BasicModuleUtilities() {
        this.logger = LoggerXFactory.newLogger(this.getClass());
    }


    // =================================================================================================================


    public final int parseInteger(String temp) throws MisConfigException {
        try {
            return Integer.parseInt(temp);
        } catch (Exception exception) {
            throw new MisConfigException("配置解析错误 " + temp, exception);
        }
    }


    public final Integer parseIntegerOrNull(String temp) throws MisConfigException {
        try {
            return Integer.parseInt(temp);
        } catch (Exception exception) {
            return null;
        }
    }


    public final long parseLong(String temp) throws MisConfigException {
        try {
            return Long.parseLong(temp);
        } catch (Exception exception) {
            throw new MisConfigException("配置解析错误 " + temp, exception);
        }
    }


    public final Long parseLongOrNull(String temp) throws MisConfigException {
        try {
            return Long.parseLong(temp);
        } catch (Exception exception) {
            return null;
        }
    }


    // =================================================================================================================


    @Api(value = "初始化文件", attention = "此方法会以工作目录为相对路径 慎用")
    protected final File initFile(String path) {
        return this.initFile(Paths.get(path));
    }

    @Api(value = "初始化文件", attention = "此方法会以工作目录为相对路径 慎用")
    protected final File initFile(Path path) {
        return this.initFile(path.toFile());
    }

    @Api(value = "初始化文件", attention = "此方法会以工作目录为相对路径 慎用")
    protected final File initFile(File file) {
        if (file.isDirectory()) {
            throw new ModuleException("指定路径已存在文件夹 -> " + file.getAbsolutePath());
        }
        try {
            if (file.createNewFile()) {
                this.logger.seek("创建新文件 -> " + file.getAbsolutePath());
            }
        } catch (IOException exception) {
            throw new ModuleException("创建文件失败 -> " + file.getAbsolutePath(), exception);
        }
        if (!file.canRead()) {
            throw new ModuleException("文件无权读取 -> " + file.getAbsolutePath());
        }
        if (!file.canWrite()) {
            throw new ModuleException("文件无权写入 -> " + file.getAbsolutePath());
        }
        return file;
    }


    // =================================================================================================================


    @Api(value = "初始化文件夹", attention = "此方法会以工作目录为相对路径 慎用")
    protected final File initFolder(String path) {
        return this.initFolder(Paths.get(path));
    }

    @Api(value = "初始化文件夹", attention = "此方法会以工作目录为相对路径 慎用")
    protected final File initFolder(Path path) {
        return this.initFolder(path.toFile());
    }

    @Api(value = "初始化文件夹", attention = "此方法会以工作目录为相对路径 慎用")
    protected final File initFolder(File file) {
        if (file.exists() && !file.isDirectory()) {
            throw new ModuleException("文件夹被文件占位 -> " + file.getAbsolutePath());
        }
        if (file.mkdirs()) {
            this.logger.seek("创建新目录 -> " + file.getAbsolutePath());
        }
        if (!file.canRead()) {
            throw new ModuleException("文件夹无权读取 -> " + file.getAbsolutePath());
        }
        if (!file.canWrite()) {
            throw new ModuleException("文件夹无权写入 -> " + file.getAbsolutePath());
        }
        if (!file.canExecute()) {
            throw new ModuleException("文件夹无权执行 -> " + file.getAbsolutePath());
        }
        return file;
    }


    // =================================================================================================================


    @Api("按行读取文件 删除注释")
    protected List<String> readFile(String path) {
        return this.readFile(Paths.get(path));
    }

    @Api("按行读取文件 删除注释")
    protected List<String> readFile(Path path) {
        return this.readFile(path.toFile());
    }

    @Api("按行读取文件 删除注释")
    protected List<String> readFile(File file) {
        checkReadability(file);
        String line;
        List<String> temp = new LinkedList<>();
        try (
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader)
        ) {
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                if (line.startsWith("#")) {
                    continue;
                }
                int index = line.indexOf("#");
                if (index > 0) {
                    line = line.substring(0, index).trim();
                }
                temp.add(line);
            }
        } catch (IOException exception) {
            throw new ModuleException(exception);
        }
        return temp;
    }


    // =================================================================================================================


    @Api("按行读取文件内容")
    protected List<String> readFileLines(String path) {
        return this.readFileLines(Paths.get(path));
    }

    @Api("按行读取文件内容")
    protected List<String> readFileLines(Path path) {
        return this.readFileLines(path.toFile());
    }

    @Api("按行读取文件内容")
    protected List<String> readFileLines(File file) {
        checkReadability(file);
        String line;
        List<String> temp = new LinkedList<>();
        try (
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader)
        ) {
            while ((line = reader.readLine()) != null) {
                temp.add(line);
            }
        } catch (IOException exception) {
            throw new ModuleException(exception);
        }
        return temp;
    }


    // =================================================================================================================


    @Api("读取文件内容 UTF-8")
    protected String readFileContent(String path) {
        return this.readFileContent(Paths.get(path));
    }

    @Api("读取文件内容 UTF-8")
    protected String readFileContent(Path path) {
        return this.readFileContent(path.toFile());
    }

    @Api("读取文件内容 UTF-8")
    protected String readFileContent(File file) {
        checkReadability(file);
        String temp;
        try (InputStream fileInputStream = new FileInputStream(file)) {
            byte[] bytes = fileInputStream.readAllBytes();
            temp = new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new ModuleException(exception);
        }
        return temp;
    }


    // =================================================================================================================


    private static void checkReadability(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("文件不存在 -> " + file.getAbsolutePath());
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("文件是目录 -> " + file.getAbsolutePath());
        }
        if (!file.canRead()) {
            throw new IllegalArgumentException("文件无权读 -> " + file.getAbsolutePath());
        }
    }


    // =================================================================================================================


}
