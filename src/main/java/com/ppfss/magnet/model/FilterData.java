// PPFSS_Magnet Plugin 
// Авторские права (c) 2026 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Material;

import java.util.EnumSet;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilterData {
    @SerializedName("enabled")
    private boolean enabled = false;

    @SerializedName("type")
    private FilterType type = FilterType.BLACKLIST;

    @SerializedName("blocklist")
    private EnumSet<Material> blocklist = EnumSet.noneOf(Material.class);
}
