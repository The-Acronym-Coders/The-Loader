package com.teamacronymcoders.theloader;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Mod(value = TheLoader.ID)
public class TheLoader {
    public static final String ID = "theloader";
    public static final Logger LOGGER = LogManager.getLogger(ID);

    private final File dataDirectory;
    private final File resourceDirectory;

    public TheLoader() {
        MinecraftForge.EVENT_BUS.addListener(this::setupDataPackFinder);

        File theLoaderDirectory = new File(".", "the_loader");
        this.dataDirectory = new File(theLoaderDirectory, "datapacks");
        this.resourceDirectory = new File(theLoaderDirectory, "resourcepacks");

        try {
            Files.createDirectories(theLoaderDirectory.toPath());
            Files.createDirectories(dataDirectory.toPath());
            Files.createDirectories(resourceDirectory.toPath());
        } catch (IOException e) {
            LOGGER.error("Tried to create Folders", e);
        }

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            //During RunData it is null
            //noinspection ConstantConditions
            if (Minecraft.getInstance() != null) {
                Minecraft.getInstance().getResourcePackList().addPackFinder(new AlwaysEnabledFolderPackFinder(resourceDirectory));
            }
        });
    }

    private void setupDataPackFinder(FMLServerAboutToStartEvent event) {
        event.getServer().getResourcePacks().addPackFinder(new AlwaysEnabledFolderPackFinder(dataDirectory));
    }
}
