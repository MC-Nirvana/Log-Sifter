package cn.nirvana.logSifter.core;

import cn.nirvana.logSifter.loader.ConfigLoader;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

import java.util.Map;

@EventBusSubscriber(modid = "log_sifter")
public class LogCore {
    private static ConfigLoader config;
    private static boolean filtersApplied = false;

    public static void initialize() {
        config = ConfigLoader.load();
        // 添加调试信息来确认配置是否正确加载
        org.apache.logging.log4j.LogManager.getLogger().info(
                "Loaded {} suppress rules",
                config.getSuppressRules().size());

        applyFilters();
    }

    private static void applyFilters() {
        try {
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            Configuration configuration = context.getConfiguration();

            // 为所有logger配置添加过滤器（主要用于屏蔽功能）
            Map<String, LoggerConfig> loggerConfigs = configuration.getLoggers();
            LogFilter logFilter = new LogFilter(config);

            for (LoggerConfig loggerConfig : loggerConfigs.values()) {
                loggerConfig.removeFilter(logFilter);
                loggerConfig.addFilter(logFilter);
            }

            // 特别为根logger添加过滤器
            LoggerConfig rootLogger = configuration.getRootLogger();
            rootLogger.addFilter(logFilter);

            context.updateLoggers();

            filtersApplied = true;
            LogManager.getLogger().info("Log Sifter applied successfully");
        } catch (Exception e) {
            LogManager.getLogger().error("Failed to apply Log Sifter", e);
        }
    }

    public static void reapplyFiltersIfNeeded() {
        if (!filtersApplied) {
            LogManager.getLogger().info("Re-applying Log Sifter filters");
            applyFilters();
        }
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        LogManager.getLogger().info("Log Sifter common setup");
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        LogManager.getLogger().info("Re-applying Log Sifter on server start");
        applyFilters();
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (!filtersApplied) {
            LogManager.getLogger().info("Re-applying Log Sifter on first server tick");
            applyFilters();
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        LogManager.getLogger().info("Log Sifter server stopping");
        filtersApplied = false;
    }

    /**
     * 日志过滤器内部类（主要用于屏蔽功能）
     */
    private static class LogFilter extends AbstractFilter {
        private final ConfigLoader config;

        public LogFilter(ConfigLoader config) {
            this.config = config;
        }

        @Override
        public Result filter(LogEvent event) {
            String loggerName = event.getLoggerName();
            String message = event.getMessage().getFormattedMessage();

            // 检查屏蔽规则
            for (ConfigLoader.FilterRule rule : config.getSuppressRules()) {
                if (rule.matches(loggerName, message)) {
                    return Result.DENY;
                }
            }

            return Result.NEUTRAL;
        }

        @Override
        public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
            return Result.NEUTRAL;
        }

        @Override
        public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
            return Result.NEUTRAL;
        }

        @Override
        public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
            return Result.NEUTRAL;
        }
    }
}
