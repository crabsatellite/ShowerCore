package mod.crabmod.showercore.particle;

import mod.crabmod.showercore.ClientConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.WaterDropParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class ShowerParticle extends WaterDropParticle {
  private final double invisibleHeightMin;
  private final double invisibleHeightMax;

  // User ThreadLocal to store the invisible height range
  public static final ThreadLocal<Double> INVISIBLE_HEIGHT_MIN = new ThreadLocal<>();
  public static final ThreadLocal<Double> INVISIBLE_HEIGHT_MAX = new ThreadLocal<>();

  protected ShowerParticle(
      ClientLevel world,
      double x,
      double y,
      double z,
      double velocityX,
      double velocityY,
      double velocityZ,
      double invisibleHeightMin,
      double invisibleHeightMax) {
    super(world, x, y, z);
    this.gravity = 0.1F;
    this.setSize(0.02F, 0.02F);
    this.lifetime = 40;
    this.setAlpha(0.0F);
    this.rCol = 1.0F;
    this.gCol = 1.0F;
    this.bCol = 1.0F;
    this.invisibleHeightMin = invisibleHeightMin;
    this.invisibleHeightMax = invisibleHeightMax;
  }

  @Override
  public void tick() {
    super.tick();

    // Adjust the transparency of the particle dynamically according to the height of the particle
    if (this.y >= invisibleHeightMin && this.y <= invisibleHeightMax) {
      this.setAlpha(
          0.0F); // Set the transparency to 0 when the particle is in the specified height range
    } else {
      this.setAlpha(ClientConfig.enableTranslucentParticles ? 0.9F : 1.0F);
    }
  }

  @Override
  public ParticleRenderType getRenderType() {
    return ClientConfig.enableTranslucentParticles
        ? ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT
        : ParticleRenderType.PARTICLE_SHEET_OPAQUE;
  }

  public static class Provider implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet sprite;

    public Provider(SpriteSet sprite) {
      this.sprite = sprite;
    }

    @Override
    public Particle createParticle(
        SimpleParticleType type,
        ClientLevel world,
        double x,
        double y,
        double z,
        double velocityX,
        double velocityY,
        double velocityZ) {
      double invisibleHeightMin = INVISIBLE_HEIGHT_MIN.get();
      double invisibleHeightMax = INVISIBLE_HEIGHT_MAX.get();

      ShowerParticle particle =
          new ShowerParticle(
              world,
              x,
              y,
              z,
              velocityX,
              velocityY,
              velocityZ,
              invisibleHeightMin,
              invisibleHeightMax);
      particle.pickSprite(this.sprite);
      return particle;
    }
  }
}
