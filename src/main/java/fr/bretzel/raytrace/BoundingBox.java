package fr.bretzel.raytrace;

import net.minecraft.server.v1_15_R1.AxisAlignedBB;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class BoundingBox
{

    //min and max points of hit box
    Vector max;
    Vector min;

    public BoundingBox(Block block)
    {
        org.bukkit.util.BoundingBox boundingBox = block.getBoundingBox();
        max = boundingBox.getMax();
        min = boundingBox.getMin();
    }

    public BoundingBox(Vector min, Vector max)
    {
        this.max = max;
        this.min = min;
    }

    public BoundingBox(AxisAlignedBB bb)
    {
        min = new Vector(bb.minX, bb.minY, bb.minZ);
        max = new Vector(bb.maxX, bb.maxY, bb.maxZ);
    }

    public Vector midPoint()
    {
        return max.clone().add(min).multiply(0.5);
    }

    public Vector getMax()
    {
        return max;
    }

    public Vector getMin()
    {
        return min;
    }

    public boolean isInside(Vector position)
    {
        if (position.getX() < min.getX() || position.getX() > max.getX())
        {
            return false;
        } else if (position.getY() < min.getY() || position.getY() > max.getY())
        {
            return false;
        } else return !(position.getZ() < min.getZ()) && !(position.getZ() > max.getZ());
    }
}
