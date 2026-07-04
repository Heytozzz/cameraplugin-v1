package water.of.cup;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ResourcePackManager {

    private File resourcePackFile;
    private final HashMap<Material, BufferedImage> imageHashMap = new HashMap<>();
    // filename (lowercase, no path) -> actual file on disk. Built once via a recursive
    // walk instead of re-scanning the whole pack folder for every single Material,
    // which is what used to make plugin startup crawl.
    private final HashMap<String, File> textureIndex = new HashMap<>();

    public void initialize() {
        String source = Camera.getInstance().getConfig().getString("settings.camera.resourcepack.source", "LEGACY");

        if ("GITHUB".equalsIgnoreCase(source)) {
            String repo = Camera.getInstance().getConfig().getString(
                    "settings.camera.resourcepack.github-repo", "InventivetalentDev/minecraft-assets");
            String ref = Camera.getInstance().getConfig().getString(
                    "settings.camera.resourcepack.github-ref", "1.21.4");
            downloadGithubTexturePack(repo, ref);
        } else {
            downloadLegacyResourcePackIfNeeded();
        }

        buildTextureIndex();
        initializeImageHashmap();

        Bukkit.getLogger().info("[Cameras] Resource pack ready: "
                + (this.resourcePackFile != null ? this.resourcePackFile.getName() : "none")
                + " (" + this.imageHashMap.size() + " textures loaded)");
    }

    public File getTextureByMaterial(Material material) {
        return textureIndex.get(material.toString().toLowerCase() + ".png");
    }

    private void initializeImageHashmap() {
        for (Material material : Material.values()) {
            File textureFile = this.getTextureByMaterial(material);
            if (textureFile != null) {
                try {
                    BufferedImage image = ImageIO.read(textureFile);
                    if (image != null) {
                        imageHashMap.put(material, image);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // -----------------------------------------------------------------
    // Texture index (filename -> File), works the same whether the pack is a
    // flat legacy folder or a nested "block/", "item/", "entity/"... structure.
    // -----------------------------------------------------------------

    private void buildTextureIndex() {
        textureIndex.clear();
        if (resourcePackFile == null || !resourcePackFile.exists()) {
            return;
        }
        // Index the "block" subfolder first (if present) so block textures win over
        // any same-named item/entity texture elsewhere in the pack.
        File blockDir = new File(resourcePackFile, "block");
        if (blockDir.exists()) {
            walkAndIndex(blockDir);
        }
        walkAndIndex(resourcePackFile);
    }

    private void walkAndIndex(File dir) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                walkAndIndex(f);
            } else if (f.getName().toLowerCase().endsWith(".png")) {
                textureIndex.putIfAbsent(f.getName().toLowerCase(), f);
            }
        }
    }

    // -----------------------------------------------------------------
    // LEGACY source: the small, bundled-download 1.16.4 flat pack (original behaviour)
    // -----------------------------------------------------------------

    private void downloadLegacyResourcePackIfNeeded() {
        File dataFolder = Camera.getInstance().getDataFolder();
        File mapDir = new File(dataFolder, "resource-packs");
        if (!mapDir.exists()) {
            mapDir.mkdir();
        }

        File legacyDir = new File(mapDir, "1_16_4");
        if (legacyDir.exists() && legacyDir.listFiles() != null && legacyDir.listFiles().length > 0) {
            this.resourcePackFile = legacyDir;
            return;
        }

        File[] existing = mapDir.listFiles();
        if (existing != null && existing.length > 0) {
            // Something is already there from a previous run (possibly a github- folder
            // left over from switching modes) — reuse the first folder we find.
            for (File f : existing) {
                if (f.isDirectory()) {
                    this.resourcePackFile = f;
                    return;
                }
            }
        }

        Bukkit.getLogger().info("[Cameras] No resource pack found, downloading the legacy pack...");
        File fileLocation = new File(mapDir, "1_16_4.zip");
        try (BufferedInputStream in = new BufferedInputStream(new URL(
                "https://github.com/Cup0fCode/resource-packs/raw/main/1_16_4.zip").openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(fileLocation)) {
            byte[] dataBuffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, dataBuffer.length)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }

            if (!legacyDir.exists()) {
                legacyDir.mkdir();
            }

            ZipUtils.unzip(fileLocation, legacyDir.getPath() + "/");
            fileLocation.delete();
            this.resourcePackFile = legacyDir;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -----------------------------------------------------------------
    // GITHUB source: downloads just assets/minecraft/textures/** from a
    // minecraft-assets-style GitHub repo at a given branch/tag, discarding
    // everything else (models, sounds, lang, etc.) to save space.
    // -----------------------------------------------------------------

    private void downloadGithubTexturePack(String repo, String ref) {
        File dataFolder = Camera.getInstance().getDataFolder();
        File mapDir = new File(dataFolder, "resource-packs");
        if (!mapDir.exists()) {
            mapDir.mkdir();
        }

        String safeName = "github-" + repo.replace("/", "_") + "-" + ref.replace("/", "_");
        File packDir = new File(mapDir, safeName);
        if (packDir.exists() && packDir.listFiles() != null && packDir.listFiles().length > 0) {
            this.resourcePackFile = packDir;
            return;
        }
        packDir.mkdirs();

        File zipFile = new File(mapDir, "github-download-tmp.zip");
        boolean downloaded = false;
        for (String refType : new String[] { "heads", "tags" }) {
            String url = "https://codeload.github.com/" + repo + "/zip/refs/" + refType + "/" + ref;
            try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
                 FileOutputStream out = new FileOutputStream(zipFile)) {
                Bukkit.getLogger().info("[Cameras] Downloading texture pack from " + repo + "@" + ref
                        + " (this can take a little while)...");
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) != -1) {
                    out.write(buf, 0, n);
                }
                downloaded = true;
                break;
            } catch (IOException e) {
                // try the next ref type (branch vs tag) before giving up
            }
        }

        if (!downloaded) {
            Bukkit.getLogger().warning("[Cameras] Could not download " + repo + "@" + ref
                    + " — check settings.camera.resourcepack.github-repo/github-ref. Falling back to the legacy pack.");
            downloadLegacyResourcePackIfNeeded();
            return;
        }

        int extracted = 0;
        try (ZipFile zf = new ZipFile(zipFile)) {
            Enumeration<? extends ZipEntry> entries = zf.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                String lower = entry.getName().toLowerCase();
                int idx = lower.indexOf("/textures/");
                if (idx == -1 || !lower.endsWith(".png")) {
                    continue; // skip models, blockstates, sounds, lang, .mcmeta, etc.
                }
                String relative = entry.getName().substring(idx + "/textures/".length());
                File outFile = new File(packDir, relative);
                File parent = outFile.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                try (InputStream is = zf.getInputStream(entry);
                     FileOutputStream os = new FileOutputStream(outFile)) {
                    byte[] buf = new byte[4096];
                    int n;
                    while ((n = is.read(buf)) != -1) {
                        os.write(buf, 0, n);
                    }
                }
                extracted++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        zipFile.delete();
        Bukkit.getLogger().info("[Cameras] Extracted " + extracted + " textures from " + repo + "@" + ref);
        this.resourcePackFile = packDir;
    }

    public HashMap<Material, BufferedImage> getImageHashMap() {
        return this.imageHashMap;
    }

    public boolean isLoaded() {
        return this.resourcePackFile != null && !this.imageHashMap.isEmpty();
    }

}
