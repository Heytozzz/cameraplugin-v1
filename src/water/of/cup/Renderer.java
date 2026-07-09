package water.of.cup;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

/**
 * Renders one photo. Spreads the 128x128 raycasts across multiple ticks instead of
 * doing all ~16,384 of them in a single render() call — that single-call approach was
 * a real (if brief) lag spike on busy servers, especially now that each ray can also
 * retry a few times for sparse plants and test entities separately. Bukkit calls
 * render() repeatedly for as long as a map is unlocked and being viewed; each call here
 * just picks up a few more columns where the previous one left off.
 */
public class Renderer extends MapRenderer {

	private static final int RESOLUTION = 128;

	private final CameraProfile profile;
	private final Utils.Filter filter;

	// Progressive-render state — populated once on the first render() call so every
	// later chunk uses the exact same viewpoint/settings, instead of re-reading the
	// player's (possibly since-moved) position on every tick.
	private boolean initialized = false;
	private boolean finished = false;
	private int nextColumn = 0;
	private int columnsPerTick = 16;

	private World world;
	private Player photographer;
	private Location eyes;
	private Vector eyesVec;
	private double pitch, yaw, fovRadians, maxDistance, skyDistance;
	private boolean shadowsEnabled, reliefEnabled, fogEnabled, entitiesEnabled;
	private double reliefStrength, fogDistance, entityRaySize;
	private Utils.PostFX postFx;
	private Predicate<Entity> entityFilter;

	private double detailDistance;

	private byte[][] canvasBytes;
	private double[] prevColumnDistance;

	// What actually showed up in this photo, for the collection album feature.
	private final Set<Material> discoveredBlocks = new HashSet<>();
	private final Set<EntityType> discoveredEntities = new HashSet<>();

	public Renderer(CameraProfile profile, Utils.Filter filter) {
		this.profile = profile;
		this.filter = filter;
	}

	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {
		if (finished || map.isLocked()) {
			return;
		}

		if (!initialized) {
			initialize(player);
		}

		int columnsThisTick = Math.min(columnsPerTick, RESOLUTION - nextColumn);
		for (int i = 0; i < columnsThisTick; i++) {
			renderColumn(nextColumn, canvas);
			nextColumn++;
		}

		if (nextColumn >= RESOLUTION) {
			finished = true;
			byte[][] finishedBytes = canvasBytes;
			Bukkit.getScheduler().runTaskAsynchronously(Camera.getInstance(), () -> MapStorage.store(map.getId(), finishedBytes));
			map.setLocked(true);

			if (photographer != null) {
				AlbumManager.recordDiscoveries(photographer, discoveredBlocks, discoveredEntities);
			}
		}
	}

	private void initialize(Player player) {
		this.photographer = player;
		this.world = player.getWorld();
		this.eyes = player.getEyeLocation();
		this.eyesVec = eyes.toVector();
		this.pitch = -Math.toRadians(eyes.getPitch());
		this.yaw = Math.toRadians(eyes.getYaw() + 90);

		this.shadowsEnabled = profile.isShadowsEnabled();
		this.fovRadians = Math.toRadians(profile.getFov());
		this.reliefEnabled = profile.isReliefEnabled();
		this.reliefStrength = profile.getReliefStrength();
		this.fogEnabled = profile.isFogEnabled();
		this.fogDistance = profile.getFogDistance();
		this.entitiesEnabled = profile.isEntitiesEnabled();
		this.entityRaySize = profile.getEntityRaySize();
		this.maxDistance = profile.getMaxDistance();
		this.skyDistance = maxDistance;
		this.columnsPerTick = Math.max(1, profile.getColumnsPerTick());

		this.postFx = Utils.combine(profile.getPostFx(), filter);
		this.detailDistance = Camera.getInstance().getConfig().getDouble("settings.album.detail-distance", 15);

		// Captures every entity except the photographer — the old "instanceof Animals"
		// check silently excluded squids, dolphins and bats, which was the "some mobs
		// never show up" bug. Utils.isCapturableEntity only excludes non-visual/marker
		// entities (dropped items, XP orbs, etc.) that wouldn't look like anything anyway.
		this.entityFilter = e -> !e.equals(player) && Utils.isCapturableEntity(e.getType());

		this.canvasBytes = new byte[RESOLUTION][RESOLUTION];
		this.prevColumnDistance = new double[RESOLUTION];
		Arrays.fill(prevColumnDistance, -1);

		this.initialized = true;
	}

