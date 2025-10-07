package cn.nirvana.logSifter.util;

import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileUtil {

    /**
     * 确保配置文件存在，如果不存在则从默认配置复制
     * @param configPath 配置文件路径
     */
    public static void ensureConfigFileExists(Path configPath) {
        // 如果配置文件不存在，则从默认配置文件复制
        if (!Files.exists(configPath)) {
            try {
                // 创建配置目录（如果不存在）
                Files.createDirectories(configPath.getParent());

                // 从资源中复制默认配置文件
                copyDefaultConfig(configPath);
            } catch (IOException e) {
                throw new RuntimeException("Unable to create configuration file: " + e.getMessage(), e);
            }
        }
    }

    /**
     * 从资源目录复制默认配置文件
     * @param targetPath 目标路径
     * @throws IOException IO异常
     */
    private static void copyDefaultConfig(Path targetPath) throws IOException {
        try (InputStream defaultConfig = FileUtil.class.getClassLoader().getResourceAsStream("log_sifter.toml")) {
            if (defaultConfig != null) {
                Files.copy(defaultConfig, targetPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                throw new IOException("Default configuration file not found: log_sifter.toml");
            }
        }
    }
}
