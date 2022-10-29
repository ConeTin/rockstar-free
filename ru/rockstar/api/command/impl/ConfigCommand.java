package ru.rockstar.api.command.impl;

import com.mojang.realmsclient.gui.ChatFormatting;

import ru.rockstar.Main;
import ru.rockstar.api.command.CommandAbstract;
import ru.rockstar.api.utils.notifications.NotificationPublisher;
import ru.rockstar.api.utils.notifications.NotificationType;
import ru.rockstar.client.ui.settings.config.Config;
import ru.rockstar.client.ui.settings.config.ConfigManager;

public class ConfigCommand extends CommandAbstract {

    public ConfigCommand() {
        super("config", "configurations", "�6.config" + ChatFormatting.RED + " save | load | delete " + "�7<name>", "config", "cfg");
    }

    @Override
    public void execute(String... args) {
        try {
            if (args.length >= 2) {
                String upperCase = args[1].toUpperCase();
                if (args.length == 3) {
                    switch (upperCase) {
                        case "LOAD":
                            if (Main.instance.configManager.loadConfig(args[2])) {
                                Main.msg(ChatFormatting.GREEN + "������ \"" + args[2] + "\" ������� ��������",true);
                                NotificationPublisher.queue(ChatFormatting.GREEN + "������", ChatFormatting.GREEN + "������� ��������: \"" + args[2] + "\"",  NotificationType.SUCCESS);
                            } else {
                                Main.msg(ChatFormatting.RED + "������ �������� ������� \"" + args[2] + "\"",true);
                                NotificationPublisher.queue(ChatFormatting.RED + "������", ChatFormatting.RED + "�� ��������: \"" + args[2] + "\"",  NotificationType.ERROR);
                            }
                            break;
                        case "SAVE":
                            if (Main.instance.configManager.saveConfig(args[2])) {
                                Main.msg(ChatFormatting.GREEN + "������ \"" + args[2] + "\" ������� ��������",true);
                                NotificationPublisher.queue(ChatFormatting.GREEN + "������", ChatFormatting.GREEN + "������� ��������: \"" + args[2] + "\"", NotificationType.SUCCESS);
                                ConfigManager.getLoadedConfigs().clear();
                                Main.instance.configManager.load();
                            } else {
                                Main.msg(ChatFormatting.RED + "������ ���������� �������: \"" + args[2] + "\"",true);
                                NotificationPublisher.queue(ChatFormatting.RED + "������", ChatFormatting.RED + "�� ��������: \"" + args[2] + "\"",  NotificationType.ERROR);
                            }
                            break;
                        case "DELETE":
                            if (Main.instance.configManager.deleteConfig(args[2])) {
                                Main.msg(ChatFormatting.GREEN + "������ \"" + args[2] + "\" ������� ������",true);
                                NotificationPublisher.queue(ChatFormatting.GREEN + "������", ChatFormatting.GREEN + "������� ������: \"" + args[2] + "\"",  NotificationType.SUCCESS);
                            } else {
                                Main.msg(ChatFormatting.RED + "�� ������� ��������� ������: \"" + args[2] + "\"",true);
                                NotificationPublisher.queue(ChatFormatting.RED + "������", ChatFormatting.RED + "�� ������ :( \"" + args[2] + "\"",  NotificationType.ERROR);
                            }
                            break;
                    }
                } else if (args.length == 2 && upperCase.equalsIgnoreCase("LIST")) {
                    Main.msg(ChatFormatting.GREEN + "������:",true);
                    for (Config config : Main.instance.configManager.getContents()) {
                        Main.msg(ChatFormatting.RED + config.getName(),true);
                    }
                }
            } else {
                Main.msg(this.getUsage(),true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}