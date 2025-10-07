- [简体中文版](./README.md)
- [繁體中文版](./README_TW.md)
- English version

<p align="center">
  <img src="./src/main/resources/n_logfilter.png" alt="Log Sifter Logo" width="128" height="128">
</p>

# Log Sifter - Filter Out Annoying but Harmless Logs
[![GitHub release](https://img.shields.io/github/release/MC-Nirvana/Log-Sifter.svg)](https://github.com/MC-Nirvana/Log-Sifter/releases)
[![GitHub issues](https://img.shields.io/github/issues/MC-Nirvana/Log-Sifter.svg)](https://github.com/MC-Nirvana/Log-Sifter/issues)
[![GitHub license](https://img.shields.io/github/license/MC-Nirvana/Log-Sifter.svg)](https://github.com/MC-Nirvana/Log-Sifter/blob/main/LICENSE)

## Ⅰ. Introduction
Log Sifter is a mod designed specifically for Minecraft NeoForge servers and clients (internal codename: Plana-01) , which filters and blocks redundant log information generated in the game. This module is based on the NeoForge platform and can help users reduce noise in log files by customizing configuration rules, making important information more prominent and visible.

## II. Main Features
- **Log Filtering**: Define rules through configuration files to block specific log outputs
- **Regular Expression Support**: Support precise log content matching using regular expressions
- **Dynamic Configuration**: Support loading and applying filtering rules at runtime
- **Lightweight Design**: Does not modify core game functions, focusing only on log management

## III. Installation Guide
### 3-1：Prerequisites
- Installed supported Minecraft version (1.21.1)
- Installed corresponding NeoForge version (21.1.200 or higher)
### 3-2：Installation Steps
1. Download the latest version from the project's [Release](https://github.com/MC-Nirvana/Log-Sifter/releases/latest) page
2. Place the mod file in the client (or server) `mods` folder
3. Start the game, and the mod will automatically generate the default configuration file in the `config` directory
4. Edit the configuration file as needed
5. Restart the server to apply changes
6. Enjoy the clean console and logs brought by Log Sifter

## IV. Configuration File
The configuration file is located in the `config` directory and is named `log_sifter.toml`. This file uses TOML format for configuration. Here is a sample configuration file:
```toml
# Log Sifter Default Configuration

# Suppress Rules - Completely prevent matching log output
# Each [[suppress.rule]] represents one suppress rule
# Format:
# logger: Log source
# message: Message matching, can be text or regex
# is_regex: Whether it is a regular expression, default is false
[suppress]

[[suppress.rule]]
logger = "net.minecraft.world.item.crafting.RecipeManager"
message = "Parsing error loading recipe .*"
is_regex = true

[[suppress.rule]]
logger = "net.minecraft.advancements.AdvancementTree"
message = "Couldn't load advancements: .*"
is_regex = true
```
## Ⅴ. Building from Source Code
If you want to build the plugin from source code, you need Java Development Kit (JDK) 21 or higher.

### 5-1：Build Steps
1. Clone the repository: `git clone https://github.com/MC-Nirvana/Log-Sifter.git`
2. Enter the repository directory: `cd Log-Sifter`
3. Execute the build command: `./gradlew build`
4. Find the generated JAR file in the `build/libs/` directory

### 5-2：Development Environment Setup
- Recommended to use IntelliJ IDEA for development
- After importing the project, ensure Gradle dependencies download correctly

## VI. Contribution and Support
Welcome to submit bug reports and feature suggestions through GitHub Issues

### 6-1：Contribution Methods
- Submit code improvements and new feature implementations
- Improve documentation and translations
- Report bugs and security issues
- Participate in discussions and provide usage feedback

### 6-2：Best Practices for Submitting Pull Requests
1. Fork the project and create a feature branch
2. Write clear commit messages
3. Ensure code complies with project coding standards
4. Add corresponding test cases
5. Keep Pull Requests focused on a single feature or fix

### 6-3：Developer Resources
- The project follows standard Git workflow
- Please ensure code passes all tests before submitting a Pull Request
- Maintain consistent code style, refer to existing code structure

## VII. Project Roadmap
- [ ] Add log level modification functionality

## VIII.  License
This project uses the [GPL-3.0 license](LICENSE) open source license

## IX. Support and Feedback
If you like this project, please consider:
- Giving the project a Star ⭐
- Sharing this project on social media
- Participating in project discussions and providing valuable feedback

### Ⅹ. Sponsorship Support
If you wish to support the continued development and maintenance of this project, you can sponsor through the following methods:

- [MianBaoDuo](https://mbd.pub/o/MC_Nirvana) - Sponsor through MianBaoDuo (suitable for mainland China users)
- [PayPal](https://paypal.me/WHFNirvana) - Sponsor through PayPal (suitable for overseas users)

Your sponsorship will be used for:
- Maintaining project infrastructure
- Buying french fries for the author at the dock :)

## XI. Official Community
- [QQ](https://qm.qq.com/q/u1FEfZMFe8)
- [Discord](https://discord.gg/4CVVEkC9aB)