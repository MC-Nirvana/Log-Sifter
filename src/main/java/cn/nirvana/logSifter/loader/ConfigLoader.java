package cn.nirvana.logSifter.loader;

import cn.nirvana.logSifter.util.FileUtil;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ConfigLoader {
    private List<FilterRule> suppressRules = new ArrayList<>();

    public static ConfigLoader load() {
        Path configPath = FMLPaths.CONFIGDIR.get().resolve("log_sifter.toml");

        // 确保配置文件存在
        FileUtil.ensureConfigFileExists(configPath);

        // 加载配置文件
        ConfigLoader config = new ConfigLoader();
        CommentedFileConfig fileConfig = CommentedFileConfig.builder(configPath)
                .sync()
                .autosave()
                .writingMode(com.electronwill.nightconfig.core.io.WritingMode.REPLACE)
                .build();

        fileConfig.load();
        config.parseConfig(fileConfig);
        fileConfig.close();

        return config;
    }

    @SuppressWarnings("unchecked")
    private void parseConfig(CommentedFileConfig config) {
        // 解析屏蔽规则
        Object suppressObj = config.get("suppress");
        if (suppressObj instanceof com.electronwill.nightconfig.core.Config) {
            com.electronwill.nightconfig.core.Config suppressTable = (com.electronwill.nightconfig.core.Config) suppressObj;
            List<com.electronwill.nightconfig.core.Config> suppressRulesList = suppressTable.get("rule");

            if (suppressRulesList != null) {
                for (com.electronwill.nightconfig.core.Config ruleConfig : suppressRulesList) {
                    String logger = ruleConfig.get("logger");
                    String message = ruleConfig.get("message");
                    Boolean isRegex = ruleConfig.get("is_regex");

                    if (logger != null && message != null) {
                        boolean regex = isRegex != null && isRegex;
                        suppressRules.add(new FilterRule(logger, message, regex));
                    }
                }
            }
        }
    }

    public List<FilterRule> getSuppressRules() {
        return suppressRules;
    }

    public static class FilterRule {
        private final String loggerName;
        private final String message;
        private final boolean isRegex;
        private final Pattern pattern; // 仅在使用正则表达式时使用

        public FilterRule(String loggerName, String message, boolean isRegex) {
            this.loggerName = loggerName;
            this.message = message;
            this.isRegex = isRegex;

            // 预编译正则表达式以提高性能
            if (isRegex) {
                this.pattern = Pattern.compile(message);
            } else {
                this.pattern = null;
            }
        }

        public boolean matches(String logger, String logMessage) {
            if (logger == null || logMessage == null) {
                return false;
            }

            // 检查logger名称是否匹配
            if (!logger.startsWith(loggerName)) {
                return false;
            }

            // 根据匹配类型检查消息内容
            if (isRegex) {
                return pattern.matcher(logMessage).matches();
            } else {
                return logMessage.contains(message);
            }
        }

        public String getLoggerName() {
            return loggerName;
        }

        public String getMessage() {
            return message;
        }

        public boolean isRegex() {
            return isRegex;
        }
    }
}
