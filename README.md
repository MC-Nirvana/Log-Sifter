- 简体中文版
- [繁體中文版](./README_TW.md)
- [English version](./README_EN.md)

<p align="center">
  <img src="./src/main/resources/log_sifter.png" alt="Log Sifter Logo" width="128" height="128">
</p>

# Log Sifter - 过滤掉烦人且无害的日志
[![GitHub release](https://img.shields.io/github/release/MC-Nirvana/Log-Sifter.svg)](https://github.com/MC-Nirvana/Log-Sifter/releases)
[![GitHub issues](https://img.shields.io/github/issues/MC-Nirvana/Log-Sifter.svg)](https://github.com/MC-Nirvana/Log-Sifter/issues)
[![GitHub license](https://img.shields.io/github/license/MC-Nirvana/Log-Sifter.svg)](https://github.com/MC-Nirvana/Log-Sifter/blob/main/LICENSE)

## 一、简介
Log Sifter 是一个为 Minecraft 设计的模组（内部开发代号：Arona-02），专门用于过滤和屏蔽游戏运行过程中产生的冗余日志信息。该模组基于 NeoForge 平台开发，通过自定义配置规则，帮助用户减少日志文件中的噪音，使重要信息更加清晰可见。

## 二、主要特性
- **日志过滤**：通过配置文件定义规则来屏蔽特定的日志输出
- **正则表达式支持**：支持使用正则表达式精确匹配日志内容
- **动态配置**：支持在运行时加载和应用过滤规则
- **轻量级设计**：不修改游戏核心功能，仅专注于日志管理

## 三、安装指南

### 3-1：前提条件
- 已安装支持的 Minecraft 版本（1.21.1）
- 已安装对应版本的 NeoForge（21.1.200或更高版本）

### 3-2：安装步骤
1. 从项目的 [Release 页面](https://github.com/MC-Nirvana/Log-Sifter/releases/latest) 下载最新版本
2. 将模组文件放入客户端（或者服务器）的 `mods` 文件夹中
3. 启动游戏，模组会在 `config` 目录下自动生成默认配置文件
4. 根据需要编辑配置文件
5. 重启服务器使更改生效
6. 享受 Log Sifter 带来的干净的控制台和日志

## 四、从源代码构建
如果你想从源代码构建插件，你需要 Java Development Kit (JDK) 21 或更高版本。

### 4-1：构建步骤
1. 克隆仓库：`git clone https://github.com/MC-Nirvana/Log-Sifter.git`
2. 进入仓库目录：`cd Log-Sifter`
3. 执行构建命令：`./gradlew build`
4. 在 `build/libs/` 目录下找到生成的 JAR 文件

### 4-2：开发环境设置
- 推荐使用 IntelliJ IDEA 进行开发
- 导入项目后，确保 Gradle 依赖能够正确下载

## 五、贡献与支持
欢迎通过 GitHub Issues 提交 Bug 报告和功能建议

### 5-1：贡献方式
- 提交代码改进和新功能实现
- 完善文档和翻译
- 报告 Bug 和安全问题
- 参与讨论和提供使用反馈

### 5-2：提交 Pull Request 的最佳实践
1. Fork 项目并创建功能分支
2. 编写清晰的提交信息
3. 确保代码符合项目编码规范
4. 添加相应的测试用例
5. 保持 Pull Request 聚焦于单一功能或修复

### 5-3：开发者资源
- 项目遵循标准的 Git 工作流
- 请在提交 Pull Request 前确保代码通过所有测试
- 保持代码风格一致，参考现有代码结构

## 六、许可证
本项目采用 [MIT license](LICENSE) 开源许可证

## 七、支持与反馈
如果你喜欢这个项目，请考虑：
- 给项目点个 Star ⭐
- 在社交媒体上分享这个项目
- 参与项目讨论，提供宝贵意见

## 八：赞助支持
如果你希望支持本项目的持续开发和维护，可以通过以下方式赞助：

- [爱发电](https://ifdian.net/a/MC-Nirvana) - 通过爱发电赞助（适用于中国大陆地区用户）
- [PayPal](https://paypal.me/WHFNirvana) - 通过 PayPal 赞助（适用于海外用户）

您的赞助将用于：
- 维护项目基础设施
- 请作者去码头整点薯条:)

## 九：官方社区
- [QQ](https://qm.qq.com/q/u1FEfZMFe8)
- [Discord](https://discord.gg/4CVVEkC9aB)