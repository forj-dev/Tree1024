package forj.tree1024.entity.rb_tree;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Quaternion;

/**
 * Pose class represents the position and orientation of an object in 3D space.
 * Pitch, yaw: radians
 */
public class Pose {
    public double x, y, z;
    public float pitch, yaw;

    public Pose(double x, double y, double z, float pitch, float yaw) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }
    protected Pose(Pose pose) {
        this.x = pose.x;
        this.y = pose.y;
        this.z = pose.z;
        this.pitch = pose.pitch;
        this.yaw = pose.yaw;
    }
    public Pose copy() {
        return new Pose(this);
    }

    public Pose forward(double distance) {
        Pose pose = copy();
        pose.x += -distance * Math.sin(-yaw) * Math.cos(pitch);
        pose.y += distance * Math.sin(pitch);
        pose.z += -distance * Math.cos(-yaw) * Math.cos(pitch);
        return pose;
    }

    /**
     * Rotate the pose by pitch and yaw.
     * Pitch and yaw are in radians.
     * The original rotation is (pitch=pi/2, yaw=0)
     * Pitch should be [-pi/2, pi/2]
     * Yaw should be [0, pi*2] or [-pi, pi]
     */
    public Pose rotate(float pitch, float yaw){
        final float rad90 = (float) Math.PI / 2;
        final float rad180 = (float) Math.PI;
        float newPitch = this.pitch + pitch - rad90;
        float newYaw = this.yaw + yaw;
        if (newPitch < -rad90)
            return new Pose(x, y, z, -newPitch-rad180, newYaw+rad180);
        return new Pose(x, y, z, newPitch, newYaw);
    }

    /**
     * Apply the pose to the matrix stack.
     */
    public void apply(MatrixStack matrices){
        matrices.translate(x, y, z);
        matrices.multiply(Quaternion.fromEulerXyz(pitch - (float) Math.PI / 2, yaw, 0));
    }
}
