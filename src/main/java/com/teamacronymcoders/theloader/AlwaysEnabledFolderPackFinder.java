package com.teamacronymcoders.theloader;

import net.minecraft.resources.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.function.Supplier;

public class AlwaysEnabledFolderPackFinder implements IPackFinder {
    private static final FileFilter FILE_FILTER = (p_195731_0_) -> {
        boolean lvt_1_1_ = p_195731_0_.isFile() && p_195731_0_.getName().endsWith(".zip");
        boolean lvt_2_1_ = p_195731_0_.isDirectory() && (new File(p_195731_0_, "pack.mcmeta")).isFile();
        return lvt_1_1_ || lvt_2_1_;
    };
    private final File folder;

    public AlwaysEnabledFolderPackFinder(File folder) {
        this.folder = folder;
    }

    @Override
    @ParametersAreNonnullByDefault
    public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> packInfo, ResourcePackInfo.IFactory<T> factory) {
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
                String packName = "the_loader/" + packFile.getName();
                T resourcePackInfo = ResourcePackInfo.createResourcePack(packName, true, this.makePackSupplier(packFile), factory, ResourcePackInfo.Priority.TOP);
                if (resourcePackInfo != null) {
                    packInfo.put(packName, resourcePackInfo);
                }
            }

        }
    }

    private Supplier<IResourcePack> makePackSupplier(File file) {
        return file.isDirectory() ? () -> new FolderPack(file) : () -> new FilePack(file);
    }
}
