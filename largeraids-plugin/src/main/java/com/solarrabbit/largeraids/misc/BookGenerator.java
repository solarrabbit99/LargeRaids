package com.solarrabbit.largeraids.misc;

import java.io.InputStream;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.solarrabbit.largeraids.util.ConfigUtil;

public class BookGenerator {
    private final YamlConfiguration config;
    private final String title;
    private final String author;
    private final List<String> pages;

    public BookGenerator(InputStream resource) {
        config = ConfigUtil.getYamlConfig(resource);
        title = ChatColor.GOLD + config.getString("title");
        author = config.getString("author");
        pages = config.getStringList("pages");
    }

    public ItemStack getBook() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setTitle(title);
        meta.setAuthor(author);
        meta.setPages(pages);
        book.setItemMeta(meta);
        return book;
    }
}
