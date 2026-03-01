// PPFSS_Magnet Plugin
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.config;

import com.google.gson.annotations.SerializedName;
import com.ppfss.libs.config.YamlConfig;
import com.ppfss.libs.config.YamlConfigLoader;
import com.ppfss.libs.message.Message;
import lombok.Getter;

@Getter
public class MessageConfig extends YamlConfig {
    private static MessageConfig instance;

    @SerializedName("magnet-activated")
    Message magnetActivated = new Message("<dark_green>[PPFSS]<green>Магнит активирован");

    @SerializedName("magnet-deactivated")
    Message magnetDeactivated = new Message("<dark_red>[PPFSS]<red>Магнит деактивирован");

    @SerializedName("no-permission")
    Message noPermission = new Message("<dark_red>[PPFSS]<red>Недостаточно прав!");

    @SerializedName("player-not-found")
    Message playerNotFound = new Message("<dark_red>[PPFSS]<red>Игрок <player> не найден!");

    @SerializedName("not-number")
    Message notNumber = new Message("<dark_red>[PPFSS]<red>Вы ввели не число!");

    @SerializedName("not-enough-space")
    Message notEnoughSpace = new Message("<dark_red>[PPFSS]<red>Недостаточно места!");

    @SerializedName("magnet-given")
    Message magnetGiven = new Message("<dark_green>[PPFSS]<green>Магнит выдан");

    @SerializedName("no-item-in-hand")
    Message noItemInHand = new Message("<dark_red>[PPFSS]<red>Нету предмета в основной руке!");

    @SerializedName("magnet-enchanted")
    Message magnetEnchanted = new Message("<dark_green>[PPFSS]<green>Магнит зачарован");

    @SerializedName("plugin-reloaded")
    Message pluginReloaded = new Message("<green>Конфиги плагина перезагружены");

    @SerializedName("plugin-reload-usage")
    Message pluginReloadUsage = new Message("<yellow>/magnet reload <white>- перезагружает конфиги");

    @SerializedName("filter-usage")
    Message filterUsage = new Message(
            "<yellow>/magnet filter enable|disable|status <white>- управление фильтром",
            "<yellow>/magnet filter add [Материал] <white>- добавить предмет в фильтр",
            "<yellow>/magnet filter remove <Материал> <white>- удалить предмет из фильтра",
            "<yellow>/magnet filter change <whitelist|blacklist> <white>- сменить режим фильтра"
    );

    @SerializedName("filter-invalid-material")
    Message filterInvalidMaterial = new Message("<dark_red>[PPFSS]<red>Материал не найден: <material>");

    @SerializedName("filter-invalid-type")
    Message filterInvalidType = new Message("<dark_red>[PPFSS]<red>Неверный тип фильтра: <type>");

    @SerializedName("filter-enabled")
    Message filterEnabled = new Message("<dark_green>[PPFSS]<green>Фильтр включен");

    @SerializedName("filter-disabled")
    Message filterDisabled = new Message("<dark_green>[PPFSS]<green>Фильтр выключен");

    @SerializedName("filter-type-changed")
    Message filterTypeChanged = new Message("<dark_green>[PPFSS]<green>Тип фильтра: <type>");

    @SerializedName("filter-item-added")
    Message filterItemAdded = new Message("<dark_green>[PPFSS]<green>Добавлено в фильтр: <material>");

    @SerializedName("filter-item-removed")
    Message filterItemRemoved = new Message("<dark_green>[PPFSS]<green>Удалено из фильтра: <material>");

    @SerializedName("filter-item-exists")
    Message filterItemExists = new Message("<dark_red>[PPFSS]<red>Уже в фильтре: <material>");

    @SerializedName("filter-item-not-found")
    Message filterItemNotFound = new Message("<dark_red>[PPFSS]<red>Этого предмета нет в фильтре: <material>");

    @SerializedName("filter-status")
    Message filterStatus = new Message(
            "<gold>====== <yellow>Фильтр <gold>======",
            "<gray>Состояние: <enabled>",
            "<gray>Режим: <type>",
            "<gray>Кол-во: <count>",
            "<gray>Список:",
            "<yellow>- <items>",
            "<gold>======================"
    );

    public static void load(YamlConfigLoader loader) {
        instance = loader.loadConfig("messages", MessageConfig.class);
    }

    public static MessageConfig getInstance() {
        if (instance == null) throw new RuntimeException("Config has not been initialized yet");
        return instance;
    }
}
