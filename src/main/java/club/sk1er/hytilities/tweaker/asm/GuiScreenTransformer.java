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
            String methodName = mapMethodName(classNode, method);
            InsnList instructions = method.instructions;
            ListIterator<AbstractInsnNode> iterator = instructions.iterator();

            if (methodName.equals("mouseClicked") || methodName.equals("func_73864_a")) {
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();

                    if (node instanceof TypeInsnNode && node.getOpcode() == Opcodes.NEW) {
                        instructions.insertBefore(node, addScreenHook());
                        break;
                    }
                }
            }
        }
    }

    public InsnList addScreenHook() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, getHooksPackage() + "GuiChestHook", "mouseClicked", "(Lnet/minecraft/client/gui/GuiScreen;)V", false));
        LabelNode node = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, node));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(node);
        return list;
    }
}
