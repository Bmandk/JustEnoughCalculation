package me.towdium.jecalculation;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class JecaConfig {

    public static File configDir = new File(
        Loader.instance()
            .getConfigDir(),
        "JustEnoughCalculation/");
    public static final File configFile = new File(configDir, "main.cfg");
    public static final File recordFile = new File(configDir, "record.json");
    public static final File defaultFile = new File(configDir, "default.json");

    public static final File dataDir = new File(configDir, "data/");

    public static File getDataFile(String group) {
        return new File(dataDir, group + ".json");
    }

    public static boolean initialized = false;
    public static Configuration config;

    public static boolean isClintMode() {
        return EnumItems.ClientMode.getProperty()
            .getBoolean();
    }

    public enum EnumItems {

        ClientMode,
        EnableInventoryCheck,
        ListRecipeBlackList,
        ListRecipeCategory;

        public String getComment() {
            switch (this) {
                case ClientMode:
                    return "Set to true to force client mode: no item and recipe allowed, use key bindings instead.";
                case EnableInventoryCheck:
                    return "Set to false to disable auto inventory check";
                case ListRecipeBlackList:
                    return "Add string identifier here to disable quick transfer of this type recipe\n"
                        + "Names can be found in ListRecipeCategory";
                case ListRecipeCategory:
                    return "List of categories, this is maintained by the mod automatically";
            }
            return "";
        }

        public String getName() {
            switch (this) {
                case ClientMode:
                    return "ClientMode";
                case EnableInventoryCheck:
                    return "EnableInventoryCheck";
                case ListRecipeBlackList:
                    return "ListRecipeBlackList";
                case ListRecipeCategory:
                    return "ListRecipeCategory";
            }
            return "";
        }

        public String getCategory() {
            switch (this) {
                case ClientMode:
                case EnableInventoryCheck:
                case ListRecipeBlackList:
                case ListRecipeCategory:
                    return EnumCategory.General.toString();
            }
            return "";
        }

        public EnumType getType() {
            switch (this) {
                case ClientMode:
                case EnableInventoryCheck:
                    return EnumType.Boolean;
                case ListRecipeBlackList:
                case ListRecipeCategory:
                    return EnumType.ListString;
            }
            return EnumType.Error;
        }

        public Object getDefault() {
            switch (this) {
                case ClientMode:
                case EnableInventoryCheck:
                    return true;
                case ListRecipeBlackList:
                    return new String[0];
                case ListRecipeCategory:
                    return new String[] { "minecraft.crafting", "minecraft.smelting" };
            }
            return JecaConfig.empty;
        }

        public Property init() {
            EnumType type = this.getType();
            if (type != null) {
                switch (this.getType()) {
                    case Boolean:
                        return config
                            .get(this.getCategory(), this.getName(), (Boolean) this.getDefault(), this.getComment());
                    case ListString:
                        return config
                            .get(this.getCategory(), this.getName(), (String[]) this.getDefault(), this.getComment());
                }
                config.getCategory(EnumCategory.General.toString())
                    .get(this.getName());
            }
            return config.get(this.getCategory(), this.getName(), false, this.getComment());
        }

        public Property getProperty() {
            return config.getCategory(EnumCategory.General.toString())
                .get(this.getName());
        }
    }

    public enum EnumCategory {

        General;

        @Override
        public String toString() {
            if (this == EnumCategory.General) {
                return "general";
            }
            return "";
        }
    }

    public enum EnumType {
        Boolean,
        ListString,
        Error
    }

    public static Object empty;

    public static void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(configFile, Tags.VERSION);
        config.load();
        handleInit();
        config.save();
    }

    public static void handleInit() {
        for (EnumItems item : EnumItems.values()) {
            item.init();
        }
    }

    public static void save() {
        config.save();
    }
}
