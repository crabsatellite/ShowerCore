# ShowerCore 🚿

**[English]**

Welcome to **ShowerCore**! Tired of smelling like a zombie after a long mining trip? Want to gain superpowers just by standing under some water? You've come to the right place. This mod turns your boring hygiene routine into a magical, buff-granting experience.

> **Note:** This mod requires **[HotBath](https://www.curseforge.com/minecraft/mc-mods/hotbath)** to work properly. HotBath provides the basic bath fluids used in this mod.

## 🚿 The Shower Head

The heart of your bathroom. It's not just for looks; it's for _power_.

- **Install Core:** Slap a **Shower Core (Item)** in it (Right-click).
- **Swap Core:** Don't like that core? Slap another one in. The old one pops out.
- **Remove Core:** Regret your life choices? **Sneak + Right-click** with an empty hand to pop the core out.
- **Turn On/Off:** Just **Right-click** it. It's that simple. Even a Creeper could do it (please don't let Creepers in your bathroom).
- **Effect:** Stand under the running water to get buffs based on the installed core. No complex setup required!

### 🌟 Shower Head Effect Details

| Core Type     | Effects                                                                                          | Notes                                       |
| :------------ | :----------------------------------------------------------------------------------------------- | :------------------------------------------ |
| **Hot Water** | Speed II (10s refresh)                                                                           | Basic speed boost                           |
| **Milk**      | Regeneration II (10s refresh) + Removes negative effects + Saturation (every 15s)                | Cleanses all negative status effects        |
| **Herbal**    | Regeneration II + Resistance II (10s refresh) + Removes negative effects                         | Damages undead entities (1 HP every 2s)     |
| **Peony**     | Regeneration II + Luck II + Haste II (10s refresh) + Removes Bad Omen + Removes negative effects | Triple buff combo                           |
| **Honey**     | Regeneration II + Absorption IV (10s refresh) + Saturation (every 4s)                            | Provides golden hearts for extra protection |
| **Rose**      | Regeneration II + Strength II (10s refresh) + Removes Bad Omen + Removes negative effects        | Combat-focused buffs                        |

**Buff Stacking:** Effects stack up over time when continuously standing under the shower, providing increasing duration.

## 🔮 The Shower Core (Block)

Wait, the core is also a block? Yes!
If you place the **Shower Core** down as a block (instead of putting it in a shower head), it acts like a **Conduit**.

- **Activation (Feng Shui):** These Core Blocks are picky. They need specific "emotional support blocks" placed around them (in a frame structure) to activate.
- **AOE Buffs:** Once activated, they grant buffs to everyone in a large radius.

| Core Block    | Buffs (260 ticks/13s duration) | Activation Blocks (Frame)     |
| :------------ | :----------------------------- | :---------------------------- |
| **Hot Water** | Speed II                       | Prismarine, Prismarine Bricks |
| **Milk**      | Saturation II                  | Quartz Blocks (All types)     |
| **Herbal**    | Regeneration II                | Moss, Vines, Leaves           |
| **Peony**     | Luck II                        | Pink Wool/Concrete/Terracotta |
| **Honey**     | Absorption II                  | Honeycomb Block, Honey Block  |
| **Rose**      | Strength II                    | Red Wool/Concrete/Terracotta  |

## 🛁 The Bathtub

For when standing is just too much effort.

- **Fill 'er up:** Right-click with a bucket of water (or lava, if you're crazy). You can use **HotBath** fluids here for effects!
- **Visual Effects:** Hot water and HotBath fluids produce steam particles above the bathtub
- **The Faucet:** **Sneak + Right-click** the faucet part to make it go _whoosh_. Purely visual, but very satisfying.
- **Sit Down:** Right-click the head or foot of the tub to relax.
- **Shared Bath (Ooh la la):**
  - Lonely? Right-click an occupied tub to ask nicely.
  - The person inside gets an `[Accept]` / `[Reject]` prompt in chat. **Press 'T' to open chat and click it!**
  - (Or use commands: `/showercore accept_bath <player>` / `/showercore deny_bath <player>`)
  - **Note:** Don't run away! You must be within **5 blocks** to join.
  - If they accept, things get cozy. If they reject... well, awkward.

