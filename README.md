# BurgMod

Advanced parkour features for precise movement, strategy validation, and jump analysis.

---

## Input Status

### General
- **What Does It Do?**  
  Input Status uses your strategy from the Strategy Editor and checks every tick whether your inputs match those defined in the strategy. The comparison begins once you start moving after a reset. If a mismatch occurs at any tick, the label immediately notifies you that the inputs failed. Input Status requires a strategy to be set in the Strategy Editor.

### Label Customization
- **Show Fail Tick**  
  Shows the tick you failed the strategy on. Corresponds to tick numbers inside the strategy editor GUI.

- **Show Fail Reason**  
  Shows the inputs that were correct, missing, or extra on the fail tick.
  - Green Input: Input was correct
  - Dashed Input: Input was missing (not pressed but expected from that tick)
  - Bolded Input: Input was extra (pressed but not expected from that tick)

- **Shorten Label**  
  Uses icons instead of full words for the label. For example, "Waiting..." -> "..."

### Showcases

[![Showcase 1](https://img.youtube.com/vi/Nl3VHL3SM0U/0.jpg)](https://www.youtube.com/watch?v=Nl3VHL3SM0U)

[▶ Watch Showcase 1](https://www.youtube.com/watch?v=Nl3VHL3SM0U)

[![Showcase 2](https://img.youtube.com/vi/McFBeERzFMo/0.jpg)](https://www.youtube.com/watch?v=McFBeERzFMo)

[▶ Watch Showcase 2](https://www.youtube.com/watch?v=McFBeERzFMo)

---

## Perfect 45 Offset

### General
- **What Does It Do?**  
  Perfect 45 Offset calculates the best landing offset you can still achieve for a 45 jump from your current position if the rest of your timings and angles are absolutely perfect. For example, let's consider a quad 45. The label starts by displaying the best possible offset you can get. But then, after let's say 2 45 strafes, the displayed offset would show the best offset you can still get if you are perfect from that point on, which would naturally be lower than the starting offset because of human error in the first 2 45 strafes. If the calculated offset becomes negative, landing the jump is no longer possible. If it is calculated that you will overshoot the momentum from your current position with perfect timings/angles, the label will indicate this.

### Required Config
- **\# of 45s**  
  Number of 45 strafes in your jump. Must be less than or equal to the number of jumps in your strategy.

- **Jump Angle**  
  The angle you jump at for your 45 strafe jumps (NOT your normal momentum jumps).

- **Apply JA To First**  
  Toggles whether your jump angle set in config is applied to the jump tick of your first 45 strafe when simulating offset.

- **45 Key**  
  Which strafe key (A or D) you use for 45s. Required for correct offset calculation.

- **Fix Strat 45s**  
  This button fixes the 45 jumps of your strategy if they are wrong (You are notified if they are wrong on config GUI close). Note that this fix is based on your # of 45s, 45 key, and the position of the jump ticks in your strategy. If any of those 3 are wrong for your strategy, this button will not work, so fix those before you use this.

### Label Customization
- **Show Auto Offset**  
  Displays a single offset that automatically chooses X or Z based on your jump angle in config. If it chooses the wrong axis, use the X or Z labels instead.

- **Show X Offset**  
  Always shows the offset along the X axis.

- **Show Z Offset**  
  Always shows the offset along the Z axis.

- **Shorten Label**  
  Shortens "Perfect 45 Offset" to "P45 Offset".

- **E Notation**  
  Displays offsets in scientific notation (example: -0.0000546 → -5.46e-5).

- **E Notation Max Exp**  
  Sets the greatest (least negative) exponent allowed for scientific notation. For example, if this is set to -5, -1.23e-7 is allowed but -1.23e-3 is not.

- **E Notation Precision**  
  Sets how many decimal places are shown in the scientific notation value.

### Other Config
- **Stop On Input Fail**  
  Stops tracking offset and shows fail message on label when you fail your strategy's inputs. Resets after teleporting back to checkpoint.

- **Show Overshoot Amount**  
  Changes the label from just saying "Overshoot" to saying "OS" followed by how much you would overshoot by.

- **Show LB**  
  Highlights the predicted landing block (lime green).

- **Show Jump Block**  
  Highlights the block you jump from before landing (light blue).

- **Show JB/LB Line**  
  Shows the path that was used for finding your jump block (block you do your last 45 strafe off of) and landing block (first block you collide with horizontally after 45 strafes). This path represents your movement if you performed your strategy without 45 strafing. The path only shows if either the jump block or landing block couldn't be found (if your label is showing "Can't Find LB/JB") so you can determine why.

- **Show Perfect Line**  
  Shows the predicted path of your movement if you were to perform your strategy with perfect 45 strafes (displays on reset and lasts 5s).

### Error Label Meanings
- **What Does Invalid JA Mean?**  
  The jump angle you have set in config is invalid (must be any valid number, decimals are allowed)

- **What Does Invalid Strategy Mean?**  
  Your strategy is invalid due to one of the following 2:
  - The # of 45s you have set is greater than the number of jumps in your strategy
  - The 45 jumps from your strategy are inputted wrong (use Fix Strat 45s button)

- **What Does Can't Find LB Mean?**  
  Your strategy was simulated without 45 strafing, but no landing block was detected. No horizontal collision occurred within 100 ticks after the last jump. Only blocks landed on top of count (example: top of a ladder counts; ladder side, water, lava do not).

- **What Does Can't Find JB (T#) Mean?**  
  Your strategy was simulated without 45 strafing, but no jump block was detected on the tick # displayed (final landing tick before final jump) because you were not on the ground during that tick.

### Extra
- Strategy is required for offset calculations.
- Your # of 45s must be equal to or less than the number of jumps in your strategy.
- Your strategy must include the strafing and the A/D tapping you do while 45ing. If you use Fix Strat 45s, the strafing is included automatically.
- Using Trim Strategy works for Perfect 45 Offset. The mod just extends the strategy internally using the last tick's inputs.
- The landing block of your jump must be a block that you actually land directly on top of for Perfect 45 Offset to work (example: top of a ladder counts; ladder side, water, lava do not).

### Showcases

[![Showcase 1](https://img.youtube.com/vi/ogHvuFBKtuM/0.jpg)](https://www.youtube.com/watch?v=ogHvuFBKtuM)

[▶ Watch Showcase 1](https://www.youtube.com/watch?v=ogHvuFBKtuM)

[![Showcase 2](https://img.youtube.com/vi/tBZgtMyY9Es/0.jpg)](https://www.youtube.com/watch?v=tBZgtMyY9Es)

[▶ Watch Showcase 2](https://www.youtube.com/watch?v=tBZgtMyY9Es)

---

## Trajectory

### General
- **What Does It Do?**  
  Trajectory uses your current rotation and inputs to display a line of trajectory you will follow considering your rotation and inputs stay the same.

- **How Is This Useful?**  
  This can be used for 45 strafe jumps or fast turn jumps to see if you will land earlier. It's also useful if you just like to see your player's trajectory at all times.

### Line Customization
- **Tick Length**  
  The amount of ticks that the trajectory line should predict into the future.

### Showcase

[![Showcase](https://img.youtube.com/vi/WHZO3lhQV0o/0.jpg)](https://www.youtube.com/watch?v=WHZO3lhQV0o)

[▶ Watch Showcase](https://www.youtube.com/watch?v=WHZO3lhQV0o)

---

## Position Checkers

### General
- **What Does It Do?**  
  Position Checkers send your X or Z coordinate on a certain air tick.

### Checkers
- **Add Checker**  
  Adds a new position checker for X/Z/BOTH and a specific amount of airtime.

- **Clear Checkers**  
  Clears all position checkers.

### Coordinate Limits
- **Min X**  
  Sets minumum player X coordinate required for position checkers to work.

- **Max X**  
  Sets maximum player X coordinate allowed for position checkers to work.

- **Min Z**  
  Sets minumum player Z coordinate required for position checkers to work.

- **Max Z**  
  Sets maximum player Z coordinate allowed for position checkers to work.

- **Min/Max From Pos**  
  Sets min/max X/Z values so that only jumps from a certain block range around your current position can set off position checkers.

- **Min/Max From Strat**  
  Same as Min/Max From Pos, but uses the position of your final jump tick from your strategy. IMPORTANT: Only works if momentum is noturn and button is clicked at reset location.

### Showcase

[![Showcase](https://img.youtube.com/vi/WVmCx3AI-g4/0.jpg)](https://www.youtube.com/watch?v=WVmCx3AI-g4)

[▶ Watch Showcase](https://www.youtube.com/watch?v=WVmCx3AI-g4)

---

## Turn Helper

### General
- **What Does It Do?**  
  Turn Helper moves an in-world customizable target across yaw points set in config to help with turns.

### Config
- **Mode**
  - One Moving Target: Slides a target across the yaw points' yaw values at their respective tick #
  - All Targets On: Draws the yaws of all the yaw points constantly (tick # for the yaw points is disregarded when using this mode, so if you are using this mode don't worry about tick #)

- **Delta Yaws**  
  Treats yaws from yaw points as changes in yaw like CYV Turning HUD instead of actual yaw values from F3.

- **Show Turn Accuracy**  
  Shows a label that displays how well you followed your yaw points (in %). Each tick it checks how many degrees your yaw was off by and calculates percentage linearly from that. If your yaw was 45 or more degrees off, it is treated as 0% for that tick. Turn accuracy is unaffected by Yaw +-. Yaw +- is just for thickness of the target. Note that this percentage doesn't reflect how close you are to making the jump, because some ticks in parkour are way more important to be accurate for making distance (like jump tick/jump angle).

- **Yaw Point Config Buttons**
  - Tick #: Tick #1 is the first tick that your position has changed from your reset position. To help understand, the Tick # you input here matches the tick # that this tick would be if you were to input your strategy (you don't actually need strategy for Turn Helper at all, just used this analogy so you can understand). Minimum is Tick #2 because you never rotate on Tick #1 in parkour.
  - Yaw: The yaw you should be looking at for this tick. If Delta Yaws is on, this is treated as the change in yaw you should have for this tick instead.

### Target Customization

- **Thickness (Dot)**  
  Controls the thickness of the dot. This setting is only for if you are using the dot shape.
  
- **Yaw +- (Line)**  
  Controls the thickness of the line in degrees. This setting is only for if you are using the line shape. The side edges of the line will be at the yaw of whatever yaw point the line is at, +/- this value.

### Showcases

[![Showcase 1](https://img.youtube.com/vi/Wqt8_NIQyFM/0.jpg)](https://www.youtube.com/watch?v=Wqt8_NIQyFM)

[▶ Watch Showcase 1](https://www.youtube.com/watch?v=Wqt8_NIQyFM)

[![Showcase 2](https://img.youtube.com/vi/80jl_UemU5M/0.jpg)](https://www.youtube.com/watch?v=80jl_UemU5M)

[▶ Watch Showcase 2](https://www.youtube.com/watch?v=80jl_UemU5M)

---

## General Config

---

## Edit Strategy

### General
- **What Is It For?**  
  The strategy editor is used to define the inputs of a strategy for a jump tick by tick. Features like Input Status and Perfect 45 Offset require you to have your strategy set for the jump you are currently doing.

### Strategy Editing
- **Add Tick**  
  Adds an individual tick to the strategy.

- **Add Jump**  
  Allows adding preset sequences of ticks that are common based (like Jam or HH) instead of needing to do it manually through adding individual ticks.

- **Clear Strat**  
  Clears your current strategy fully.

- **Trim Strat**  
  Deletes all duplicate ticks at the end of your strategy except for one of them. This is because more than one of the same tick at the end of your strategy is redundant for all strategy related features.

- **Mirror Strat**  
  Switches all ticks/jumps with A selected to have D selected instead and vice versa. If the tick/jump has both A and D selected or has neither selected, it is unaffected.

- **Record Strat**  
  Records the player's movement so that the user can perform the strategy in game instead of setting ticks manually. To record properly, know that recording starts when you start moving after you have reset. Note that all recorded ticks are cleared when you reset, that way you don't have to re-record every time you fail your inputs while trying to record the strategy. All empty ticks (ticks with no WASD, SPR, SNK, or JMP) are cut off when you stop recording.

- **Preview Strat**  
  Draws a line that shows the trajectory your player would follow from your current position if your strategy was performed perfectly tick by tick.

- **Jump Config Buttons**
  - Extend Button: Shows the ticks that actually make up the jump. These ticks can be modified manually, but changing the config of the jump using its config buttons will override any changes you have made to the jump's ticks manually.
  - W/A/S/D: Defines which movement keys are used for the jump.
  - A/D: Defines the strafe key that is used for the jump.
  - Run 1t: Adds one tick of running after the jump using whatever config is already set for the jump. For example, Run 1t would add one tick of W+A+SPR if the jump was a Jam with W+A+SPR.
  - 1-11t Slider: For example, if set to 2t, then for HH it would be a 2t HH, for Mark it would be a 2t Mark, etc.

### Strategy Saving
- **Save Strat**  
  Saves your current strategy under the provided key.

- **Delete Strat**  
  Deletes the strategy saved under the provided key if it exists.

- **Load Strat**  
  Loads the strategy saved under the provided key if it exists.

- **Save HPK Strat**  
  Saves your current strategy under the provided HPK OJ Jump # as an HPK strategy.

- **Delete HPK Strat**  
  Deletes the HPK strategy saved under the provided HPK OJ Jump # if it exists.

- **Auto Load HPK**  
  Enables auto-loading for HPK strategies when joining the jump # it is saved under. This uses the "[OJ] Entering Jump..." message when joining a jump so these messages must be visible. This only works for HPK Network.

- **List Strats**  
  Lists all saved strategies and saved HPK strategies in the chat.

### Extra
- Having SPR selected for a tick means you are sprinting during that tick, not that you are holding down the sprint key during that tick.
- Having SNK selected for a tick means you are sneaking during that tick, not that you are holding down the sneak key during that tick.
- Having JMP selected for a tick means you jump on that tick, not that you are holding down the jump key during that tick.
- Features that use strategy might break or warn you if you have a tick with both SPR and SNK selected. This is because it is impossible to be sprinting and sneaking at the same time.

---

## License

Copyright (c) 2026 Burg

This mod is licensed under the GNU Lesser General Public License v2.1 or later.