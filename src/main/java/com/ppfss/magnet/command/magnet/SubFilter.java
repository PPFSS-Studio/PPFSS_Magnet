// PPFSS_Magnet Plugin 
// ��������� ����� (c) 2026 PPFSS
// ��������: MIT

package com.ppfss.magnet.command.magnet;

import com.ppfss.libs.command.SubCommand;
import com.ppfss.libs.message.Placeholders;
import com.ppfss.magnet.config.Config;
import com.ppfss.magnet.config.MessageConfig;
import com.ppfss.magnet.model.FilterData;
import com.ppfss.magnet.model.FilterType;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SubFilter extends SubCommand {
    private final static List<String> MATERIALS = Arrays.stream(Material.values()).map(Enum::name).toList();
    private final MessageConfig messageConfig;
    private final Config config;
    
    public SubFilter() {
        super("filter");
        
        messageConfig = MessageConfig.getInstance();
        config = Config.getInstance();
    }

    @Override
    public List<String> complete(CommandSender sender, String... args) {
        if (!hasPermission(sender, null, null, args)) return null;

        return switch (args.length) {
            case 1 -> List.of("enable", "disable", "add", "remove", "status", "change");
            case 2 -> switch (args[0].toLowerCase()) {
                case "change" -> List.of("whitelist", "blacklist");
                case "add" -> MATERIALS;
                case "remove" -> config.getFilterData().getBlocklist().stream().map(Enum::name).toList();
                default -> new ArrayList<>();
            };
            default -> new ArrayList<>();
        };
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String... args) {
        if (args.length < 1) {
            sendUsage(sender, command, label, args);
            return;
        }

        FilterData filterData = getFilterData(sender);
        if (filterData == null) return;

        String action = args[0].toLowerCase();
        switch (action) {
            case "enable" -> handleEnable(sender, filterData, true);
            case "disable" -> handleEnable(sender, filterData, false);
            case "status" -> handleStatus(sender, filterData);
            case "change" -> handleChange(sender, filterData, args);
            case "add" -> handleAdd(sender, filterData, args);
            case "remove" -> handleRemove(sender, filterData, args);
            default -> sendUsage(sender, command, label, args);
        }
    }

    @Override
    public String getPermission(CommandSender sender, Command command, String label, String... args) {
        return "magnet.filter";
    }

    @Override
    public void noPermission(CommandSender sender, Command command, String label, String... args) {
        messageConfig.getNoPermission().send(sender);
    }

    @Override
    public void sendUsage(CommandSender sender, Command command, String label, String... args) {
        messageConfig.getFilterUsage().send(sender);
    }

    private FilterData getFilterData(CommandSender sender) {
        FilterData filterData = config.getFilterData();
        if (filterData == null) {
            sender.sendMessage("Filter config not initialized");
            return null;
        }
        return filterData;
    }

    private void handleEnable(CommandSender sender, FilterData filterData, boolean enabled) {
        filterData.setEnabled(enabled);
        config.save();
        if (enabled) {
            messageConfig.getFilterEnabled().send(sender);
        } else {
            messageConfig.getFilterDisabled().send(sender);
        }
    }

    private void handleStatus(CommandSender sender, FilterData filterData) {
        int count = filterData.getBlocklist() == null ? 0 : filterData.getBlocklist().size();
        List<String> items = filterData.getBlocklist() == null || filterData.getBlocklist().isEmpty()
                ? List.of("-")
                : filterData.getBlocklist().stream().map(Material::name).toList();
        String enabled = filterData.isEnabled() ? "<green>Включен" : "<red>Выключен";
        String type = filterData.getType() == null ? "<red>UNKNOWN" : "<yellow>" + filterData.getType().name();
        messageConfig.getFilterStatus().send(sender, Placeholders.of("enabled", enabled)
                .add("type", type)
                .add("count", String.valueOf(count))
                .add("items", items));
    }

    private void handleChange(CommandSender sender, FilterData filterData, String[] args) {
        if (args.length < 2) {
            messageConfig.getFilterUsage().send(sender);
            return;
        }

        FilterType type = FilterType.of(args[1]);
        if (type == null) {
            messageConfig.getFilterInvalidType().send(sender, Placeholders.of("type", args[1]));
            return;
        }
        filterData.setType(type);
        config.save();
        messageConfig.getFilterTypeChanged().send(sender, Placeholders.of("type", type.name()));
    }

    private void handleAdd(CommandSender sender, FilterData filterData, String[] args) {
        Material material;
        if (args.length >= 2) {
            material = parseMaterial(args[1]);
            if (material == null) {
                messageConfig.getFilterInvalidMaterial().send(sender, Placeholders.of("material", args[1]));
                return;
            }
        } else {
            if (!(sender instanceof Player player)) {
                messageConfig.getFilterUsage().send(sender);
                return;
            }
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType().isAir()) {
                messageConfig.getNoItemInHand().send(sender);
                return;
            }
            material = item.getType();
        }

        if (filterData.getBlocklist() == null) {
            filterData.setBlocklist(java.util.EnumSet.noneOf(Material.class));
        }

        if (filterData.getBlocklist().contains(material)) {
            messageConfig.getFilterItemExists().send(sender, Placeholders.of("material", material.name()));
            return;
        }

        filterData.getBlocklist().add(material);
        config.save();
        messageConfig.getFilterItemAdded().send(sender, Placeholders.of("material", material.name()));
    }

    private void handleRemove(CommandSender sender, FilterData filterData, String[] args) {
        if (args.length < 2) {
            messageConfig.getFilterUsage().send(sender);
            return;
        }
        Material material = parseMaterial(args[1]);
        if (material == null) {
            messageConfig.getFilterInvalidMaterial().send(sender, Placeholders.of("material", args[1]));
            return;
        }
        if (filterData.getBlocklist() == null || !filterData.getBlocklist().contains(material)) {
            messageConfig.getFilterItemNotFound().send(sender, Placeholders.of("material", material.name()));
            return;
        }
        filterData.getBlocklist().remove(material);
        config.save();
        messageConfig.getFilterItemRemoved().send(sender, Placeholders.of("material", material.name()));
    }

    private Material parseMaterial(String input) {
        if (input == null || input.isBlank()) return null;
        String key = input.trim().toUpperCase();
        if (key.contains(":")) {
            key = key.substring(key.indexOf(':') + 1);
        }

        Material material = Material.getMaterial(key);
        if (material != null) return material;
        return Material.matchMaterial(input);
    }
}
