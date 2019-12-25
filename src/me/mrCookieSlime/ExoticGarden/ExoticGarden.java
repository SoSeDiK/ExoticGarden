package me.mrCookieSlime.ExoticGarden;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomPotion;
import me.mrCookieSlime.CSCoreLibPlugin.general.World.CustomSkull;
import me.mrCookieSlime.ExoticGarden.items.Crook;
import me.mrCookieSlime.ExoticGarden.items.GrassSeeds;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.Lists.Categories;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.HandledBlock;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.Juice;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.bstats.bukkit.Metrics;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import me.mrCookieSlime.Slimefun.cscorelib2.updater.BukkitUpdater;
import me.mrCookieSlime.Slimefun.cscorelib2.updater.GitHubBuildsUpdater;
import me.mrCookieSlime.Slimefun.cscorelib2.updater.Updater;

public class ExoticGarden extends JavaPlugin {

	public static ExoticGarden instance;

	private List<Berry> berries = new ArrayList<>();
	private List<Tree> trees = new ArrayList<>();
	private Map<String, ItemStack> items = new HashMap<>();

	protected Config cfg;

	private Category category_main;
	private Category category_food;
	private Category category_drinks;
	private Category category_magic;
	private Kitchen kitchen;

	@Override
	public void onEnable() {
		if (!new File("plugins/ExoticGarden").exists()) new File("plugins/ExoticGarden").mkdirs();
    	if (!new File("plugins/ExoticGarden/schematics").exists()) new File("plugins/ExoticGarden/schematics").mkdirs();

    	instance = this;
    	cfg = new Config(this);

		// Setting up bStats
		new Metrics(this);

		// Setting up the Auto-Updater
		Updater updater;

		if (!getDescription().getVersion().startsWith("DEV - ")) {
			// We are using an official build, use the BukkitDev Updater
			updater = new BukkitUpdater(this, getFile(), 88425);
		}
		else {
			// If we are using a development build, we want to switch to our custom
			updater = new GitHubBuildsUpdater(this, getFile(), "TheBusyBiscuit/ExoticGarden/master");
		}

		// Only run the Updater if it has not been disabled
		if (cfg.getBoolean("options.auto-update")) updater.start();

		try {
			category_main = new Category(new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTVhNWM0YTBhMTZkYWJjOWIxZWM3MmZjODNlMjNhYzE1ZDAxOTdkZTYxYjEzOGJhYmNhN2M4YTI5YzgyMCJ9fX0="), "&aЭкзотические растения и фрукты", "", "&a> Нажмите, чтобы открыть"));
			category_food = new Category(new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&aЭкзотическая еда", "", "&a> Нажмите, чтобы открыть"));
			category_drinks = new Category(new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmE4ZjFmNzBlODU4MjU2MDdkMjhlZGNlMWEyYWQ0NTA2ZTczMmI0YTUzNDVhNWVhNmU4MDdjNGIzMTNlODgifX19"), "&aЭкзотические напитки", "", "&a> Нажмите, чтобы открыть"));
			category_magic = new Category(new CustomItem(Material.BLAZE_POWDER, "&5Магические растения", "", "&a> Нажмите, чтобы открыть"));

			SlimefunItemStack iceCube = new SlimefunItemStack("ICE_CUBE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM0MGJlZjJjMmMzM2QxMTNiYWM0ZTZhMWE4NGQ1ZmZjZWNiYmZhYjZiMzJmYTdhN2Y3NjE5NTQ0MmJkMWEyIn19fQ==", "&bКубик льда");
			new SlimefunItem(Categories.MISC, iceCube, RecipeType.GRIND_STONE,
			new ItemStack[] {new ItemStack(Material.ICE), null, null, null, null, null, null, null, null}, new CustomItem(iceCube, 4))
			.register();
		} catch (Exception e) {
			e.printStackTrace();
		}

		kitchen = new Kitchen(this);

		registerBerry("GRAPE", "Виноград", "Виноградный куст", "Виноградный сок", "Виноградный смузи", "Виноградный сэндвич", "Виноградный пирог", "&c", Color.RED, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmVlOTc2NDliZDk5OTk1NTQxM2ZjYmYwYjI2OWM5MWJlNDM0MmIxMGQwNzU1YmFkN2ExN2U5NWZjZWZkYWIwIn19fQ=="));
		registerBerry("BLUEBERRY", "Черника", "Черничный куст", "Черничный сок", "Черничный смузи", "Черничный сэндвич", "Черничный пирог", "&9", Color.BLUE, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTVhNWM0YTBhMTZkYWJjOWIxZWM3MmZjODNlMjNhYzE1ZDAxOTdkZTYxYjEzOGJhYmNhN2M4YTI5YzgyMCJ9fX0="));
		registerBerry("ELDERBERRY", "Ягода бузины", "Куст ягоды бузины", "Сок из ягоды бузины", "Смузи из ягоды бузины", "Сэндвич из ягоды бузины", "Пирог из ягоды бузины", "&c", Color.FUCHSIA, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWU0ODgzYTFlMjJjMzI0ZTc1MzE1MWUyYWM0MjRjNzRmMWNjNjQ2ZWVjOGVhMGRiMzQyMGYxZGQxZDhiIn19fQ=="));
		registerBerry("RASPBERRY", "Малина", "Малиновый куст", "Малиновый сок", "Малиновый смузи", "Малиновый сэндвич", "Малиновый пирог", "&d", Color.FUCHSIA, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODI2MmM0NDViYzJkZDFjNWJiYzhiOTNmMjQ4MmY5ZmRiZWY0OGE3MjQ1ZTFiZGIzNjFkNGE1NjgxOTBkOWI1In19fQ=="));
		registerBerry("BLACKBERRY", "Ежевика", "Куст ежевики", "Сок из ежевики", "Смузи из ежевики", "Сэндвич из ежевики", "Пирог из ежевики", "&8", Color.GRAY, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc2OWY4Yjc4YzQyZTI3MmE2NjlkNmU2ZDE5YmE4NjUxYjcxMGFiNzZmNmI0NmQ5MDlkNmEzZDQ4Mjc1NCJ9fX0="));
		registerBerry("CRANBERRY", "Клюква", "Клюквенный куст", "Клюквенный сок", "Клюквенный смузи", "Клюквенный сэндвич", "Клюквенный пирог", "&c", Color.FUCHSIA, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDVmZTZjNzE4ZmJhNzE5ZmY2MjIyMzdlZDllYTY4MjdkMDkzZWZmYWI4MTRiZTIxOTJlOTY0M2UzZTNkNyJ9fX0="));
		registerBerry("COWBERRY", "Брусника", "Брусничный куст", "Брусничный сок", "Брусничный смузи", "Брусничный сэндвич", "Брусничный пирог", "&c", Color.FUCHSIA, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTA0ZTU0YmYyNTVhYjBiMWM0OThjYTNhMGNlYWU1YzdjNDVmMTg2MjNhNWEwMmY3OGE3OTEyNzAxYTMyNDkifX19"));
		registerBerry("STRAWBERRY", "Клубника", "Клубничный куст", "Клубничный сок", "Клубничный смузи", "Клубничный сэндвич", "Клубничный пирог", "&4", Color.FUCHSIA, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JjODI2YWFhZmI4ZGJmNjc4ODFlNjg5NDQ0MTRmMTM5ODUwNjRhM2Y4ZjA0NGQ4ZWRmYjQ0NDNlNzZiYSJ9fX0="));

		registerPlant("TOMATO", "Помидорный росток", "Помидор", "&4", PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTkxNzIyMjZkMjc2MDcwZGMyMWI3NWJhMjVjYzJhYTU2NDlkYTVjYWM3NDViYTk3NzY5NWI1OWFlYmQifX19"));
		registerPlant("LETTUCE", "Салатовый росток", "Салат", "&2", PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDc3ZGQ4NDJjOTc1ZDhmYjAzYjFhZGQ2NmRiODM3N2ExOGJhOTg3MDUyMTYxZjIyNTkxZTZhNGVkZTdmNSJ9fX0="));
		registerPlant("TEA_LEAF", "Росток чайного листа", "Чайный лист", "&a", PlantType.DOUBLE_PLANT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTUxNGM4YjQ2MTI0N2FiMTdmZTM2MDZlNmUyZjRkMzYzZGNjYWU5ZWQ1YmVkZDAxMmI0OThkN2FlOGViMyJ9fX0="));
		registerPlant("CABBAGE", "Капустный росток", "Капуста", "&2",  PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmNkNmQ2NzMyMGM5MTMxYmU4NWExNjRjZDdjNWZjZjI4OGYyOGMyODE2NTQ3ZGIzMGEzMTg3NDE2YmRjNDViIn19fQ=="));
		registerPlant("SWEET_POTATO", "Росток сладкой картошки", "Сладкая картошка", "&6",  PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2ZmNDg1NzhiNjY4NGUxNzk5NDRhYjFiYzc1ZmVjNzVmOGZkNTkyZGZiNDU2ZjZkZWY3NjU3NzEwMWE2NiJ9fX0="));
		registerPlant("MUSTARD_SEED", "Росток горчичного семени", "Горчичное семя", "&e",  PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWQ1M2E0MjQ5NWZhMjdmYjkyNTY5OWJjM2U1ZjI5NTNjYzJkYzMxZDAyN2QxNGZjZjdiOGMyNGI0NjcxMjFmIn19fQ=="));
		registerPlant("CURRY_LEAF", "Росток листьев карри", "Лист карри", "&2",  PlantType.DOUBLE_PLANT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzJhZjdmYThiZGYzMjUyZjY5ODYzYjIwNDU1OWQyM2JmYzJiOTNkNDE0MzcxMDM0MzdhYjE5MzVmMzIzYTMxZiJ9fX0="));
		registerPlant("ONION", "Росток лука", "Лук", "&c",  PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmNlMDM2ZTMyN2NiOWQ0ZDhmZWYzNjg5N2E4OTYyNGI1ZDliMThmNzA1Mzg0Y2UwZDdlZDFlMWZjN2Y1NiJ9fX0="));
		registerPlant("GARLIC", "Чесночный росток", "Чеснок", "&r",  PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzA1MmQ5YzExODQ4ZWJjYzlmODM0MDMzMjU3N2JmMWQyMmI2NDNjMzRjNmFhOTFmZTRjMTZkNWE3M2Y2ZDgifX19"));
		registerPlant("CILANTRO", "Росток кориандра", "Кориандр", "&a",  PlantType.DOUBLE_PLANT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTYxNDkxOTZmM2E4ZDZkNmYyNGU1MWIyN2U0Y2I3MWM2YmFiNjYzNDQ5ZGFmZmI3YWEyMTFiYmU1NzcyNDIifX19"));
		registerPlant("BLACK_PEPPER", "Росток чёрного перца", "Чёрный перец", "&8",  PlantType.DOUBLE_PLANT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjM0MmI5YmY5ZjFmNjI5NTg0MmIwZWZiNTkxNjk3YjE0NDUxZjgwM2ExNjVhZTU4ZDBkY2ViZDk4ZWFjYyJ9fX0="));

		registerPlant("CORN", "Кукурузный росток", "Кукуруза", "&6",  PlantType.DOUBLE_PLANT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWJkMzgwMmU1ZmFjMDNhZmFiNzQyYjBmM2NjYTQxYmNkNDcyM2JlZTkxMWQyM2JlMjljZmZkNWI5NjVmMSJ9fX0="));
		registerPlant("PINEAPPLE", "Ананасовый росток", "Ананас", "&6",  PlantType.DOUBLE_PLANT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdlZGRkODJlNTc1ZGZkNWI3NTc5ZDg5ZGNkMjM1MGM5OTFmMDQ4M2E3NjQ3Y2ZmZDNkMmM1ODdmMjEifX19"));

		registerTree("OAK_APPLE", "Яблочное", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JiMzExZjNiYTFjMDdjM2QxMTQ3Y2QyMTBkODFmZTExZmQ4YWU5ZTNkYjIxMmEwZmE3NDg5NDZjMzYzMyJ9fX0=", "Яблоко", "&c", Color.FUCHSIA, "Яблочный сок", "Яблочный пирог", Material.DIRT, Material.GRASS_BLOCK);
		registerTree("COCONUT", "Кокосовое", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQyN2RlZDU3Yjk0Y2Y3MTViMDQ4ZWY1MTdhYjNmODViZWY1YTdiZTY5ZjE0YjE1NzNlMTRlN2U0MmUyZTgifX19", "Кокос", "&6", Color.MAROON, "Кокосовое молоко", null, Material.SAND);
		registerTree("CHERRY", "Вишнёвое", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUyMDc2NmI4N2QyNDYzYzM0MTczZmZjZDU3OGIwZTY3ZDE2M2QzN2EyZDdjMmU3NzkxNWNkOTExNDRkNDBkMSJ9fX0=", "Вишня", "&c", Color.FUCHSIA, "Вишнёвый сок", "Вишнёвый пирог", Material.DIRT, Material.GRASS_BLOCK);
		registerTree("POMEGRANATE", "Гранатовое", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JiMzExZjNiYTFjMDdjM2QxMTQ3Y2QyMTBkODFmZTExZmQ4YWU5ZTNkYjIxMmEwZmE3NDg5NDZjMzYzMyJ9fX0=", "Гранат", "&4", Color.RED, "Гранатовый сок", "Гранатовый пирог", Material.DIRT, Material.GRASS_BLOCK);
		registerTree("LEMON", "Лимонное", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTU3ZmQ1NmNhMTU5Nzg3NzkzMjRkZjUxOTM1NGI2NjM5YThkOWJjMTE5MmM3YzNkZTkyNWEzMjliYWVmNmMifX19", "Лимон", "&e", Color.YELLOW, "Лимонный сок", "Лимонный пирог", Material.DIRT, Material.GRASS_BLOCK);
		registerTree("PLUM", "Сливовое", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjlkNjY0MzE5ZmYzODFiNGVlNjlhNjk3NzE1Yjc2NDJiMzJkNTRkNzI2Yzg3ZjY0NDBiZjAxN2E0YmNkNyJ9fX0=", "Слива", "&5", Color.RED, "Сливовый сок", "Сливовый пирог", Material.DIRT, Material.GRASS_BLOCK);
		registerTree("LIME", "Лаймовое", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWE1MTUzNDc5ZDlmMTQ2YTVlZTNjOWUyMThmNWU3ZTg0YzRmYTM3NWU0Zjg2ZDMxNzcyYmE3MWY2NDY4In19fQ==", "Лайм", "&a", Color.LIME, "Лаймовый сок", "Лаймовый пирог", Material.DIRT, Material.GRASS_BLOCK);
		registerTree("ORANGE", "Апельсиновое", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjViMWRiNTQ3ZDFiNzk1NmQ0NTExYWNjYjE1MzNlMjE3NTZkN2NiYzM4ZWI2NDM1NWEyNjI2NDEyMjEyIn19fQ==", "Апельсин", "&6", Color.ORANGE, "Апельсиновый сок", "Апельсиновый пирог", Material.DIRT, Material.GRASS_BLOCK);
		registerTree("PEACH", "Персиковое", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDNiYTQxZmU4Mjc1Nzg3MWU4Y2JlYzlkZWQ5YWNiZmQxOTkzMGQ5MzM0MWNmODEzOWQxZGZiZmFhM2VjMmE1In19fQ==", "Персик", "&5", Color.RED, "Персиковый сок", "Персиковый пирог", Material.DIRT, Material.GRASS_BLOCK);
		registerTree("PEAR", "Грушевое", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmRlMjhkZjg0NDk2MWE4ZWNhOGVmYjc5ZWJiNGFlMTBiODM0YzY0YTY2ODE1ZThiNjQ1YWVmZjc1ODg5NjY0YiJ9fX0=", "Груша", "&a", Color.LIME, "Грушевый сок", "Грушевый пирог", Material.DIRT, Material.GRASS_BLOCK);
		registerTree("DRAGON_FRUIT", "Питайевое", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODQ3ZDczYTkxYjUyMzkzZjJjMjdlNDUzZmI4OWFiM2Q3ODQwNTRkNDE0ZTM5MGQ1OGFiZDIyNTEyZWRkMmIifX19\\", "Питайя", "&d", Color.FUCHSIA, "Сок из питайи", "Пирог из питайи", Material.DIRT, Material.GRASS_BLOCK);

		registerDishes();

		registerMagicalPlant("COAL", "уголь", "Угольное магическое растение", new ItemStack(Material.COAL, 2), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc4OGY1ZGRhZjUyYzU4NDIyODdiOTQyN2E3NGRhYzhmMDkxOWViMmZkYjFiNTEzNjVhYjI1ZWIzOTJjNDcifX19",
		new ItemStack[] {null, new ItemStack(Material.COAL_ORE), null, new ItemStack(Material.COAL_ORE), new ItemStack(Material.WHEAT_SEEDS), new ItemStack(Material.COAL_ORE), null, new ItemStack(Material.COAL_ORE), null});

		registerMagicalPlant("IRON", "железо", "Железное магическое растение", new ItemStack(Material.IRON_INGOT), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGI5N2JkZjkyYjYxOTI2ZTM5ZjVjZGRmMTJmOGY3MTMyOTI5ZGVlNTQxNzcxZTBiNTkyYzhiODJjOWFkNTJkIn19fQ==",
		new ItemStack[] {null, new ItemStack(Material.IRON_BLOCK), null, new ItemStack(Material.IRON_BLOCK), getItem("COAL_PLANT"), new ItemStack(Material.IRON_BLOCK), null, new ItemStack(Material.IRON_BLOCK), null});

		registerMagicalPlant("GOLD", "золото", "Золотое магическое растение", SlimefunItems.GOLD_4K, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTRkZjg5MjI5M2E5MjM2ZjczZjQ4ZjllZmU5NzlmZTA3ZGJkOTFmN2I1ZDIzOWU0YWNmZDM5NGY2ZWNhIn19fQ==",
		new ItemStack[] {null, SlimefunItems.GOLD_16K, null, SlimefunItems.GOLD_16K, getItem("IRON_PLANT"), SlimefunItems.GOLD_16K, null, SlimefunItems.GOLD_16K, null});

		registerMagicalPlant("COPPER", "медь", "Медное магическое растение", new CustomItem(SlimefunItems.COPPER_DUST, 8),  "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTkyM2RiZmQ4ZjMxMTI2OTBiYjVhNjE2OGE4ZDNjYTVhYjllN2Q0M2IxZDExY2ZjYjY0M2RlN2RmZTIxIn19fQ==",
		new ItemStack[] {null, SlimefunItems.COPPER_DUST, null, SlimefunItems.COPPER_DUST, getItem("GOLD_PLANT"), SlimefunItems.COPPER_DUST, null, SlimefunItems.COPPER_DUST, null});

		registerMagicalPlant("REDSTONE", "редстоун", "Редстоуновое магическое растение", new ItemStack(Material.REDSTONE, 8), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZThkZWVlNTg2NmFiMTk5ZWRhMWJkZDc3MDdiZGI5ZWRkNjkzNDQ0ZjFlM2JkMzM2YmQyYzc2NzE1MWNmMiJ9fX0=",
		new ItemStack[] {null, new ItemStack(Material.REDSTONE_BLOCK), null, new ItemStack(Material.REDSTONE_BLOCK), getItem("GOLD_PLANT"), new ItemStack(Material.REDSTONE_BLOCK), null, new ItemStack(Material.REDSTONE_BLOCK), null});

		registerMagicalPlant("LAPIS", "лазурит", "Лазуритовое магическое растение", new ItemStack(Material.LAPIS_LAZULI, 16), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFhMGQwZmVhMWFmYWVlMzM0Y2FiNGQyOWQ4Njk2NTJmNTU2M2M2MzUyNTNjMGNiZWQ3OTdlZDNjZjU3ZGUwIn19fQ==",
		new ItemStack[] {null, new ItemStack(Material.LAPIS_ORE), null, new ItemStack(Material.LAPIS_ORE), getItem("REDSTONE_PLANT"), new ItemStack(Material.LAPIS_ORE), null, new ItemStack(Material.LAPIS_ORE), null});

		registerMagicalPlant("ENDER", "эндер-частицы", "Магическое эндер-растение", new ItemStack(Material.ENDER_PEARL, 4), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGUzNWFhZGU4MTI5MmU2ZmY0Y2QzM2RjMGVhNmExMzI2ZDA0NTk3YzBlNTI5ZGVmNDE4MmIxZDE1NDhjZmUxIn19fQ==",
		new ItemStack[] {null, new ItemStack(Material.ENDER_PEARL), null, new ItemStack(Material.ENDER_PEARL), getItem("LAPIS_PLANT"), new ItemStack(Material.ENDER_PEARL), null, new ItemStack(Material.ENDER_PEARL), null});

		registerMagicalPlant("QUARTZ", "Незер-кварц", "Кварцевое магическое растение", new ItemStack(Material.QUARTZ, 8), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjZkZTU4ZDU4M2MxMDNjMWNkMzQ4MjQzODBjOGE0NzdlODk4ZmRlMmViOWE3NGU3MWYxYTk4NTA1M2I5NiJ9fX0=",
		new ItemStack[] {null, new ItemStack(Material.NETHER_QUARTZ_ORE), null, new ItemStack(Material.NETHER_QUARTZ_ORE), getItem("ENDER_PLANT"), new ItemStack(Material.NETHER_QUARTZ_ORE), null, new ItemStack(Material.NETHER_QUARTZ_ORE), null});

		registerMagicalPlant("DIAMOND", "алмаз", "Алмазное магическое растение", new ItemStack(Material.DIAMOND), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjg4Y2Q2ZGQ1MDM1OWM3ZDU4OThjN2M3ZTNlMjYwYmZjZDNkY2IxNDkzYTg5YjllODhlOWNiZWNiZmU0NTk0OSJ9fX0=",
		new ItemStack[] {null, new ItemStack(Material.DIAMOND), null, new ItemStack(Material.DIAMOND), getItem("QUARTZ_PLANT"), new ItemStack(Material.DIAMOND), null, new ItemStack(Material.DIAMOND), null});

		registerMagicalPlant("EMERALD", "изумруд", "Изумрудное магическое растение", new ItemStack(Material.EMERALD), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGZjNDk1ZDFlNmViNTRhMzg2MDY4YzZjYjEyMWM1ODc1ZTAzMWI3ZjYxZDcyMzZkNWYyNGI3N2RiN2RhN2YifX19",
		new ItemStack[] {null, new ItemStack(Material.EMERALD), null, new ItemStack(Material.EMERALD), getItem("DIAMOND_PLANT"), new ItemStack(Material.EMERALD), null, new ItemStack(Material.EMERALD), null});

		registerMagicalPlant("GLOWSTONE", "светокамень", "Светокаменное магическое растение", new ItemStack(Material.GLOWSTONE_DUST, 8), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjVkN2JlZDhkZjcxNGNlYTA2M2U0NTdiYTVlODc5MzExNDFkZTI5M2RkMWQ5YjkxNDZiMGY1YWIzODM4NjYifX19",
		new ItemStack[] {null, new ItemStack(Material.GLOWSTONE), null, new ItemStack(Material.GLOWSTONE), getItem("REDSTONE_PLANT"), new ItemStack(Material.GLOWSTONE), null, new ItemStack(Material.GLOWSTONE), null});

		registerMagicalPlant("OBSIDIAN", "обсидиан", "Обсидиановое магическое растение", new ItemStack(Material.OBSIDIAN, 2), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzg0MGI4N2Q1MjI3MWQyYTc1NWRlZGM4Mjg3N2UwZWQzZGY2N2RjYzQyZWE0NzllYzE0NjE3NmIwMjc3OWE1In19fQ==",
		new ItemStack[] {null, new ItemStack(Material.OBSIDIAN), null, new ItemStack(Material.OBSIDIAN), getItem("LAPIS_PLANT"), new ItemStack(Material.OBSIDIAN), null, new ItemStack(Material.OBSIDIAN), null});

		registerMagicalPlant("SLIME", "слизь", "Слизневое магическое растение", new ItemStack(Material.SLIME_BALL, 8), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTBlNjVlNmU1MTEzYTUxODdkYWQ0NmRmYWQzZDNiZjg1ZThlZjgwN2Y4MmFhYzIyOGE1OWM0YTk1ZDZmNmEifX19",
		new ItemStack[] {null, new ItemStack(Material.SLIME_BALL), null, new ItemStack(Material.SLIME_BALL), getItem("ENDER_PLANT"), new ItemStack(Material.SLIME_BALL), null, new ItemStack(Material.SLIME_BALL), null});

		new Crook(Categories.TOOLS, new SlimefunItemStack("CROOK", new CustomItem(Material.WOODEN_HOE, "&rВетка дерева", "", "&7+ &b25% &7к частоте выпадения сажанцев")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {new ItemStack(Material.STICK), new ItemStack(Material.STICK), null, null, new ItemStack(Material.STICK), null, null, new ItemStack(Material.STICK), null})
		.register();

		SlimefunItemStack grass_seeds = new SlimefunItemStack("GRASS_SEEDS", Material.PUMPKIN_SEEDS, "&rСемена травы", "", "&7&oМогут быть посажены на земле");
		new GrassSeeds(category_main, grass_seeds, new RecipeType(new CustomItem(Material.GRASS, "&7Выпадают при разрушении травы")),
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register();

		new PlantsListener(this);
		new FoodListener(this);

		items.put("WHEAT_SEEDS", new ItemStack(Material.WHEAT_SEEDS));
		items.put("PUMPKIN_SEEDS", new ItemStack(Material.PUMPKIN_SEEDS));
		items.put("MELON_SEEDS", new ItemStack(Material.MELON_SEEDS));
		items.put("OAK_SAPLING", new ItemStack(Material.OAK_SAPLING));
		items.put("SPRUCE_SAPLING", new ItemStack(Material.SPRUCE_SAPLING));
		items.put("BIRCH_SAPLING", new ItemStack(Material.BIRCH_SAPLING));
		items.put("JUNGLE_SAPLING", new ItemStack(Material.JUNGLE_SAPLING));
		items.put("ACACIA_SAPLING", new ItemStack(Material.ACACIA_SAPLING));
		items.put("DARK_OAK_SAPLING", new ItemStack(Material.DARK_OAK_SAPLING));
		items.put("GRASS_SEEDS", grass_seeds);

		Iterator<String> iterator = items.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			cfg.setDefaultValue("grass-drops." + key, true);
			if (!cfg.getBoolean("grass-drops." + key)) iterator.remove();
		}
		cfg.save();
	}

	private void registerDishes() {
		new Juice(category_drinks, new SlimefunItemStack("LIME_SMOOTHIE", new CustomPotion("&aЛаймовый смузи", Color.LIME, new PotionEffect(PotionEffectType.SATURATION, 10, 0), "", "&7&oВосстанавливает &b&o" + "5.0 единиц" + " &7&oголода")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("LIME_JUICE"), getItem("ICE_CUBE"), null, null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new SlimefunItemStack("TOMATO_JUICE", new CustomPotion("&4Томатный сок", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&oВосстанавливает &b&o" + "3.0 единицы" + " &7&oголода")), RecipeType.JUICER,
		new ItemStack[] {getItem("TOMATO"), null, null, null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new SlimefunItemStack("WINE", new CustomPotion("&cВино", Color.RED, new PotionEffect(PotionEffectType.SATURATION, 10, 0), "", "&7&oВосстанавливает &b&o" + "5.0 единиц" + " &7&oголода")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("GRAPE"), new ItemStack(Material.SUGAR), null, null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new SlimefunItemStack("LEMON_ICED_TEA", new CustomPotion("&eОхлаждённый лимонный чай", Color.YELLOW, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&oВосстанавливает &b&o" + "6.5 единиц" + " &7&oголода")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("LEMON"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new SlimefunItemStack("RASPBERRY_ICED_TEA", new CustomPotion("&dОхлаждённый малиновый чай", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&oВосстанавливает &b&o" + "6.5 единиц" + " &7&oголода")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("RASPBERRY"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new SlimefunItemStack("PEACH_ICED_TEA", new CustomPotion("&dОхлаждённый персиковый чай", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&oВосстанавливает &b&o" + "6.5 единиц" + " &7&oголода")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("PEACH"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new SlimefunItemStack("STRAWBERRY_ICED_TEA", new CustomPotion("&4Охлаждённый клубничный чай", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&oВосстанавливает &b&o" + "6.5 единиц" + " &7&oголода")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("STRAWBERRY"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new SlimefunItemStack("CHERRY_ICED_TEA", new CustomPotion("&cОхлаждённый вишнёвый чай", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&oВосстанавливает &b&o" + "6.5 единиц" + " &7&oголода")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("CHERRY"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new SlimefunItemStack("THAI_TEA", new CustomPotion("&6Тайский чай", Color.RED, new PotionEffect(PotionEffectType.SATURATION, 14, 0), "", "&7&oВосстанавливает &b&o" + "7.0 единиц" + " &7&oголода")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("TEA_LEAF"), new ItemStack(Material.SUGAR), SlimefunItems.HEAVY_CREAM, getItem("COCONUT_MILK"), null, null, null, null, null})
		.register();

		new CustomFood(category_food, new SlimefunItemStack("PUMPKIN_BREAD", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjM0ODdkNDU3ZjkwNjJkNzg3YTNlNmNlMWM0NjY0YmY3NDAyZWM2N2RkMTExMjU2ZjE5YjM4Y2U0ZjY3MCJ9fX0=", "&rТыквенный хлеб", "", "&7&oВосстанавливает &b&o" + "4.0 единицы" + " &7&oголода"),
		new ItemStack[] {new ItemStack(Material.PUMPKIN), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, null, null, null, null, null, null},
		8)
		.register();

		new EGPlant(Categories.MISC, new SlimefunItemStack("MAYO", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Y4ZDUzNmM4YzJjMjU5NmJjYzE3MDk1OTBhOWQ3ZTMzMDYxYzU2ZTY1ODk3NGNkODFiYjgzMmVhNGQ4ODQyIn19fQ==", "&rМайонез"), RecipeType.GRIND_STONE, false,
		new ItemStack[] {new ItemStack(Material.EGG), null, null, null, null, null, null, null, null})
		.register();

		new EGPlant(Categories.MISC, new SlimefunItemStack("MUSTARD", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWI5ZTk5NjIxYjk3NzNiMjllMzc1ZTYyYzY0OTVmZjFhYzg0N2Y4NWIyOTgxNmMyZWI3N2I1ODc4NzRiYTYyIn19fQ==", "&eГорчица"), RecipeType.GRIND_STONE, false,
		new ItemStack[] {getItem("MUSTARD_SEED"), null, null, null, null, null, null, null, null})
		.register();

		new EGPlant(Categories.MISC, new SlimefunItemStack("BBQ_SAUCE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTg2ZjE5YmYyM2QyNDhlNjYyYzljOGI3ZmExNWVmYjhhMWYxZDViZGFjZDNiODYyNWE5YjU5ZTkzYWM4YSJ9fX0=", "&cСоус для барбекю"), RecipeType.ENHANCED_CRAFTING_TABLE, false,
		new ItemStack[] {getItem("TOMATO"), getItem("MUSTARD"), getItem("SALT"), new ItemStack(Material.SUGAR), null, null, null, null, null})
		.register();

		new SlimefunItem(Categories.MISC, new SlimefunItemStack("VEGETABLE_OIL", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFjYjI4ZmI4YTMxMDQ0M2FmMDJjN2ExMjgzYWNlOTVhOTkwNmIyZTBlNmYzNjM2NTk3ZWRiZThjYWQ0ZSJ9fX0=", "&rРастительное масло"), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {new ItemStack(Material.BEETROOT_SEEDS), new ItemStack(Material.WATER_BUCKET), null, null, null, null, null, null, null})
		.register();

		new SlimefunItem(Categories.MISC, new SlimefunItemStack("CORNMEAL", Material.SUGAR, "&rОвсяная мука"), RecipeType.GRIND_STONE,
		new ItemStack[] {getItem("CORN"), null, null, null, null, null, null, null, null})
		.register();

		new SlimefunItem(Categories.MISC, new SlimefunItemStack("YEAST", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjA2YmUyZGYyMTIyMzQ0YmRhNDc5ZmVlY2UzNjVlZTBlOWQ1ZGEyNzZhZmEwZThjZThkODQ4ZjM3M2RkMTMxIn19fQ==", "&rДрожжи"), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {new ItemStack(Material.SUGAR), new ItemStack(Material.WATER_BUCKET), null, null, null, null, null, null, null})
		.register();

		new SlimefunItem(Categories.MISC, new SlimefunItemStack("MOLASSES", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjIxZDdiMTU1ZWRmNDQwY2I4N2VjOTQ0ODdjYmE2NGU4ZDEyODE3MWViMTE4N2MyNmQ1ZmZlNThiZDc5NGMifX19", "&8Меласса"), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {new ItemStack(Material.BEETROOT), new ItemStack(Material.SUGAR_CANE), new ItemStack(Material.WATER_BUCKET), null, null, null, null, null, null})
		.register();

		new SlimefunItem(Categories.MISC, new SlimefunItemStack("BROWN_SUGAR", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTY0ZDQyNDcyNzhlMTQ5ODM3NGFhNmIwZTQ3MzY4ZmU0ZjEzOGFiYzk0ZTU4M2U4ODM5OTY1ZmJlMjQxYmUifX19", "&rКоричневый сахар"), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {new ItemStack(Material.SUGAR), getItem("MOLASSES"), null, null, null, null, null, null, null})
		.register();

		new SlimefunItem(Categories.MISC, new SlimefunItemStack("COUNTRY_GRAVY", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjIxZmE5NDM5YmZkODM4NDQ2NDE0NmY5YzY3ZWJkNGM1ZmJmNDE5NjkyNDg5MjYyN2VhZGYzYmNlMWZmIn19fQ==", "&rДеревенский соус"), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {SlimefunItems.WHEAT_FLOUR, new ItemStack(Material.SUGAR), getItem("BLACK_PEPPER"), null, null, null, null, null, null})
		.register();

		new CustomFood(category_food, new SlimefunItemStack("CHOCOLATE_BAR", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODE5Zjk0OGQxNzcxOGFkYWNlNWRkNmUwNTBjNTg2MjI5NjUzZmVmNjQ1ZDcxMTNhYjk0ZDE3YjYzOWNjNDY2In19fQ==", "&rШоколадная плитка", "", "&7&oВосстанавливает &b&o" + "1.5 единицы" + " &7&oголода"),
		new ItemStack[] {new ItemStack(Material.COCOA_BEANS), SlimefunItems.HEAVY_CREAM, null, null, null, null, null, null, null},
		3)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("POTATO_SALAD", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWZlOTJlMTFhNjdiNTY5MzU0NDZhMjE0Y2FhMzcyM2QyOWU2ZGI1NmM1NWZhOGQ0MzE3OWE4YTMxNzZjNmMxIn19fQ==", "&rКартофельный салат", "", "&7&oВосстанавливает &b&o" + "6.0 единиц" + " &7&oголода"),
		new ItemStack[] {new ItemStack(Material.BAKED_POTATO), getItem("MAYO"), getItem("ONION"), new ItemStack(Material.BOWL), null, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("CHICKEN_SANDWICH", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19", "&rКуриный сэндвич", "", "&7&oВосстанавливает &b&o" + "5.5 единиц" + " &7&oголода"),
		new ItemStack[] {new ItemStack(Material.COOKED_CHICKEN), getItem("MAYO"), new ItemStack(Material.BREAD), null, null, null, null, null, null},
		11)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("FISH_SANDWICH", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19", "&rРыбный сэндвич", "", "&7&oВосстанавливает &b&o" + "5.5 единиц" + " &7&oголода"),
		new ItemStack[] {new ItemStack(Material.COOKED_COD), getItem("MAYO"), new ItemStack(Material.BREAD), null, null, null, null, null, null},
		11)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("BAGEL", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTAyZTkyZjEzZGUzYmVlNjkyMjhjMzg0NDc4ZTc2MTIzMDY4MWU1ZmNlOWJkYTE5NWRhZWFmODQ4NDEzOTMzMSJ9fX0=", "&rРогалик", "", "&7&oВосстанавливает &b&o" + "2.0 единицы" + " &7&oголода"),
		new ItemStack[] {getItem("YEAST"), SlimefunItems.WHEAT_FLOUR, null, null, null, null, null, null, null},
		4)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("EGG_SALAD", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWZlOTJlMTFhNjdiNTY5MzU0NDZhMjE0Y2FhMzcyM2QyOWU2ZGI1NmM1NWZhOGQ0MzE3OWE4YTMxNzZjNmMxIn19fQ==", "&rЯичница", "", "&7&oВосстанавливает &b&o" + "6.0 единиц" + " &7&oголода"),
		new ItemStack[] {new ItemStack(Material.EGG), getItem("MAYO"), new ItemStack(Material.BOWL), null, null, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("TOMATO_SOUP", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzYzNjZmMTc0MjhhNDk5MDEyNjg0NGY3NGEwMmRiZjU1MjRmMzViZTEzMjNmOGZhYjBiZjYxYTU3ZmY0MWRlMyJ9fX0=", "&4Суп из помидоров", "", "&7&oВосстанавливает &b&o" + "5.5 единиц" + " &7&oголода"),
		new ItemStack[] {new ItemStack(Material.BOWL), getItem("TOMATO"), null, null, null, null, null, null, null},
		11)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("STRAWBERRY_SALAD", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWZlOTJlMTFhNjdiNTY5MzU0NDZhMjE0Y2FhMzcyM2QyOWU2ZGI1NmM1NWZhOGQ0MzE3OWE4YTMxNzZjNmMxIn19fQ==", "&cКлубничный салат", "", "&7&oВосстанавливает &b&o" + "5.0 единиц" + " &7&oголода"),
		new ItemStack[] {new ItemStack(Material.BOWL), getItem("STRAWBERRY"), getItem("LETTUCE"), getItem("TOMATO"), null, null, null, null, null},
		10)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("GRAPE_SALAD", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWZlOTJlMTFhNjdiNTY5MzU0NDZhMjE0Y2FhMzcyM2QyOWU2ZGI1NmM1NWZhOGQ0MzE3OWE4YTMxNzZjNmMxIn19fQ==", "&cВиноградный салат", "", "&7&oВосстанавливает &b&o" + "5.0 единиц" + " &7&oголода"),
		new ItemStack[] {new ItemStack(Material.BOWL), getItem("GRAPE"), getItem("LETTUCE"), getItem("TOMATO"), null, null, null, null, null},
		10)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("CHICKEN_CURRY", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDA5ZTBkZDU0ODlmMDNlZmRjODA4MzA4OGY1MjFiODI5NDZjZGVjOThmYzFjOTRjNGUwOTc5MmU0NzM1MTg0YSJ9fX0=", "&rКурица-карри", "", "&7&oВосстанавливает &b&o" + "8.0 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("CILANTRO"), new ItemStack(Material.COOKED_CHICKEN), getItem("BROWN_SUGAR"), getItem("CURRY_LEAF"), getItem("VEGETABLE_OIL"), getItem("CURRY_LEAF"), getItem("ONION"), new ItemStack(Material.BOWL), getItem("GARLIC")},
		16)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("COCONUT_CHICKEN_CURRY", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDA5ZTBkZDU0ODlmMDNlZmRjODA4MzA4OGY1MjFiODI5NDZjZGVjOThmYzFjOTRjNGUwOTc5MmU0NzM1MTg0YSJ9fX0=", "&rКокосовое куриное карри", "", "&7&oВосстанавливает &b&o" + "9.5 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("COCONUT"), getItem("COCONUT"), getItem("CHICKEN_CURRY"), null, null, null, null, null, null},
		19)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("BISCUIT", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWYwOTQ0NTZmZDc5NGI2NTMxZmM2ZGVjNmYzOTZiNjgwYjk1MzYwMDIwNjNlMTFjZTI0ZDBhNzRiMGI3ZDg4NSJ9fX0=", "&6Бисквит", "", "&7&oВосстанавливает &b&o" + "2.0 единицы" + " &7&oголода"),
		new ItemStack[] {SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, null, null, null, null, null, null, null},
		4)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("BISCUITS_GRAVY", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjhiYmI4MzVlMjJkOWVjNjJlMjI0MTFiOGUwMTUxMzhkNTU5NzI4M2FkMzZlNjE4ZmU0NGJhNWYxYTZiNjBmZCJ9fX0=", "&rБискфит в деревенском соусе", "", "&7&oВосстанавливает &b&o" + "6.5 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("COUNTRY_GRAVY"), getItem("COUNTRY_GRAVY"), getItem("COUNTRY_GRAVY"), getItem("BISCUIT"), getItem("BISCUIT"), getItem("BISCUIT"), null, new ItemStack(Material.BOWL), null},
		13)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("CHEESECAKE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0=", "&rЧизкейк", "", "&7&oВосстанавливает &b&o" + "8.0 единиц" + " &7&oголода"),
		new ItemStack[] {new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null, null, null},
		16)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("CHERRY_CHEESECAKE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0=", "&cВишнёвый чизкейк", "", "&7&oВосстанавливает &b&o" + "8.5 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("CHEESECAKE"), getItem("CHERRY"), null, null, null, null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("BLUEBERRY_CHEESECAKE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0=", "&9Черничный чизкейк", "", "&7&oВосстанавливает &b&o" + "8.5 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("CHEESECAKE"), getItem("BLUEBERRY"), null, null, null, null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("PUMPKIN_CHEESECAKE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0=", "&6Тыквенный чизкейк", "", "&7&oВосстанавливает &b&o" + "8.5 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("CHEESECAKE"), new ItemStack(Material.PUMPKIN), null, null, null, null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("SWEETENED_PEAR_CHEESECAKE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0=", "&6Сладкий грушевый чизкейк", "", "&7&oВосстанавливает &b&o" + "9.0 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("CHEESECAKE"), new ItemStack(Material.SUGAR), getItem("PEAR"), null, null, null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("BLACKBERRY_COBBLER", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzZjMzY1MjNjMmQxMWI4YzhlYTJlOTkyMjkxYzUyYTY1NDc2MGVjNzJkY2MzMmRhMmNiNjM2MTY0ODFlZSJ9fX0=", "&8Десерт из ежевики", "", "&7&oВосстанавливает &b&o" + "6.0 единиц" + " &7&oголода"),
		new ItemStack[] {new ItemStack(Material.SUGAR), getItem("BLACKBERRY"), SlimefunItems.WHEAT_FLOUR, null, null, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("PAVLOVA", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0=", "&rТорт «Павлова»", "", "&7&oВосстанавливает &b&o" + "9.0 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("LEMON"), getItem("STRAWBERRY"), new ItemStack(Material.SUGAR), new ItemStack(Material.EGG), SlimefunItems.HEAVY_CREAM, null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("CORN_ON_THE_COB", Material.GOLDEN_CARROT, "&6Кукурузные хлопья", "", "&7&oВосстанавливает &b&o" + "4.5 единицы" + " &7&oголода"),
		new ItemStack[] {SlimefunItems.BUTTER, getItem("CORN"), null, null, null, null, null, null, null},
		9)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("CREAMED_CORN", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTE3NGIzNGM1NDllZWQ4YmFmZTcyNzYxOGJhYjY4MjFhZmNiMTc4N2I1ZGVjZDFlZWNkNmMyMTNlN2U3YzZkIn19fQ==", "&rКукурузная каша", "", "&7&oВосстанавливает &b&o" + "4.0 единицы" + " &7&oголода"),
		new ItemStack[] {SlimefunItems.HEAVY_CREAM, getItem("CORN"), new ItemStack(Material.BOWL), null, null, null, null, null, null},
		8)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("BACON", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTdiYTIyZDVkZjIxZTgyMWE2ZGU0YjhjOWQzNzNhM2FhMTg3ZDhhZTc0ZjI4OGE4MmQyYjYxZjI3MmU1In19fQ==", "&rБекон", "", "&7&oВосстанавливает &b&o" + "1.5 единицы" + " &7&oголода"),
		new ItemStack[] {new ItemStack(Material.COOKED_PORKCHOP), null, null, null, null, null, null, null, null},
		3)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("SANDWICH", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19", "&rСэндвич", "", "&7&oВосстанавливает &b&o" + "9.5 единиц" + " &7&oголода"),
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("MAYO"), new ItemStack(Material.COOKED_BEEF), getItem("TOMATO"), getItem("LETTUCE"), null, null, null, null},
		19)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("BLT", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19", "&rБСП сэндвич &o(бекон, салат, помидор)", "", "&7&oВосстанавливает &b&o" + "9.0 единиц" + " &7&oголода"),
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.COOKED_PORKCHOP), getItem("TOMATO"), getItem("LETTUCE"), null, null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("LEAFY_CHICKEN_SANDWICH", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19", "&rКуриный сэндвич с салатом", "", "&7&oВосстанавливает &b&o" + "6.5 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("CHICKEN_SANDWICH"), getItem("LETTUCE"), null, null, null, null, null, null, null},
		1)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("LEAFY_FISH_SANDWICH", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19", "&rРыбный сэндвич с салатом", "", "&7&oВосстанавливает &b&o" + "6.5 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("FISH_SANDWICH"), getItem("LETTUCE"), null, null, null, null, null, null, null},
		11)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("HAMBURGER", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0=", "&rГамбургер", "", "&7&oВосстанавливает &b&o" + "5.0 единиц" + " &7&oголода"),
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.COOKED_BEEF), null, null, null, null, null, null, null},
		10)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("CHEESEBURGER", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0=", "&rЧизбургер", "", "&7&oВосстанавливает &b&o" + "6.5 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("HAMBURGER"), SlimefunItems.CHEESE, null, null, null, null, null, null, null},
		13)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("BACON_CHEESEBURGER", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0=", "&rЧизбургер с беконом", "", "&7&oВосстанавливает &b&o" + "8.5 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("CHEESEBURGER"), getItem("BACON"), null, null, null, null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("DELUXE_CHEESEBURGER", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0=", "&rЧизбургер делюкс", "", "&7&oВосстанавливает &b&o" + "8.0 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("CHEESEBURGER"), getItem("LETTUCE"), getItem("TOMATO"), null, null, null, null, null, null},
		16)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("GARLIC_BREAD", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTMzZmE3ZDNlNjNiMjgwYTVkN2UyYmIwOTMzMmRmZjg2YjE3ZGVjZDJiMDllY2NkZDYyZGE1MjY1NTk3Zjc0ZCJ9fX0=", "&rЧесночный хлеб", "", "&7&oВосстанавливает &b&o" + "5.0 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("GARLIC"), new ItemStack(Material.BREAD), null, null, null, null, null, null, null},
		10)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("GARLIC_CHEESE_BREAD", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTMzZmE3ZDNlNjNiMjgwYTVkN2UyYmIwOTMzMmRmZjg2YjE3ZGVjZDJiMDllY2NkZDYyZGE1MjY1NTk3Zjc0ZCJ9fX0=", "&rЧесночный сырный хлеб", "", "&7&oВосстанавливает &b&o" + "6.5 единиц" + " &7&oголода"),
		new ItemStack[] {SlimefunItems.CHEESE, getItem("GARLIC"), new ItemStack(Material.BREAD), null, null, null, null, null, null},
		13)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("CARROT_CAKE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjkxMzY1MTRmMzQyZTdjNTIwOGExNDIyNTA2YTg2NjE1OGVmODRkMmIyNDkyMjAxMzllOGJmNjAzMmUxOTMifX19", "&rМорковный пирог", "", "&7&oВосстанавливает &b&o" + "6.0 единиц" + " &7&oголода"),
		new ItemStack[] {new ItemStack(Material.CARROT), SlimefunItems.WHEAT_FLOUR, new ItemStack(Material.SUGAR), new ItemStack(Material.EGG), null, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("CHICKEN_BURGER", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0=", "&rКуриный бургер", "", "&7&oВосстанавливает &b&o" + "5.0 единиц" + " &7&oголода"),
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.COOKED_CHICKEN), null, null, null, null, null, null, null},
		10)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("CHICKEN_CHEESEBURGER", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0=", "&rКуриный чизбургер", "", "&7&oВосстанавливает &b&o" + "6.5 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("CHICKEN_BURGER"), SlimefunItems.CHEESE, null, null, null, null, null, null, null},
		13)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("BACON_BURGER", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0=", "&rБургер с беконом", "", "&7&oВосстанавливает &b&o" + "5.0 единиц" + " &7&oголода"),
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("BACON"), null, null, null, null, null, null, null},
		10)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("BACON_SANDWICH", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19", "&rСэндвич с беконом", "", "&7&oВосстанавливает &b&o" + "9.5 единиц" + " &7&oголода"),
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("BACON"), getItem("MAYO"), getItem("TOMATO"), getItem("LETTUCE"), null, null, null, null},
		19)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("TACO", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThjZWQ3NGEyMjAyMWE1MzVmNmJjZTIxYzhjNjMyYjI3M2RjMmQ5NTUyYjcxYTM4ZDU3MjY5YjM1MzhjZiJ9fX0=", "&rТако", "", "&7&oВосстанавливает &b&o" + "9.0 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_BEEF), getItem("LETTUCE"), getItem("TOMATO"), getItem("CHEESE"), null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("FISH_TACO", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThjZWQ3NGEyMjAyMWE1MzVmNmJjZTIxYzhjNjMyYjI3M2RjMmQ5NTUyYjcxYTM4ZDU3MjY5YjM1MzhjZiJ9fX0=", "&rРыбный тако", "", "&7&oВосстанавливает &b&o" + "9.0 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_COD), getItem("LETTUCE"), getItem("TOMATO"), getItem("CHEESE"), null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("STREET_TACO", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWFkN2MwYTA0ZjE0ODVjN2EzZWYyNjFhNDhlZTgzYjJmMWFhNzAxYWIxMWYzZmM5MTFlMDM2NmE5Yjk3ZSJ9fX0=", "&rУличный тако", "", "&7&oВосстанавливает &b&o" + "9.0 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_BEEF), getItem("CILANTRO"), getItem("ONION"), null, null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("JAMMY_DODGER", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWQwMGRmYjNhNTdjMDY4YTBjYzdiNjI0ZDhkODg1MjA3MDQzNWQyNjM0YzBlNWRhOWNiYmFiNDYxNzRhZjBkZiJ9fX0=", "&cПеченье с начинкой", "", "&7&oВосстанавливает &b&o" + "5.0 единиц" + " &7&oголода"),
		new ItemStack[] {null, getItem("BISCUIT"), null, null, getItem("RASPBERRY_JUICE"), null, null, getItem("BISCUIT"), null},
		8)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("PANCAKES", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ3ZjRmNWE3NGM2NjkxMjgwY2Q4MGU3MTQ4YjQ5YjJjZTE3ZGNmNjRmZDU1MzY4NjI3ZjVkOTJhOTc2YTZhOCJ9fX0=", "&rБлины", "", "&7&oВосстанавливает &b&o" + "6.0 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("WHEAT_FLOUR"), new ItemStack(Material.SUGAR), getItem("BUTTER"), new ItemStack(Material.EGG), new ItemStack(Material.EGG), null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("BLUEBERRY_PANCAKES", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ3ZjRmNWE3NGM2NjkxMjgwY2Q4MGU3MTQ4YjQ5YjJjZTE3ZGNmNjRmZDU1MzY4NjI3ZjVkOTJhOTc2YTZhOCJ9fX0=", "&rЧерничные блины", "", "&7&oВосстанавливает &b&o" + "6.5 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("PANCAKES"), getItem("BLUEBERRY"), null, null, null, null, null, null, null},
		13)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("SWEET_BERRY_PANCAKES", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTQ0Y2E5OWUzMDhhMTg2YjMwMjgxYjIwMTdjNDQxODlhY2FmYjU5MTE1MmY4MWZlZWE5NmZlY2JlNTcifX19", "&rБлины из сладких ягод", "", "&7&oВосстанавливает &b&o" + "6.5 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("PANCAKES"), new ItemStack(Material.SWEET_BERRIES), null, null, null, null, null, null, null},
		13)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("FRIES", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTYzYjhhZWFmMWRmMTE0ODhlZmM5YmQzMDNjMjMzYTg3Y2NiYTNiMzNmN2ZiYTljMmZlY2FlZTk1NjdmMDUzIn19fQ==", "&rКартофель фри", "", "&7&oВосстанавливает &b&o" + "6.0 единиц" + " &7&oголода"),
		new ItemStack[] {new ItemStack(Material.POTATO), getItem("SALT"), null, null, null, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("POPCORN", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ5N2IxNDdjZmFlNTIyMDU1OTdmNzJlM2M0ZWY1MjUxMmU5Njc3MDIwZTRiNGZhNzUxMmMzYzZhY2RkOGMxIn19fQ==", "&rПопкорн", "", "&7&oВосстанавливает &b&o" + "4.0 единицы" + " &7&oголода"),
		new ItemStack[] {getItem("CORN"), getItem("BUTTER"), null, null, null, null, null, null, null},
		8)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("SWEET_POPCORN", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ5N2IxNDdjZmFlNTIyMDU1OTdmNzJlM2M0ZWY1MjUxMmU5Njc3MDIwZTRiNGZhNzUxMmMzYzZhY2RkOGMxIn19fQ==", "&rПопкорн &7(сладкий)", "", "&7&oВосстанавливает &b&o" + "6.0 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("CORN"), getItem("BUTTER"), new ItemStack(Material.SUGAR), null, null, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("SALTY_POPCORN", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ5N2IxNDdjZmFlNTIyMDU1OTdmNzJlM2M0ZWY1MjUxMmU5Njc3MDIwZTRiNGZhNzUxMmMzYzZhY2RkOGMxIn19fQ==", "&rПопкорн &7(солёный)", "", "&7&oВосстанавливает &b&o" + "6.0 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("CORN"), getItem("BUTTER"), getItem("SALT"), null, null, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("SHEPARDS_PIE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ==", "&rПастуший пирог", "", "&7&oВосстанавливает &b&o" + "8.0 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("CABBAGE"), new ItemStack(Material.CARROT), SlimefunItems.WHEAT_FLOUR, new ItemStack(Material.COOKED_BEEF), getItem("TOMATO"), null, null, null, null},
		16)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("CHICKEN_POT_PIE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ==", "&rКуриный пирог", "", "&7&oВосстанавливает &b&o" + "8.5 единиц" + " &7&oголода"),
		new ItemStack[] {new ItemStack(Material.COOKED_CHICKEN), new ItemStack(Material.CARROT), SlimefunItems.WHEAT_FLOUR, new ItemStack(Material.POTATO), null, null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("CHOCOLATE_CAKE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTExOWZjYTRmMjhhNzU1ZDM3ZmJlNWRjZjZkOGMzZWY1MGZlMzk0YzFhNzg1MGJjN2UyYjcxZWU3ODMwM2M0YyJ9fX0=", "&rШоколадный торт", "", "&7&oВосстанавливает &b&o" + "8.5 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, new ItemStack(Material.EGG), null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("CREAM_COOKIE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGZkNzFlMjBmYzUwYWJmMGRlMmVmN2RlY2ZjMDFjZTI3YWQ1MTk1NTc1OWUwNzJjZWFhYjk2MzU1ZjU5NGYwIn19fQ==", "&rКремовое печенье", "", "&7&oВосстанавливает &b&o" + "6.0 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("BLUEBERRY_MUFFIN", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM3OTRjNzM2ZmM3NmU0NTcwNjgzMDMyNWI5NTk2OTQ2NmQ4NmY4ZDdiMjhmY2U4ZWRiMmM3NWUyYWIyNWMifX19", "&rЧерничный маффин", "", "&7&oВосстанавливает &b&o" + "6.5 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("BLUEBERRY"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null},
		13)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("PUMPKIN_MUFFIN", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM3OTRjNzM2ZmM3NmU0NTcwNjgzMDMyNWI5NTk2OTQ2NmQ4NmY4ZDdiMjhmY2U4ZWRiMmM3NWUyYWIyNWMifX19", "&rТыквенный маффин", "", "&7&oВосстанавливает &b&o" + "6.5 единиц" + " &7&oголода"),
		new ItemStack[] {new ItemStack(Material.PUMPKIN), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null},
		13)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("CHOCOLATE_CHIP_MUFFIN", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM3OTRjNzM2ZmM3NmU0NTcwNjgzMDMyNWI5NTk2OTQ2NmQ4NmY4ZDdiMjhmY2U4ZWRiMmM3NWUyYWIyNWMifX19", "&rШоколадный маффин", "", "&7&oВосстанавливает &b&o" + "6.5 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null},
		13)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("BOSTON_CREAM_PIE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGZkNzFlMjBmYzUwYWJmMGRlMmVmN2RlY2ZjMDFjZTI3YWQ1MTk1NTc1OWUwNzJjZWFhYjk2MzU1ZjU5NGYwIn19fQ==", "&rБостонский кремовый пирог", "", "&7&oВосстанавливает &b&o" + "4.5 единицы" + " &7&oголода"),
		new ItemStack[] {null, getItem("CHOCOLATE_BAR"), null, null, SlimefunItems.HEAVY_CREAM, null, null, getItem("BISCUIT"), null},
		9)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("HOT_DOG", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNmMmQ3ZDdhOGIxYjk2OTE0Mjg4MWViNWE4N2U3MzdiNWY3NWZiODA4YjlhMTU3YWRkZGIyYzZhZWMzODIifX19", "&rХот-дог", "", "&7&oВосстанавливает &b&o" + "5.0 единиц" + " &7&oголода"),
		new ItemStack[] {null, null, null, null, new ItemStack(Material.COOKED_PORKCHOP), null, null, new ItemStack(Material.BREAD), null},
		10)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("BACON_WRAPPED_CHEESE_FILLED_HOT_DOG", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNmMmQ3ZDdhOGIxYjk2OTE0Mjg4MWViNWE4N2U3MzdiNWY3NWZiODA4YjlhMTU3YWRkZGIyYzZhZWMzODIifX19", "&rБекон с сыром, завёрнутые в хот-дог", "&7&o\"Когда я шеф-повар\" - @Eyamaz", "", "&7&oВосстанавливает &b&o" + "8.5 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("BACON"), getItem("HOT_DOG"), getItem("BACON"), null, getItem("CHEESE"), null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("BBQ_BACON_WRAPPED_HOT_DOG", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNmMmQ3ZDdhOGIxYjk2OTE0Mjg4MWViNWE4N2U3MzdiNWY3NWZiODA4YjlhMTU3YWRkZGIyYzZhZWMzODIifX19", "&rБарбекю, завёрнутый в хот-дог", "&7&o\"Хочешь поговорить о горячих собаках?\" - @Pahimar", "", "&7&oВосстанавливает &b&o" + "8.5 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("BACON"), getItem("HOT_DOG"), getItem("BACON"), null, getItem("BBQ_SAUCE"), null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("BBQ_DOUBLE_BACON_WRAPPED_HOT_DOG_IN_A_TORTILLA_WITH_CHEESE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNmMmQ3ZDdhOGIxYjk2OTE0Mjg4MWViNWE4N2U3MzdiNWY3NWZiODA4YjlhMTU3YWRkZGIyYzZhZWMzODIifX19", "&rБарбекю с двойным беконом, завёрнутый в хот-дог с омлетом и сыром", "&7&o\"Когда я шеф-повар\" - @Eyamaz", "", "&7&oВосстанавливает &b&o" + "10.0 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("BACON"), getItem("BBQ_SAUCE"), getItem("BACON"), getItem("BACON"), new ItemStack(Material.COOKED_PORKCHOP), getItem("BACON"), getItem("CORNMEAL"), getItem("CHEESE"), getItem("CORNMEAL")},
		20)
		.register();

		new CustomFood(category_drinks, new SlimefunItemStack("SWEETENED_TEA", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDhlOTRkZGQ3NjlhNWJlYTc0ODM3NmI0ZWM3MzgzZmQzNmQyNjc4OTRkN2MzYmVlMDExZThlNGY1ZmNkNyJ9fX0=", "&aСладкий чай", "", "&7&oВосстанавливает &b&o" + "3.0 единицы" + " &7&oголода"),
		new ItemStack[] {getItem("TEA_LEAF"), new ItemStack(Material.SUGAR), null, null, null, null, null, null, null},
		6)
		.register();

		new CustomFood(category_drinks, new SlimefunItemStack("HOT_CHOCOLATE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDExNTExYmRkNTViY2I4MjgwM2M4MDM5ZjFjMTU1ZmQ0MzA2MjYzNmUyM2Q0ZDQ2YzRkNzYxYzA0ZDIyYzIifX19", "&6Горячий шоколад", "", "&7&oВосстанавливает &b&o" + "4.0 единицы" + " &7&oголода"),
		new ItemStack[] {getItem("CHOCOLATE_BAR"), SlimefunItems.HEAVY_CREAM, null, null, null, null, null, null, null},
		8)
		.register();

		new CustomFood(category_drinks, new SlimefunItemStack("PINACOLADA", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmE4ZjFmNzBlODU4MjU2MDdkMjhlZGNlMWEyYWQ0NTA2ZTczMmI0YTUzNDVhNWVhNmU4MDdjNGIzMTNlODgifX19", "&6Пинаколада", "", "&7&oВосстанавливает &b&o" + "7.0 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("PINEAPPLE"), getItem("ICE_CUBE"), getItem("COCONUT_MILK"), null, null, null, null, null, null},
		14)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("CHOCOLATE_STRAWBERRY", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ0ZWQ3YzczYWMyODUzZGZjYWE5Y2E3ODlmYjE4ZGExZDQ3YjE3YWQ2OGIyZGE3NDhkYmQxMWRlMWE0OWVmIn19fQ==", "&cКлубника в шоколаде", "", "&7&oВосстанавливает &b&o" + "2.5 единицы" + " &7&oголода"),
		new ItemStack[] {getItem("CHOCOLATE_BAR"), getItem("STRAWBERRY"), null, null, null, null, null, null, null},
		5)
		.register();

		new Juice(category_drinks, new SlimefunItemStack("LEMONADE", new CustomPotion("&eЛимонад", Color.YELLOW, new PotionEffect(PotionEffectType.SATURATION, 8, 0), "", "&7&oВосстанавливает &b&o" + "4.0 единицы" + " &7&oголода")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("LEMON_JUICE"), new ItemStack(Material.SUGAR), null, null, null, null, null, null, null})
		.register();

		new CustomFood(category_food, new SlimefunItemStack("SWEET_POTATO_PIE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ==", "&rСладкий картофельный пирог", "", "&7&oВосстанавливает &b&o" + "6.5 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("SWEET_POTATO"), new ItemStack(Material.EGG), SlimefunItems.HEAVY_CREAM, SlimefunItems.WHEAT_FLOUR, null, null, null, null, null},
		13);

		new CustomFood(category_food, new SlimefunItemStack("LAMINGTON", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTExOWZjYTRmMjhhNzU1ZDM3ZmJlNWRjZjZkOGMzZWY1MGZlMzk0YzFhNzg1MGJjN2UyYjcxZWU3ODMwM2M0YyJ9fX0=", "&rЛамингтон", "", "&7&oВосстанавливает &b&o" + "9.0 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, getItem("COCONUT"), null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("WAFFLES", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ3ZjRmNWE3NGM2NjkxMjgwY2Q4MGU3MTQ4YjQ5YjJjZTE3ZGNmNjRmZDU1MzY4NjI3ZjVkOTJhOTc2YTZhOCJ9fX0=", "&rВафли", "", "&7&oВосстанавливает &b&o" + "6.0 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("WHEAT_FLOUR"), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), getItem("BUTTER"), null, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("CLUB_SANDWICH", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19", "&rКлубный сэндвич", "", "&7&oВосстанавливает &b&o" + "9.5 единиц" + " &7&oголода"),
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("MAYO"), getItem("BACON"), getItem("TOMATO"), getItem("LETTUCE"), getItem("MUSTARD"), null, null, null},
		19)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("BURRITO", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTM4N2E2MjFlMjY2MTg2ZTYwNjgzMzkyZWIyNzRlYmIyMjViMDQ4NjhhYjk1OTE3N2Q5ZGMxODFkOGYyODYifX19", "&rБуррито", "", "&7&oВосстанавливает &b&o" + "9.0 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_BEEF), getItem("LETTUCE"), getItem("TOMATO"), getItem("HEAVY_CREAM"), getItem("CHEESE"), null, null, null},
		18)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("CHICKEN_BURRITO", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTM4N2E2MjFlMjY2MTg2ZTYwNjgzMzkyZWIyNzRlYmIyMjViMDQ4NjhhYjk1OTE3N2Q5ZGMxODFkOGYyODYifX19", "&rКуриный буррито", "", "&7&oВосстанавливает &b&o" + "9.0 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_CHICKEN), getItem("LETTUCE"), getItem("TOMATO"), getItem("HEAVY_CREAM"), getItem("CHEESE"), null, null, null},
		18)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("GRILLED_SANDWICH", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFlZTg0ZDE5Yzg1YWZmNzk2Yzg4YWJkYTIxZWM0YzkyYzY1NWUyZDY3YjcyZTVlNzdiNWFhNWU5OWVkIn19fQ==", "&rЖареный сэндвич", "", "&7&oВосстанавливает &b&o" + "5.5 единиц" + " &7&oголода"),
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.COOKED_PORKCHOP), getItem("CHEESE"), null, null, null, null, null, null},
		11)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("LASAGNA", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMDNhMzU3NGE4NDhmMzZhZTM3MTIxZTkwNThhYTYxYzEyYTI2MWVlNWEzNzE2ZjZkODI2OWUxMWUxOWUzNyJ9fX0=", "&rЛазанья", "", "&7&oВосстанавливает &b&o" + "8.5 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("TOMATO"), getItem("CHEESE"), SlimefunItems.WHEAT_FLOUR, getItem("TOMATO"), getItem("CHEESE"), new ItemStack(Material.COOKED_BEEF), null, null, null},
		17)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("ICE_CREAM", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTUzNjZjYTE3OTc0ODkyZTRmZDRjN2I5YjE4ZmViMTFmMDViYTJlYzQ3YWE1MDM1YzgxYTk1MzNiMjgifX19", "&rМороженое", "", "&7&oВосстанавливает &b&o" + "8.0 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("HEAVY_CREAM"), getItem("ICE_CUBE"), new ItemStack(Material.SUGAR), new ItemStack(Material.COCOA_BEANS), getItem("STRAWBERRY"), null, null, null, null},
		16)
		.register();

		new Juice(category_drinks, new SlimefunItemStack("PINEAPPLE_JUICE", new CustomPotion("&6Ананасовый сок", Color.ORANGE, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&oВосстанавливает &b&o" + "3.0 единицы" + " &7&oголода")), RecipeType.JUICER,
		new ItemStack[] {getItem("PINEAPPLE"), null, null, null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new SlimefunItemStack("PINEAPPLE_SMOOTHIE", new CustomPotion("&6Ананасовый смузи", Color.ORANGE, new PotionEffect(PotionEffectType.SATURATION, 10, 0), "", "&7&oВосстанавливает &b&o" + "5.0 единиц" + " &7&oголода")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("PINEAPPLE_JUICE"), getItem("ICE_CUBE"), null, null, null, null, null, null, null})
		.register();

		new CustomFood(category_food, new SlimefunItemStack("TIRAMISU", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY5MDkxZDI4ODAyMmM3YjBlYjZkM2UzZjQ0YjBmZWE3ZjJjMDY5ZjQ5NzQ5MWExZGNhYjU4N2ViMWQ1NmQ0In19fQ==", "&rТирамису", "", "&7&oВосстанавливает &b&o" + "8.0 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("HEAVY_CREAM"), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), new ItemStack(Material.COCOA_BEANS), new ItemStack(Material.EGG), null, null, null, null},
		16)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("TIRAMISU_WITH_STRAWBERRIES", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY5MDkxZDI4ODAyMmM3YjBlYjZkM2UzZjQ0YjBmZWE3ZjJjMDY5ZjQ5NzQ5MWExZGNhYjU4N2ViMWQ1NmQ0In19fQ==", "&rТирамису с клубникой", "", "&7&oВосстанавливает &b&o" + "9.0 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("TIRAMISU"), getItem("STRAWBERRY"), null, null, null, null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("TIRAMISU_WITH_RASPBERRIES", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY5MDkxZDI4ODAyMmM3YjBlYjZkM2UzZjQ0YjBmZWE3ZjJjMDY5ZjQ5NzQ5MWExZGNhYjU4N2ViMWQ1NmQ0In19fQ==", "&rТирамису с малиной", "", "&7&oВосстанавливает &b&o" + "9.0 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("TIRAMISU"), getItem("RASPBERRY"), null, null, null, null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("TIRAMISU_WITH_BLACKBERRIES", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY5MDkxZDI4ODAyMmM3YjBlYjZkM2UzZjQ0YjBmZWE3ZjJjMDY5ZjQ5NzQ5MWExZGNhYjU4N2ViMWQ1NmQ0In19fQ==", "&rТирамису с ежевикой", "", "&7&oВосстанавливает &b&o" + "9.0 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("TIRAMISU"), getItem("BLACKBERRY"), null, null, null, null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("CHOCOLATE_PEAR_CAKE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTExOWZjYTRmMjhhNzU1ZDM3ZmJlNWRjZjZkOGMzZWY1MGZlMzk0YzFhNzg1MGJjN2UyYjcxZWU3ODMwM2M0YyJ9fX0=", "&rШоколадно-грушевый торт", "", "&7&oВосстанавливает &b&o" + "9.5 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, getItem("PEAR"), new ItemStack(Material.EGG), null, null, null},
		19)
		.register();

		new CustomFood(category_food, new SlimefunItemStack("APPLE_PEAR_CAKE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ==", "&cЯблочно-грушевый торт", "", "&7&oВосстанавливает &b&o" + "9.0 единиц" + " &7&oголода"),
		new ItemStack[] {getItem("APPLE"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, getItem("PEAR"), new ItemStack(Material.EGG), null, null, null},
		18)
		.register();
	}

	@Override
	public void onDisable() {
		instance = null;
	}

	public void registerTree(String id, String name, String texture, String fruitName, String color, Color pcolor, String juiceName, String pieName, Material... soil) {
		Tree tree = new Tree(fruitName, id, texture, soil);
		trees.add(tree);

		SlimefunItemStack sfi = new SlimefunItemStack(id + "_SAPLING", Material.OAK_SAPLING, color + name + " деревце");

		items.put(id + "_SAPLING", sfi);

		new SlimefunItem(category_main, sfi, new RecipeType(new CustomItem(Material.GRASS, "&7Выпадает при разрушении травы")),
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register();

		try {
			new EGPlant(category_main, new SlimefunItemStack(id, texture, color + fruitName), new RecipeType(new CustomItem(Material.OAK_LEAVES, "&7Выпадает с листвы соответствующего дерева")), true,
			new ItemStack[] {null, null, null, null, getItem(id + "_SAPLING"), null, null, null, null})
			.register();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (pcolor != null) {
			new Juice(category_drinks, new SlimefunItemStack(id + "_JUICE", new CustomPotion(color + juiceName, pcolor, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&oВосстанавливает &b&o" + "3.0 единицы" + " &7&oголода")), RecipeType.JUICER,
			new ItemStack[] {getItem(id), null, null, null, null, null, null, null, null})
			.register();
		}

		if (pieName != null) {
			try {
				new CustomFood(category_food, new SlimefunItemStack(id + "_PIE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ==", color + pieName, "", "&7&oВосстанавливает &b&o" + "6.5 единиц" + " &7&oголода"),
				new ItemStack[] {getItem(id), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), new ItemStack(Material.MILK_BUCKET), SlimefunItems.WHEAT_FLOUR, null, null, null, null},
				13)
				.register();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (!new File("plugins/ExoticGarden/schematics", id + "_TREE.schematic").exists())
			saveResource("schematics/" + id + "_TREE.schematic", false);
	}

	public void registerBerry(String id, String name, String bushName, String juiceName, String smoothieName, String sandwichName, String pieName, String color, Color pcolor, PlantType type, PlantData data) {
		Berry berry = new Berry(id, type, data);
		berries.add(berry);

		SlimefunItemStack sfi = new SlimefunItemStack(id + "_BUSH", Material.OAK_SAPLING, color + bushName);

		items.put(id + "_BUSH", sfi);

		new SlimefunItem(category_main, sfi, new RecipeType(new CustomItem(Material.GRASS, "&7Выпадает при разрушении травы")),
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register();

		new EGPlant(category_main, new SlimefunItemStack(id, data.getTexture(), color + name), new RecipeType(new CustomItem(Material.OAK_LEAVES, "&7Добывается при сборе соответствующего куста")), true,
		new ItemStack[] {null, null, null, null, getItem(id + "_BUSH"), null, null, null, null})
		.register();

		new Juice(category_drinks, new SlimefunItemStack(id + "_JUICE", new CustomPotion(color + juiceName, pcolor, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&oВосстанавливает &b&o" + "3.0 единицы" + " &7&oголода")), RecipeType.JUICER,
		new ItemStack[] {getItem(id), null, null, null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new SlimefunItemStack(id + "_SMOOTHIE", new CustomPotion(color + smoothieName, pcolor, new PotionEffect(PotionEffectType.SATURATION, 10, 0), "", "&7&oВосстанавливает &b&o" + "5.0 единиц" + " &7&oголода")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem(id + "_JUICE"), getItem("ICE_CUBE"), null, null, null, null, null, null, null})
		.register();

		try {
			new CustomFood(category_food, new SlimefunItemStack(id + "_JELLY_SANDWICH", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGM4YTkzOTA5M2FiMWNkZTY2NzdmYWY3NDgxZjMxMWU1ZjE3ZjYzZDU4ODI1ZjBlMGMxNzQ2MzFmYjA0MzkifX19", color + sandwichName, "", "&7&oВосстанавливает &b&o" + "8.0 единиц" + " &7&oголода"),
			new ItemStack[] {null, new ItemStack(Material.BREAD), null, null, getItem(id + "_JUICE"), null, null, new ItemStack(Material.BREAD), null},
			16)
			.register();

			new CustomFood(category_food, new SlimefunItemStack(id + "_PIE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ==", color + pieName, "", "&7&oВосстанавливает &b&o" + "6.5 единиц" + " &7&oголода"),
			new ItemStack[] {getItem(id), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), new ItemStack(Material.MILK_BUCKET), SlimefunItems.WHEAT_FLOUR, null, null, null, null},
			13)
			.register();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static ItemStack getItem(String id) {
		SlimefunItem item = SlimefunItem.getByID(id);
		return item != null ? item.getItem() : null;
	}

	public void registerPlant(String id, String name, String plantName, String color, PlantType type, PlantData data) {
		Berry berry = new Berry(id, type, data);
		berries.add(berry);

		SlimefunItemStack sfi = new SlimefunItemStack(id + "_BUSH", Material.OAK_SAPLING, color + name);

		items.put(id + "_BUSH", sfi);

		new SlimefunItem(category_main, sfi, new RecipeType(new CustomItem(Material.GRASS, "&7Выпадает при разрушении травы")),
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register();

		new EGPlant(category_main, new SlimefunItemStack(id, data.getTexture(), color + plantName), new RecipeType(new CustomItem(Material.OAK_LEAVES, "&7Добывается при сборе соответствующего куста")), true,
		new ItemStack[] {null, null, null, null, getItem(id + "_BUSH"), null, null, null, null})
		.register();
	}

	public void registerMagicalPlant(String id, String name, String plantName, ItemStack item, String skull, ItemStack[] recipe) {
		SlimefunItemStack essence = new SlimefunItemStack(id + "_ESSENCE", Material.BLAZE_POWDER, "&rМагическая эссенция", "", "&7Элемент: &o" + name);

		Berry berry = new Berry(essence, id + "_ESSENCE", PlantType.ORE_PLANT, new PlantData(skull));
		berries.add(berry);

		new SlimefunItem(category_magic, new SlimefunItemStack(id + "_PLANT", Material.OAK_SAPLING, "&r" + plantName), RecipeType.ENHANCED_CRAFTING_TABLE,
		recipe)
		.register();

		HandledBlock plant = new HandledBlock(category_magic, essence, RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {essence, essence, essence, essence, null, essence, essence, essence, essence});

		plant.setRecipeOutput(item.clone());
		plant.register();
	}

	public static Berry getBerry(Block block) {
		SlimefunItem item = BlockStorage.check(block);
		if (item instanceof HandledBlock) {
			for (Berry berry : instance.berries) {
				if (item.getID().equalsIgnoreCase(berry.getID())) return berry;
			}
		}
		return null;
	}

	public static ItemStack harvestPlant(Block block) {
		ItemStack itemstack = null;
		SlimefunItem item = BlockStorage.check(block);
		if (item != null) {
			for (Berry berry : instance.berries) {
				if (item.getID().equalsIgnoreCase(berry.getID())) {
					switch (berry.getType()) {
						case ORE_PLANT:
						case DOUBLE_PLANT: {
							Block plant;
							if (BlockStorage.check(block.getRelative(BlockFace.DOWN)) == null) {
								plant = block;
								BlockStorage.clearBlockInfo(block.getRelative(BlockFace.UP));
								block.getWorld().playEffect(block.getRelative(BlockFace.UP).getLocation(), Effect.STEP_SOUND, Material.OAK_LEAVES);
								block.getRelative(BlockFace.UP).setType(Material.AIR);
							}
							else {
								plant = block.getRelative(BlockFace.DOWN);
								BlockStorage.clearBlockInfo(block);
								block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, Material.OAK_LEAVES);
								block.setType(Material.AIR);
							}
							plant.setType(Material.OAK_SAPLING);
							itemstack = berry.getItem();
							BlockStorage._integrated_removeBlockInfo(plant.getLocation(), false);
							BlockStorage.store(plant, getItem(berry.toBush()));
							break;
						}
						default: {
							block.setType(Material.OAK_SAPLING);
							itemstack = berry.getItem();
							BlockStorage._integrated_removeBlockInfo(block.getLocation(), false);
							BlockStorage.store(block, getItem(berry.toBush()));
							break;
						}
					}
				}
			}
		}
		return itemstack;
	}

	public static Kitchen getKitchen() {
		return instance.kitchen;
	}

	public static List<Tree> getTrees() {
		return instance.trees;
	}

	public static List<Berry> getBerries() {
		return instance.berries;
	}

	public static Map<String, ItemStack> getItems() {
		return instance.items;
	}

}
