package com.teamacronymcoders.theloader;

import net.minecraft.resources.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AlwaysEnabledFolderPackFinder implements IPackFinder {
    private static final IPackNameDecorator THE_LOADER = IPackNameDecorator.create("pack.theloader");

    private static final FileFilter FILE_FILTER = (file) -> {
        boolean isZip = file.isFile() && file.getName().endsWith(".zip");
        boolean isFolderWithMeta = file.isDirectory() && (new File(file, "pack.mcmeta")).isFile();
        return isZip || isFolderWithMeta;
    };
    private final File folder;

    public AlwaysEnabledFolderPackFinder(File folder) {
        this.folder = folder;
    }

    @Override
    @ParametersAreNonnullByDefault
    public <T extends ResourcePackInfo> void func_230230_a_(Consumer<T> packConsumer, ResourcePackInfo.IFactory<T> factory) {
        if (!folder.isDirectory()) {
            try {
                Files.createDirectories(folder.toPath());
            } catch (IOException e) {
                TheLoader.LOGGER.error("Failed to Setup Directory for Pack", e);
            }
        }

        File[] packFiles = this.folder.listFiles(FILE_FILTER);
        if (packFiles != null) {
            for (File packFile : packFiles) {
                T resourcePackInfo = ResourcePackInfo.createResourcePack(packFile.getName(), true,
                        this.makePackSupplier(packFile), factory, ResourcePackInfo.Priority.TOP,
                        IPackNameDecorator.field_232625_a_);
                if (resourcePackInfo != null) {
                    packConsumer.accept(resourcePackInfo);
                }
            }

        }
    }

    private Supplier<IResourcePack> makePackSupplier(File file) {
        return file.isDirectory() ? () -> new FolderPack(file) : () -> new FilePack(file);
    }
}
