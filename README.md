# Hotbar Saver
**Made by rackeldevs**

Save and restore your hotbar in any gamemode — Survival, Creative, Adventure.  
Works across different servers. No server-side mod needed.

---

## Requirements

- Minecraft **1.21.11**
- Fabric Loader **≥ 0.18.0**
- Fabric API
- Mod Menu *(optional — for in-game config screen)*

---

## Installation

1. Download the latest `hotbarsaver-1.0.0.jar` from the [Releases](../../releases) page
2. Drop it into your `.minecraft/mods/` folder
3. Also install **Fabric API** and **Mod Menu** if you haven't already
4. Launch Minecraft

---

## Setup (one time)

1. From the main menu, click **Mods** → find **Hotbar Saver** → click the ⚙️ config button
2. You'll see two fields:
   - **Save modifier key** — type any letter (default: `X`)
   - **Load modifier key** — type any letter (default: `V`)
3. Click **Save & Close**

> You can also press **H** at any time while in-game to open the config screen.

---

## How to Use

### Saving a hotbar
1. Arrange your hotbar exactly how you want it
2. Hold your **save key** and press a number **1–9**
   - Example: `X+1` saves your current hotbar to slot 1
3. You'll see a green confirmation message: *✔ Hotbar saved to slot 1 (X+1) — Made by rackeldevs*

You can save up to **9 different hotbars** (slots 1–9).

### Restoring a hotbar
1. Hold your **load key** and press the slot number
   - Example: `V+1` restores the hotbar saved in slot 1
2. You'll see: *✔ Slot 1 restored! (V+1) — Made by rackeldevs*

---

## Typical Workflow

> **Use case:** You play on a survival server but want to carry your item setup into a creative world.

1. On your **survival server** → set up your hotbar (shulkers, tools, etc.) → press `X+1` to save
2. Join your **creative world** → press `V+1` to restore it instantly

The saved hotbar lives at `.minecraft/hotbar.nbt` and persists across sessions and server restarts.

---

## What Gets Preserved

| Data | Preserved? |
|------|-----------|
| Enchantments | ✅ Yes |
| Custom item names & colors | ✅ Yes |
| Custom lore | ✅ Yes |
| Player head textures | ✅ Yes |
| Shulker box contents | ✅ Yes |
| Map art | ✅ Yes |
| Stack count | ✅ Yes |

---

## Keybinds

| Key | Action |
|-----|--------|
| `H` | Open config screen (in-game) |
| Save key + `1~9` | Save hotbar to slot 1–9 |
| Load key + `1~9` | Restore hotbar from slot 1–9 |

All keys are configurable in the config screen.

---

## Building from Source

Requirements: **JDK 21**, internet connection

```bash
git clone https://github.com/rackeldevs1/survivalhotbarsaving
cd survivalhotbarsaving
./gradlew build          # Mac/Linux
gradlew.bat build        # Windows
```

Output jar: `build/libs/hotbarsaver-1.0.0.jar`

---

## License

MIT — free to use, modify, and redistribute.

---

*Made with ❤️ by rackeldevs*
