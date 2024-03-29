/*
 * Hytilities - Hypixel focused Quality of Life mod.
 * Copyright (C) 2021  Sk1er LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package club.sk1er.hytilities.forge;

import club.sk1er.hytilities.asm.EntityPlayerSPTransformer;
import club.sk1er.hytilities.asm.GuiIngameForgeTransformer;
import club.sk1er.hytilities.asm.GuiPlayerTabOverlayTransformer;
import club.sk1er.hytilities.asm.LayerArmorBaseTransformer;
import club.sk1er.hytilities.asm.MinecraftTransformer;
import club.sk1er.hytilities.asm.GuiChatTransformer;
import club.sk1er.hytilities.forge.transformer.HytilitiesTransformer;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

public class ClassTransformer implements IClassTransformer {

    private final Multimap<String, HytilitiesTransformer> transformerMap = ArrayListMultimap.create();
    private final boolean outputBytecode = Boolean.parseBoolean(System.getProperty("debugBytecode", "false"));

    public ClassTransformer() {
        registerTransformer(new MinecraftTransformer());
        registerTransformer(new GuiPlayerTabOverlayTransformer());
        registerTransformer(new LayerArmorBaseTransformer());
        registerTransformer(new GuiIngameForgeTransformer());
        registerTransformer(new EntityPlayerSPTransformer());
        registerTransformer(new GuiChatTransformer());
    }

    private void registerTransformer(HytilitiesTransformer transformer) {
        for (String cls : transformer.getClassName()) {
            transformerMap.put(cls, transformer);
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (bytes == null) return null;

        final Collection<HytilitiesTransformer> transformers = transformerMap.get(transformedName);
        if (transformers.isEmpty()) return bytes;

        final ClassReader classReader = new ClassReader(bytes);
        final ClassNode classNode = new ClassNode();
        classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

        for (HytilitiesTransformer transformer : transformers) {
            transformer.transform(classNode, transformedName);
        }

        final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        try {
            classNode.accept(classWriter);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (outputBytecode) {
            final File bytecodeDirectory = new File("bytecode");
            String transformedClassName;

            // anonymous classes
            if (transformedName.contains("$")) {
                transformedClassName = transformedName.replace('$', '.') + ".class";
            } else {
                transformedClassName = transformedName + ".class";
            }

            final File bytecodeOutput = new File(bytecodeDirectory, transformedClassName);

            try {
                if (!bytecodeDirectory.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    bytecodeDirectory.mkdirs();
                }

                if (!bytecodeOutput.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    bytecodeOutput.createNewFile();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try (FileOutputStream os = new FileOutputStream(bytecodeOutput)) {
                os.write(classWriter.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return classWriter.toByteArray();
    }
}
