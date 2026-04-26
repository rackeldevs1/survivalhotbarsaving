# Hotbar Saver — Fabric Mod

Save and restore your hotbar in **any gamemode** (Survival, Creative, Adventure).  
Works exactly like vanilla's creative hotbar saving, but without the Creative Mode restriction.

## Keybindings

| Key | Action |
|-----|--------|
| **F6** | Save your current hotbar → writes to `.minecraft/hotbar.nbt` |
| **F7** | Restore your saved hotbar → reads from `.minecraft/hotbar.nbt` |

Both keys are rebindable in **Options → Controls → Hotbar Saver**.

## How it works

Vanilla Minecraft stores saved hotbars in `.minecraft/hotbar.nbt`.  
This mod writes and reads that same file — meaning:

- ✅ Hotbars saved here are **compatible with vanilla creative load** (and vice versa)
- ✅ Works across different servers (save on Server A, load on Server B)
- ✅ Client-side only — no server mod/plugin needed

## Building

Requirements: **JDK 21**, internet connection (first build downloads Minecraft mappings)

```bash
# Clone / place this folder somewhere
cd hotbarsaver

# Build the mod jar
./gradlew build        # Linux/Mac
gradlew.bat build      # Windows
```

Output jar will be at:
```
build/libs/hotbarsaver-1.0.0.jar
```

Copy that `.jar` into your `.minecraft/mods/` folder.

## Notes

- **Saving in Survival**: Items are saved with full NBT (enchantments, custom names, etc.)
- **Loading in Creative**: Works fine — items appear in your hotbar normally
- **Loading in Survival**: Items are placed directly into hotbar slots; server may reject modded/creative-only items depending on its anti-cheat
- The mod saves to hotbar slot **"0"** (the first of the 9 vanilla saved hotbars)

## Requirements

- Minecraft **1.21.8**
- Fabric Loader **≥ 0.16.0**
- Fabric API