	private void renderColumn(int x, MapCanvas canvas) {
		for (int y = 0; y < RESOLUTION; y++) {

			double yrotate = -((y) * fovRadians / RESOLUTION - fovRadians / 2);
			double xrotate = ((x) * fovRadians / RESOLUTION - fovRadians / 2);

			Vector rayVector = new Vector(
					Math.cos(yaw + xrotate) * Math.cos(pitch + yrotate),
					Math.sin(pitch + yrotate),
					Math.sin(yaw + xrotate) * Math.cos(pitch + yrotate));

			// FluidCollisionMode.ALWAYS: water (and lava) now register as their own hit
			// instead of being skipped through to whatever's underneath — that "never"
			// mode was why open water rendered as invisible/transparent before.
			// ignorePassableBlocks = false: this is what makes tall grass, ferns, saplings,
			// flowers and vines actually register a hit instead of being skipped over as
			// if they were air. raytraceThroughSparsePlants additionally simulates seeing
			// through the *gaps* of those plants (see its own comment for why that needs
			// a workaround at all).
			RayTraceResult blockResult = raytraceThroughSparsePlants(world, eyes, rayVector, maxDistance);

			RayTraceResult entityResult = null;
			if (entitiesEnabled) {
				entityResult = world.rayTraceEntities(eyes, rayVector, maxDistance, entityRaySize, entityFilter);
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
				colorByte = Utils.colorFromEntity(entityResult.getHitEntity(), entityResult.getHitPosition(), shade, fogBlend, postFx);
				if (currentDistance <= detailDistance) {
					discoveredEntities.add(entityResult.getHitEntity().getType());
				}
			} else if (blockResult != null) {
				currentDistance = blockDist;
				byte lightLevel = blockResult.getHitBlock().getRelative(blockResult.getHitBlockFace()).getLightLevel();
				double shade = shadeFor(lightLevel, shadowsEnabled, currentDistance, y, prevColumnDistance,
						reliefEnabled, reliefStrength);
				double fogBlend = fogFactor(currentDistance, fogEnabled, fogDistance);
				colorByte = Utils.colorFromType(blockResult.getHitBlock(), blockResult.getHitPosition(),
						blockResult.getHitBlockFace(), shade, fogBlend, postFx);
				if (currentDistance <= detailDistance) {
					discoveredBlocks.add(blockResult.getHitBlock().getType());
				}
			} else {
				// no block/entity hit: sky
				canvas.setPixel(x, y, MapPalette.PALE_BLUE);
				canvasBytes[x][y] = MapPalette.PALE_BLUE;
				prevColumnDistance[y] = skyDistance;
				continue;
			}

			canvas.setPixel(x, y, colorByte);
			canvasBytes[x][y] = colorByte;
			prevColumnDistance[y] = currentDistance;
		}
	}

	/**
	 * Bukkit's raytrace only sees a block's simplified bounding/outline shape, not its
	 * real visual mesh — so a hit on e.g. SHORT_GRASS looks exactly like a hit on a full
	 * solid cube, even though visually most of that cube is empty space between the
	 * "blades" of the cross model. There is no server-side API that knows about that
	 * finer mesh detail, so this fakes it statistically: each hit on a known "sparse"
	 * plant material rolls a deterministic (position-based, so a locked photo can't
	 * flicker) chance of actually registering, based on roughly how much of the block's
	 * silhouette that plant visually covers. A "miss" nudges the ray origin just past
	 * this block and tries again, so the ray effectively looks straight through the gap.
	 */
	private RayTraceResult raytraceThroughSparsePlants(World world, Location startEyes, Vector rayVector, double maxDistance) {
		Location origin = startEyes.clone();
		double traveled = 0;
		Vector normalizedRay = rayVector.clone().normalize();

		for (int attempt = 0; attempt < 6; attempt++) {
			double remaining = maxDistance - traveled;
			if (remaining <= 0) {
				return null;
			}
			RayTraceResult result = world.rayTraceBlocks(origin, rayVector, remaining, FluidCollisionMode.ALWAYS, false);
			if (result == null || result.getHitBlock() == null) {
				return result;
			}

			Double coverage = Utils.getSparsePlantCoverage(result.getHitBlock().getType());
			if (coverage == null) {
				return result; // solid enough — this is a real hit
			}

			Vector hitPos = result.getHitPosition();
			double roll = Utils.deterministicRandom(hitPos.getX() * 12.9, hitPos.getY() * 78.2, hitPos.getZ() * 37.7);
			if (roll < coverage) {
				return result; // rolled a "hit" on the sparse geometry
			}

			// "missed" — nudge past this block and keep looking
			double hitDistance = hitPos.distance(origin.toVector());
			traveled += hitDistance + 0.05;
			origin = hitPos.toLocation(world).add(normalizedRay.clone().multiply(0.05));
		}

		return null; // gave up after too many sparse blocks in a row
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
