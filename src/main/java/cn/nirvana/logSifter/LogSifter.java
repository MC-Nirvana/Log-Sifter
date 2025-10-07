package cn.nirvana.logSifter;

import cn.nirvana.logSifter.core.LogCore;

import net.neoforged.bus.api.IEventBus;

import net.neoforged.fml.common.Mod;

import org.apache.logging.log4j.LogManager;

@Mod(cn.nirvana.logSifter.LogSifter.MODID)
public class LogSifter {
    public static final String MODID = "log_sifter";

    public LogSifter(IEventBus modEventBus) {
        LogManager.getLogger().info("Log Sifter mod is initializing");

        try {
            // 初始化日志核心
            LogCore.initialize();
        } catch (Exception e) {
            LogManager.getLogger().error("Failed to initialize Log Sifter", e);
        }
    }
}