## 🦆 The Rubber Duck

The true boss of this mod.

- **Physics:** It floats! It bobs! It's adorable!
- **Spin:** Poke it (Right-click) to make it spin.
- **Pickup:** **Sneak + Right-click** to take your buddy home.
- **DANGER:** **DO NOT** throw it in lava. It will scream. You will feel bad. You monster.

## ⚙️ Configuration

You can tweak the mod to your liking in the config files.

- **Activation Blocks:** Change which blocks activate the Core Blocks (e.g., make Dirt activate the Hot Water Core if you're cheap).
- **Steam Fluids:** Define which _extra_ fluids (like Lava or modded fluids) make the bathtub steamy. (HotBath fluids steam automatically!)
- **Duck Destroyers:** Decide which fluids are dangerous for your rubber duck.
- **Client Visuals:** Toggle translucent particles for a mistier look.

## 🚫 Known Incompatibilities

- **Chunk Animator:** This mod messes with block rendering, limiting some features:
  - **Custom Fluids:** You can't pour custom fluids (from other mods) into the bathtub.
  - **Invincible Ducks:** The "Duck Destroyers" config breaks, so your duck won't die in dangerous fluids. (Wait, is that a bad thing?)
  - _Note: HotBath fluids still render fine._

## 🛠️ Crafting Recipes

### Bath Cores

| Item                 | Recipe                                                                                                     | Ingredients                                                                          |
| :------------------- | :--------------------------------------------------------------------------------------------------------- | :----------------------------------------------------------------------------------- |
| **Herbal Bath Core** | <img src="https://s2.loli.net/2025/12/13/smGlzIifASH3FCc.png" width="150" alt="Herbal Bath Core Recipe" /> | **Center:** Conduit<br>**Top, Bottom, Left, Right:** hotbath:bath_herb (Cross Shape) |
| **Honey Bath Core**  | <img src="https://s2.loli.net/2025/12/13/YRWjK3MbnB6va9x.png" width="150" alt="Honey Bath Core Recipe" />  | **Center:** Conduit<br>**Top, Bottom, Left, Right:** Honeycomb (Cross Shape)         |
| **Hot Water Core**   | <img src="https://s2.loli.net/2025/12/13/27wp1SMZj936HgX.png" width="150" alt="Hot Water Core Recipe" />   | **Center:** Conduit<br>**Top, Bottom, Left, Right:** Magma Cream (Cross Shape)       |
| **Milk Bath Core**   | <img src="https://s2.loli.net/2025/12/13/6uweN5IRJM9LVH8.png" width="150" alt="Milk Bath Core Recipe" />   | **Center:** Conduit<br>**Top, Bottom, Left, Right:** Milk Bucket (Cross Shape)       |
| **Peony Bath Core**  | <img src="https://s2.loli.net/2025/12/13/RYCEIUzTA1XWnbx.png" width="150" alt="Peony Bath Core Recipe" />  | **Center:** Conduit<br>**Top, Bottom, Left, Right:** Peony (Cross Shape)             |
| **Rose Bath Core**   | <img src="https://s2.loli.net/2025/12/13/sUpuyHzrGZWFKPR.png" width="150" alt="Rose Bath Core Recipe" />   | **Center:** Conduit<br>**Top, Bottom, Left, Right:** Rose Bush (Cross Shape)         |

### Bathtubs & Shower Heads

| Item                                           | Recipe                                                                                                        | Ingredients                                                                                                        |
| :--------------------------------------------- | :------------------------------------------------------------------------------------------------------------ | :----------------------------------------------------------------------------------------------------------------- |
| **Bathtub**<br>_(Example: Jungle)_             | <img src="https://s2.loli.net/2025/12/13/oyveJ1RlMD2TNGb.png" width="150" alt="Bathtub Recipe" />             | **Shape:** U-shape (Bottom 2 rows)<br>**Materials:** Jungle Planks (or other wood/stone) + Iron Nugget (Top Right) |
| **Compact Shower Head**<br>_(Example: Jungle)_ | <img src="https://s2.loli.net/2025/12/13/m81Lg3HjUc7lJYQ.png" width="150" alt="Compact Shower Head Recipe" /> | **Top Middle:** Bucket<br>**Middle:** Stick (Left), Jungle Planks (Right)                                          |
| **Rain Shower Head**<br>_(Example: Jungle)_    | <img src="https://s2.loli.net/2025/12/13/zjXch7KNHTw4ruq.png" width="150" alt="Rain Shower Head Recipe" />    | **Top Middle:** Bucket<br>**Middle:** Stick (Left), Jungle Planks (Right)<br>**Bottom Left:** Stick                |

### Others

| Item            | Recipe                                                                                                | Ingredients                                                                          |
| :-------------- | :---------------------------------------------------------------------------------------------------- | :----------------------------------------------------------------------------------- |
| **Rubber Duck** | <img src="https://s2.loli.net/2025/12/13/muoV9zUkEHJ3bCP.png" width="150" alt="Rubber Duck Recipe" /> | **Top/Bottom Rows:** Yellow Dye<br>**Middle Row:** Red Dye + Slime Ball + Yellow Dye |

---

**[中文]**

# ShowerCore 🚿

欢迎来到 **ShowerCore**！还在为挖矿归来一身僵尸味而烦恼吗？想洗个澡就能变强吗？那你来对地方了。本模组致力于将你枯燥的卫生习惯变成一场充满魔法和 Buff 的奇妙体验。

> **注意：** 本模组需要 **[HotBath](https://www.curseforge.com/minecraft/mc-mods/hotbath)** 作为前置模组。HotBath 提供了基础的洗浴液体。

## 🚿 洗浴喷头 (Shower Head)

浴室的灵魂。它不只是个装饰，它是力量的源泉！

- **安装核心**：把**洗浴核心 (物品)** 塞进去（右键）。
- **替换核心**：喜新厌旧？直接拿新核心怼上去，旧的会自动弹出来。
- **移除核心**：后悔了？**空手蹲下 + 右键** 把它扣出来。
- **开关**：**右键**点击即可。简单到连苦力怕都会用（但请千万别让它进浴室）。
- **效果**：只要站在喷头的水流下，就能获得对应核心的 Buff。**不需要**搭建复杂的激活结构！

### 🌟 喷头效果详细说明

| 核心类型             | 效果                                                                      | 备注                                    |
| :------------------- | :------------------------------------------------------------------------ | :-------------------------------------- |
| **热水** (Hot Water) | 速度 II (10 秒刷新)                                                       | 基础速度提升                            |
| **牛奶** (Milk)      | 生命恢复 II (10 秒刷新) + 清除负面效果 + 饱和度 (每 15 秒)                | 清除所有负面状态效果                    |
| **草药** (Herbal)    | 生命恢复 II + 抗性提升 II (10 秒刷新) + 清除负面效果                      | 对亡灵生物造成伤害 (每 2 秒 1 点生命值) |
| **牡丹** (Peony)     | 生命恢复 II + 幸运 II + 急迫 II (10 秒刷新) + 移除不祥之兆 + 清除负面效果 | 三重增益组合                            |
| **蜂蜜** (Honey)     | 生命恢复 II + 伤害吸收 IV (10 秒刷新) + 饱和度 (每 4 秒)                  | 提供伤害吸收金心以获得额外保护          |
| **玫瑰** (Rose)      | 生命恢复 II + 力量 II (10 秒刷新) + 移除不祥之兆 + 清除负面效果           | 战斗型增益                              |

如果你把**洗浴核心**直接放置在地上（而不是塞进喷头里），它就会变成一个类似**潮涌核心 (Conduit)** 的装置。

- **激活 (风水)**：作为方块时，核心很矫情。它们需要特定的“风水宝地”（在周围搭建特定方块的框架）才能激活。
- **范围 Buff**：一旦激活，它会给大范围内的所有玩家提供 Buff。

| 核心方块             | 增益效果 (Buff)             | 风水宝地 (激活方块)   |
| :------------------- | :-------------------------- | :-------------------- |
| **热水** (Hot Water) | 速度 II                     | 海晶石、海晶石砖      |
| **牛奶** (Milk)      | 生命恢复 II                 | 石英块 (各种)         |
| **草药** (Herbal)    | 生命恢复 II + 抗性 II       | 苔藓、藤蔓、树叶      |
| **牡丹** (Peony)     | 幸运 II + 急迫 II + 恢复 II | 粉色 羊毛/混凝土/陶瓦 |
| **蜂蜜** (Honey)     | 恢复 II + 伤害吸收 IV       | 蜜脾块、蜂蜜块        |
| **玫瑰** (Rose)      | 力量 II + 恢复 II           | 红色 羊毛/混凝土/陶瓦 |

## 🛁 浴缸 (Bathtub)

站着洗澡太累？那就躺着！

- **加水**：拿水桶（或者岩浆桶，如果你头铁的话）右键加满。你可以使用 **[HotBath](https://www.curseforge.com/minecraft/mc-mods/hotbath)** 的液体来获得他们的效果！
- **视觉效果**：热水和 HotBath 液体会在浴缸上方产生蒸汽粒子效果
- **水龙头**：**空手蹲下 + 右键** 点击水龙头，享受哗啦啦的流水声。虽然只是视觉效果，但很解压。
- **坐下**：右键点击浴缸头或尾，舒舒服服躺下。
- **鸳鸯浴 (Shared Bath)**：
  - 一个人洗澡太寂寞？右键点击已经有人的浴缸发起“共浴申请”。
  - 缸里的人会收到 `[Accept]` (接受) 和 `[Reject]` (拒绝) 的选项。**按 T 打开聊天栏点击它！**
  - (或者使用指令：`/showercore accept_bath <玩家名>` / `/showercore deny_bath <玩家名>`)
  - **注意**：别跑太远！必须在 **5 格** 范围内才能加入。
  - 如果对方接受，恭喜你，挤一挤更健康。如果对方拒绝……呃，这就很尴尬了。

## 🦆 橡皮鸭 (Rubber Duck)

本模组真正的霸主。

- **物理学**：它会漂！它会浮！它超级可爱！
- **互动**：戳它（右键）让它转圈圈。
- **带走**：**空手蹲下 + 右键** 把你的洗澡搭子带回家。
- **警告**：**千万不要** 把它扔进岩浆里。它会发出凄惨的叫声。你会良心不安的。你这个残忍的怪物。

## ⚙️ 配置文件 (Configuration)

你可以通过配置文件来调整模组的设定。

- **激活方块 (Activation Blocks)**：自定义每种核心方块需要什么方块来激活（比如你可以把海晶石改成泥土，如果你很抠门的话）。
- **蒸汽流体 (Steam Fluids)**：定义哪些*额外*的液体（比如岩浆或其他模组的液体）倒进浴缸会产生蒸汽效果。（HotBath 自带的洗澡水会自动产生蒸汽，不用配置！）
- **鸭子杀手 (Duck Destroyers)**：定义哪些液体会销毁橡皮鸭。
- **客户端视觉 (Client Visuals)**：开启/关闭半透明粒子效果，让水雾更逼真。

## 🚫 已知不兼容 (Known Incompatibilities)

- **Chunk Animator**：该模组修改了方块渲染方式，导致部分功能受限：
  - **自定义液体**：无法将其他模组的液体倒进浴缸。
  - **鸭子无敌**：“鸭子杀手”配置失效，鸭子在危险液体里不会被销毁。（这难道不是好事？）
  - _注：HotBath 自带的液体渲染依然正常。_

## 🛠️ 合成配方 (Crafting Recipes)

### 洗浴核心 (Bath Cores)

| 物品                                 | 配方                                                                                                       | 合成材料                                                                               |
| :----------------------------------- | :--------------------------------------------------------------------------------------------------------- | :------------------------------------------------------------------------------------- |
| **草药浴核心**<br>(Herbal Bath Core) | <img src="https://s2.loli.net/2025/12/13/xPnEKvFAqt4O5kM.png" width="150" alt="Herbal Bath Core Recipe" /> | **中心：** 潮涌核心 (Conduit)<br>**上下左右：** 洗浴用药草(hotbath:bath_herb) (十字形) |
| **蜂蜜浴核心**<br>(Honey Bath Core)  | <img src="https://s2.loli.net/2025/12/13/c3U41qvNLxbCGh5.png" width="150" alt="Honey Bath Core Recipe" />  | **中心：** 潮涌核心 (Conduit)<br>**上下左右：** 蜜脾 (Honeycomb) (十字形)              |
| **热水核心**<br>(Hot Water Core)     | <img src="https://s2.loli.net/2025/12/13/tOILqnZyoE7JaPd.png" width="150" alt="Hot Water Core Recipe" />   | **中心：** 潮涌核心 (Conduit)<br>**上下左右：** 岩浆膏 (Magma Cream) (十字形)          |
| **牛奶浴核心**<br>(Milk Bath Core)   | <img src="https://s2.loli.net/2025/12/13/cOSirPfRwnpsEVm.png" width="150" alt="Milk Bath Core Recipe" />   | **中心：** 潮涌核心 (Conduit)<br>**上下左右：** 奶桶 (Milk Bucket) (十字形)            |
| **牡丹浴核心**<br>(Peony Bath Core)  | <img src="https://s2.loli.net/2025/12/13/cv2N4u7VF1sAozh.png" width="150" alt="Peony Bath Core Recipe" />  | **中心：** 潮涌核心 (Conduit)<br>**上下左右：** 牡丹 (Peony) (十字形)                  |
| **玫瑰浴核心**<br>(Rose Bath Core)   | <img src="https://s2.loli.net/2025/12/13/JUaCEHetx1pr4vu.png" width="150" alt="Rose Bath Core Recipe" />   | **中心：** 潮涌核心 (Conduit)<br>**上下左右：** 玫瑰丛 (Rose Bush) (十字形)            |

### 浴缸与喷头 (Bathtubs & Shower Heads)

| 物品                                                        | 配方                                                                                                          | 合成材料                                                                            |
| :---------------------------------------------------------- | :------------------------------------------------------------------------------------------------------------ | :---------------------------------------------------------------------------------- |
| **浴缸**<br>(Bathtub)<br>_(示例：丛林木)_                   | <img src="https://s2.loli.net/2025/12/13/xsmf5VUbKkEQ6w2.png" width="150" alt="Bathtub Recipe" />             | **形状：** U 型 (底部两排)<br>**材料：** 丛林木板 (或其他木材/石材) + 铁粒 (右上角) |
| **紧凑型喷头**<br>(Compact Shower Head)<br>_(示例：丛林木)_ | <img src="https://s2.loli.net/2025/12/13/MRnypK2jqhNx3PE.png" width="150" alt="Compact Shower Head Recipe" /> | **上中：** 桶<br>**中：** 木棍 (左), 丛林木板 (右)                                  |
| **雨淋式喷头**<br>(Rain Shower Head)<br>_(示例：丛林木)_    | <img src="https://s2.loli.net/2025/12/13/vZwtJ12rN3Wu8kF.png" width="150" alt="Rain Shower Head Recipe" />    | **上中：** 桶<br>**中：** 木棍 (左), 丛林木板 (右)<br>**下左：** 木棍               |

### 其他 (Others)

| 物品                        | 配方                                                                                                  | 合成材料                                                          |
| :-------------------------- | :---------------------------------------------------------------------------------------------------- | :---------------------------------------------------------------- |
| **橡皮鸭**<br>(Rubber Duck) | <img src="https://s2.loli.net/2025/12/13/qdRvPFoOJzL15pj.png" width="150" alt="Rubber Duck Recipe" /> | **上/下排：** 黄色染料<br>**中排：** 红色染料 + 粘液球 + 黄色染料 |

---

_Enjoy your shower! 洗个痛快澡！_
