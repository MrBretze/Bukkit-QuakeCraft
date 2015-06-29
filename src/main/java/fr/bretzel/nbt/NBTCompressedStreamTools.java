package fr.bretzel.nbt;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class NBTCompressedStreamTools {

    public static NBTTagCompound read(InputStream inputstream) throws IOException {
        DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(inputstream)));

        NBTTagCompound nbttagcompound;

        try {
            nbttagcompound = read(datainputstream, NBTReadLimiter.a);
        } finally {
            datainputstream.close();
        }

        return nbttagcompound;
    }

    public static void wrhite(NBTTagCompound nbttagcompound, OutputStream outputstream) throws IOException {
        DataOutputStream dataoutputstream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputstream)));

        try {
            a(nbttagcompound, dataoutputstream);
        } finally {
            dataoutputstream.close();
        }

    }

    public static NBTTagCompound read(DataInputStream datainputstream) throws IOException {
        return read(datainputstream, NBTReadLimiter.a);
    }

    public static NBTTagCompound read(DataInput datainput, NBTReadLimiter nbtreadlimiter) throws IOException {
        NBTBase nbtbase = a(datainput, 0, nbtreadlimiter);

        if (nbtbase instanceof NBTTagCompound) {
            return (NBTTagCompound) nbtbase;
        } else {
            throw new IOException("Root tag must be a named compound tag");
        }
    }

    public static void wrhite(NBTTagCompound nbttagcompound, DataOutput dataoutput) throws IOException {
        a(nbttagcompound, dataoutput);
    }

    private static void a(NBTBase nbtbase, DataOutput dataoutput) throws IOException {
        dataoutput.writeByte(nbtbase.getTypeId());
        if (nbtbase.getTypeId() != 0) {
            dataoutput.writeUTF("");
            nbtbase.write(dataoutput);
        }
    }

    private static NBTBase a(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
        byte b0 = datainput.readByte();

        if (b0 == 0) {
            return new NBTTagEnd();
        } else {
            datainput.readUTF();
            NBTBase nbtbase = NBTBase.createTag(b0);

            try {
                nbtbase.load(datainput, i, nbtreadlimiter);
                return nbtbase;
            } catch (IOException ioexception) {
                throw new RuntimeException(ioexception);
            }
        }
    }
}
