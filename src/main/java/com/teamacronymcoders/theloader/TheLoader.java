package com.teamacronymcoders.theloader;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.FolderPackFinder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Mod(value = TheLoader.ID)
public class TheLoader {
    public static final String ID = "theloader";
    public static final Logger LOGGER = LogManager.getLogger(ID);

    private final File theLoaderDirectory;
    private final File dataDirectory;
    private final File resourceDirectory;

    public TheLoader() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.addListener(this::setupDataPackFinder);

        this.theLoaderDirectory = new File(".", "the_loader");
        this.dataDirectory = new File(theLoaderDirectory, "datapacks");
        this.resourceDirectory = new File(theLoaderDirectory, "resourcepacks");
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        try {
            Files.createDirectories(theLoaderDirectory.toPath());
            Files.createDirectories(dataDirectory.toPath());
            Files.createDirectories(resourceDirectory.toPath());
        } catch (IOException e) {
            LOGGER.error("Tried to create Folders", e);
        }
    }

    private void setupDataPackFinder(FMLServerAboutToStartEvent event) {
        event.getServer().getResourcePacks().addPackFinder(new FolderPackFinder(dataDirectory));
    }

    private void clientSetup(FMLClientSetupEvent event) {
        Minecraft.getInstance().getResourcePackList().addPackFinder(new FolderPackFinder(resourceDirectory));
    }
}
