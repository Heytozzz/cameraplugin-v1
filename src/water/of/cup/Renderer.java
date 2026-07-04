package water.of.cup;

import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class Renderer extends MapRenderer {

	private static final int RESOLUTION = 128;
	private static final double MAX_DISTANCE = 256;
	// A large sentinel distance used for "sky" pixels so relief shading doesn't treat
	// the sky/terrain boundary as a depth discontinuity.
	private static final double SKY_DISTANCE = MAX_DISTANCE;

	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {
		if (map.isLocked()) {
			return;
		}

		var config = Camera.getInstance().getConfig();
		boolean shadowsEnabled = config.getBoolean("settings.render.shadows", true);
		boolean reliefEnabled = config.getBoolean("settings.render.relief.enabled", true);
		double reliefStrength = config.getDouble("settings.render.relief.strength", 0.18);
		boolean fogEnabled = config.getBoolean("settings.render.fog.enabled", true);
		double fogDistance = config.getDouble("settings.render.fog.distance", 180);
		boolean entitiesEnabled = config.getBoolean("settings.render.animals.enabled", true);
		double entityRaySize = config.getDouble("settings.render.entity-ray-size", -0.15);

		Location eyes = player.getEyeLocation();
		Vector eyesVec = eyes.toVector();
		double pitch = -Math.toRadians(eyes.getPitch());
		double yaw = Math.toRadians(eyes.getYaw() + 90);

		// Captures every entity except the photographer — the old "instanceof Animals"
		// check silently excluded squids, dolphins and bats, which was the "some mobs
		// never show up" bug. Utils.isCapturableEntity only excludes non-visual/marker
		// entities (dropped items, XP orbs, etc.) that wouldn't look like anything anyway.
		Predicate<Entity> entityFilter = e -> !e.equals(player) && Utils.isCapturableEntity(e.getType());

		byte[][] canvasBytes = new byte[RESOLUTION][RESOLUTION];
		// distance of the previous column (x-1) at each row y, used for relief/edge shading
		double[] prevColumnDistance = new double[RESOLUTION];
		java.util.Arrays.fill(prevColumnDistance, -1);

		for (int x = 0; x < RESOLUTION; x++) {
			for (int y = 0; y < RESOLUTION; y++) {

				double yrotate = -((y) * .9 / RESOLUTION - .45);
				double xrotate = ((x) * .9 / RESOLUTION - .45);

				Vector rayVector = new Vector(
						Math.cos(yaw + xrotate) * Math.cos(pitch + yrotate),
						Math.sin(pitch + yrotate),
						Math.sin(yaw + xrotate) * Math.cos(pitch + yrotate));

				// FluidCollisionMode.ALWAYS: water (and lava) now register as their own hit
				// instead of being skipped through to whatever's underneath — that "never"
				// mode was why open water rendered as invisible/transparent before.
				// ignorePassableBlocks = false: this is what makes tall grass, ferns, saplings,
				// flowers and vines actually register a hit instead of being skipped over as
				// if they were air.
				RayTraceResult blockResult = player.getWorld().rayTraceBlocks(
						eyes, rayVector, MAX_DISTANCE, FluidCollisionMode.ALWAYS, false);

				RayTraceResult entityResult = null;
				if (entitiesEnabled) {
					entityResult = player.getWorld().rayTraceEntities(
							eyes, rayVector, MAX_DISTANCE, entityRaySize, entityFilter);
				}

				double blockDist = blockResult != null ? blockResult.getHitPosition().distance(eyesVec) : Double.MAX_VALUE;
				double entityDist = entityResult != null ? entityResult.getHitPosition().distance(eyesVec) : Double.MAX_VALUE;

				byte colorByte;
				double currentDistance;

				if (entityResult != null && entityDist < blockDist) {
					currentDistance = entityDist;
					double shade = shadeFor(entityResult.getHitEntity().getLocation().getBlock().getLightLevel(),
							shadowsEnabled, currentDistance, y, prevColumnDistance, reliefEnabled, reliefStrength);
					double fogBlend = fogFactor(currentDistance, fogEnabled, fogDistance);
					colorByte = Utils.colorFromEntity(entityResult.getHitEntity(), entityResult.getHitPosition(), shade, fogBlend);
				} else if (blockResult != null) {
					currentDistance = blockDist;
					byte lightLevel = blockResult.getHitBlock().getRelative(blockResult.getHitBlockFace()).getLightLevel();
					double shade = shadeFor(lightLevel, shadowsEnabled, currentDistance, y, prevColumnDistance,
							reliefEnabled, reliefStrength);
					double fogBlend = fogFactor(currentDistance, fogEnabled, fogDistance);
					colorByte = Utils.colorFromType(blockResult.getHitBlock(), blockResult.getHitPosition(),
							blockResult.getHitBlockFace(), shade, fogBlend);
				} else {
					// no block/entity hit: sky
					canvas.setPixel(x, y, MapPalette.PALE_BLUE);
					canvasBytes[x][y] = MapPalette.PALE_BLUE;
					prevColumnDistance[y] = SKY_DISTANCE;
					continue;
				}

				canvas.setPixel(x, y, colorByte);
				canvasBytes[x][y] = colorByte;
				prevColumnDistance[y] = currentDistance;
			}
		}

		Bukkit.getScheduler().runTaskAsynchronously(Camera.getInstance(), () -> MapStorage.store(map.getId(), canvasBytes));

		map.setLocked(true);
	}

	/**
	 * Combines vanilla light level with relief (edge/depth) shading into a single
	 * brightness multiplier. Relief compares this pixel's hit distance against the
	 * previous column's hit distance at the same row: closer than its neighbor
	 * (sticking out toward the camera) gets lighter, farther (receding) gets darker —
	 * the same idea vanilla uses to shade hills on the in-hand map, adapted to a
	 * first-person raycast instead of a top-down height map.
	 */
	private static double shadeFor(byte lightLevel, boolean shadowsEnabled, double currentDistance, int y,
			double[] prevColumnDistance, boolean reliefEnabled, double reliefStrength) {
		double shade = 1.0;

		if (shadowsEnabled) {
			double lightFactor = Math.max(lightLevel / 15.0, 0.35); // floor so it's never pitch black
			shade *= lightFactor;
		}

		if (reliefEnabled && prevColumnDistance[y] > 0) {
			double diff = prevColumnDistance[y] - currentDistance; // positive = this pixel is closer (protrudes)
			double normalized = Math.max(-1.0, Math.min(1.0, diff / 4.0)); // 4 blocks of difference maxes it out
			shade *= (1.0 + normalized * reliefStrength);
		}

		return Math.max(0.25, Math.min(1.6, shade));
	}

	/** 0 = no fog, up to 0.85 = heavy fog (never fully hides the silhouette). */
	private static double fogFactor(double distance, boolean fogEnabled, double fogDistance) {
		if (!fogEnabled || distance <= fogDistance) {
			return 0.0;
		}
		double over = distance - fogDistance;
		double factor = over / (fogDistance * 1.2);
		return Math.max(0.0, Math.min(0.85, factor));
	}
}
