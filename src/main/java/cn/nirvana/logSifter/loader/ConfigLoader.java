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
        Object suppressObj = config.get("suppress");
        if (suppressObj instanceof com.electronwill.nightconfig.core.Config) {
            com.electronwill.nightconfig.core.Config suppressTable = (com.electronwill.nightconfig.core.Config) suppressObj;
            List<com.electronwill.nightconfig.core.Config> suppressRulesList = suppressTable.get("rule");

            if (suppressRulesList != null) {
                for (com.electronwill.nightconfig.core.Config ruleConfig : suppressRulesList) {
                    String logger = ruleConfig.get("logger");
                    String message = ruleConfig.get("message");
                    Boolean isRegex = ruleConfig.get("is_regex");

                    // 现在只需要 message 不为空即可添加规则
                    if (message != null) {
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
        private final Pattern pattern;

        public FilterRule(String loggerName, String message, boolean isRegex) {
            // 如果 logger 为空，将其转换为空字符串，便于后续判断
            this.loggerName = (loggerName == null) ? "" : loggerName;
            this.message = message;
            this.isRegex = isRegex;

            if (isRegex) {
                this.pattern = Pattern.compile(message);
            } else {
                this.pattern = null;
            }
        }

        public boolean matches(String logger, String logMessage) {
            if (logMessage == null) {
                return false;
            }

            // --- 核心逻辑：留空即全域匹配 ---
            // 只有当配置了具体的 loggerName 时，才校验来源前缀
            if (!this.loggerName.isEmpty()) {
                if (logger == null || !logger.startsWith(this.loggerName)) {
                    return false;
                }
            }

            // 根据匹配类型检查消息内容
            if (isRegex) {
                // 使用 find() 以便在长堆栈中检索特征
                return pattern.matcher(logMessage).find();
            } else {
                return logMessage.contains(message);
            }
        }

        public String getLoggerName() { return loggerName; }
        public String getMessage() { return message; }
        public boolean isRegex() { return isRegex; }
    }
}