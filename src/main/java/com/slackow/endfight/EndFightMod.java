package com.slackow.endfight;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.io.FileDeleteStrategy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class EndFightMod implements ModInitializer {

	public static long time;

	@Override
	public void onInitialize() {

	}

	public static int itemToInt(ItemStack item) {
		if (item == null) return 0;
		return item.count << 24 | item.getMeta() << 12 | Item.getRawId(item.getItem());
	}

	public static ItemStack intToItem(int num) {
		if (num == 0) return null;
		return new ItemStack(Item.byRawId(num & 0xFFF), num >>> 24, num >>> 12 & 0xFFF);
	}

	public static void giveInventory(PlayerEntity player) throws CommandException {
		Path path = getInventoryPath();
		if(Files.notExists(path)){
			try {
				Files.write(path,
						Collections.singleton("[16777483,16777473,16777571,16777477,1073741842,0,16777542,16777491," +
								"83886446,16777274,16777571,16777571,16777571,16777571,16777571,16777489,402653446," +
								"0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]"));
			} catch (IOException e) {
				throw new CommandException("file.read.error");
			}
		}
		try {
			String content = Files.lines(path).collect(Collectors.joining());
			ItemStack[] full = StreamSupport.stream(new JsonParser().parse(content).getAsJsonArray().spliterator(), false)
					.mapToInt(JsonElement::getAsInt)
					.mapToObj(EndFightMod::intToItem).toArray(ItemStack[]::new);
			if (full.length < 40) {
				throw new CommandException("commands.generic.exception");
			}
			System.arraycopy(full, 0, player.inventory.main, 0, 36);
			System.arraycopy(full, 36, player.inventory.armor, 0, 4);
		} catch (IOException e) {
			throw new CommandException("commands.generic.exception");
		}
	}

	public static Path getInventoryPath() {
		return MinecraftClient.getInstance().runDirectory.toPath().resolve("inv.txt");
	}
}
