package cn.nirvana.logSifter.core;

import cn.nirvana.logSifter.loader.ConfigLoader;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.filter.AbstractFilter;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@EventBusSubscriber(modid = "log_sifter")
public class LogCore {
    private static ConfigLoader config;
    private static boolean isRedirected = false;
    private static final String REDIRECTOR_NAME = "system.err.Redirector";
    private static final org.apache.logging.log4j.Logger ERR_LOGGER = LogManager.getLogger(REDIRECTOR_NAME);

    // 用于定时检查哪些线程的报错“喷完了”
    private static final ScheduledExecutorService WATCHDOG = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "LogSifter-Watchdog");
        t.setDaemon(true);
        return t;
    });

    public static void initialize() {
        config = ConfigLoader.load();
        applyFilters();
        redirectSystemErr();
    }

    private static void redirectSystemErr() {
        if (isRedirected) return;
        try {
            // 包装一层我们自己的“碎纸收集器”
            System.setErr(new PrintStream(new ManualAggregatorStream(), true, StandardCharsets.UTF_8.name()));
            isRedirected = true;
            LogManager.getLogger().info("LogSifter: Manual Aggregator Active.");
        } catch (Exception e) {
            LogManager.getLogger().error("Redirect failed", e);
        }
    }

    private static void applyFilters() {
        try {
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            Configuration configuration = context.getConfiguration();
            LogFilter filter = new LogFilter(config);
            configuration.getRootLogger().addFilter(filter);
            context.updateLoggers();
        } catch (Exception e) {}
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        applyFilters();
    }

    /**
     * 暴力整合流：它不相信换行符，它只相信“时间”。
     * 如果一个线程在 100ms 内没有新的输出，我们就认为这一坨日志喷完了。
     */
    private static class ManualAggregatorStream extends OutputStream {
        private static final ThreadLocal<ThreadLogSession> SESSIONS = ThreadLocal.withInitial(ThreadLogSession::new);

        @Override
        public void write(int b) {
            write(new byte[]{(byte) b}, 0, 1);
        }

        @Override
        public void write(byte[] b, int off, int len) {
            String text = new String(b, off, len, StandardCharsets.UTF_8);
            // 按照换行符拆分，逐行处理逻辑断句
            String[] lines = text.split("(?<=\n)");
            for (String line : lines) {
                SESSIONS.get().append(line);
            }
        }

        private static class ThreadLogSession {
            private final StringBuilder buffer = new StringBuilder();
            private long lastTimestamp = 0;
            private boolean scheduled = false;

            public synchronized void append(String line) {
                // --- 逻辑断句核心：主动识别新异常头 ---
                // 如果当前行不是以空白字符开头，且包含 "Exception" 或 "Error"（且不是堆栈中间行）
                // 并且缓冲区里已经有内容了，说明这是一个新的开始
                if (isNewLogHeader(line) && buffer.length() > 0) {
                    flushNow();
                }

                buffer.append(line);
                lastTimestamp = System.currentTimeMillis();

                if (!scheduled) {
                    scheduled = true;
                    WATCHDOG.schedule(this::tryFlush, 150, TimeUnit.MILLISECONDS);
                }
            }

            private boolean isNewLogHeader(String line) {
                String trimmed = line.trim();
                // 1. 堆栈行（at...）或延续行（Caused by...）绝对不是新头部
                if (trimmed.startsWith("at ") || trimmed.startsWith("Caused by") || trimmed.startsWith("...")) {
                    return false;
                }
                // 2. 如果这行不以空格/Tab开头，且包含常见的错误结尾标识，判定为新异常头
                // 例如 "java.lang.NoClassDefFoundError: ..."
                return !line.startsWith(" ") && !line.startsWith("\t") &&
                        (trimmed.contains("Error:") || trimmed.contains("Exception:"));
            }

            public synchronized void flushNow() {
                String finalLog = buffer.toString().trim();
                if (!finalLog.isEmpty()) {
                    ERR_LOGGER.error(finalLog);
                }
                buffer.setLength(0);
                scheduled = false;
            }

            public synchronized void tryFlush() {
                if (!scheduled) return;
                long now = System.currentTimeMillis();
                if (now - lastTimestamp >= 100) {
                    flushNow();
                } else {
                    WATCHDOG.schedule(this::tryFlush, 100, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    private static class LogFilter extends AbstractFilter {
        private final ConfigLoader config;
        public LogFilter(ConfigLoader config) { this.config = config; }

        @Override
        public Result filter(LogEvent event) {
            if (config == null) return Result.NEUTRAL;

            // 此时 message 已经是整合后的一大坨，比如包含了 BlockColor 的完整堆栈
            String msg = event.getMessage().getFormattedMessage();
            String name = event.getLoggerName();

            for (ConfigLoader.FilterRule rule : config.getSuppressRules()) {
                // 只要规则匹配成功，这“一大坨”日志就全部 DENY
                if (rule.matches(name, msg)) {
                    return Result.DENY;
                }
            }
            return Result.NEUTRAL;
        }
    }
}