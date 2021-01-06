package club.sk1er.hytilities.tweaker.asm;

import club.sk1er.hytilities.tweaker.transformer.HytilitiesTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class GuiScreenTransformer implements HytilitiesTransformer  {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.gui.GuiScreen"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            final String methodName = mapMethodName(classNode, method);
            if (methodName.equals("mouseClicked") || methodName.equals("func_73864_a")) {
                final InsnList instructions = method.instructions;
                final ListIterator<AbstractInsnNode> iterator = instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode node = iterator.next();

                    if (node instanceof TypeInsnNode && node.getOpcode() == Opcodes.NEW) {
                        instructions.insertBefore(node, addScreenHook());
                        break;
                    }
                }
            }
        }
    }

    public InsnList addScreenHook() {
        final InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, getHooksPackage() + "GuiChestHook", "mouseClicked", "(Lnet/minecraft/client/gui/GuiScreen;)V", false));
        final LabelNode node = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, node));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(node);
        return list;
    }
}
