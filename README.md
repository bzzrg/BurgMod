# Burg Mod

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
  Uses icons instead of full words for the label. For example, `"Waiting..." -> "..."`

### Showcases

[![Showcase 1](https://img.youtube.com/vi/Nl3VHL3SM0U/0.jpg)](https://www.youtube.com/watch?v=Nl3VHL3SM0U)

[▶ Watch Showcase 1](https://www.youtube.com/watch?v=Nl3VHL3SM0U)

[![Showcase 2](https://img.youtube.com/vi/McFBeERzFMo/0.jpg)](https://www.youtube.com/watch?v=McFBeERzFMo)

[▶ Watch Showcase 2](https://www.youtube.com/watch?v=McFBeERzFMo)
---

## Perfect 45 Offset

### General
- **What Does It Do?**  
  Perfect 45 Offset calculates the best landing offset you can still achieve for a 45 jump from your current position if the rest of your timings and angles are absolutely perfect.  
  For example, let's consider a quad 45. The label starts by displaying the best possible offset you can get. But then, after let's say 2 45 strafes, the displayed offset would show the best offset you can still get if you are perfect from that point on, which would naturally be lower than the starting offset because of human error in the first 2 45 strafes.  
  If the calculated offset becomes negative, landing the jump is no longer possible.  
  If it is calculated that you will overshoot the momentum from your current position with perfect timings/angles, the label will indicate this.

### Required Config
- **# of 45s**  
  Number of 45 strafes in your jump. Must be less than or equal to the number of jumps in your strategy.

- **Jump Angle**  
  The angle you jump at for your 45 strafe jumps (NOT your normal momentum jumps).

- **Apply JA To First**  
  Toggles whether your jump angle set in config is applied to the jump tick of your first 45 strafe when simulating offset.

- **45 Key**  
  Which strafe key (A or D) you use for 45s. Required for correct offset calculation.

### Label Customization
- **Show Auto Offset**  
  Displays a single offset that automatically chooses X or Z based on your jump angle in config. If it chooses the wrong axis, use the X or Z labels instead.

- **Show X Offset**  
  Always shows the offset along the X axis.

- **Show Z Offset**  
  Always shows the offset along the Z axis.

- **Shorten Label**  
  Shortens `"Perfect 45 Offset"` to `"P45 Offset"`.

- **E Notation**  
  Displays offsets in scientific notation (example: `-0.0000546 → -5.46e-5`).

- **E Notation Max Exp**  
  Sets the greatest (least negative) exponent allowed for scientific notation. For example, if this is set to -5, -1.23e-7 is allowed but -1.23e-3 is not.

- **E Notation Precision**  
  Sets how many decimal places are shown in the scientific notation value.

### Other Config
- **Stop On Input Fail**  
  Stops tracking offset and shows fail message on label when you fail your strategy's inputs. Resets after teleporting back to checkpoint.

- **Show Overshoot Amount**  
  Changes the label from just saying `"Overshoot"` to saying `"OS"` followed by how much you would overshoot by.

- **Show LB**  
  Highlights the predicted landing block (lime green).

- **Show Jump Block**  
  Highlights the block you jump from before landing (light blue).

- **Show JB/LB Line**  
  Shows the path that was used for finding your jump block (block you do your last 45 strafe off of) and landing block (first block you collide with horizontally after 45 strafes).  
  This path represents your movement if you performed your strategy without 45 strafing.  
  The path only shows if either the jump block or landing block couldn't be found so you can determine why.

- **Show Perfect Line**  
  Shows the predicted path of your movement if you were to perform your strategy with perfect 45 strafes.

### Buttons
- **Fix Strat 45s**  
  This button fixes the 45 jumps of your strategy if they are wrong.

### Error Label Meanings
- **What Does Invalid JA Mean?**  
  The jump angle you have set in config is invalid.

- **What Does Invalid Strategy Mean?**  
  Your strategy is invalid due to one of the following:
  - # of 45s is greater than number of jumps  
  - Last tick is not AIR  
  - 45 jumps are inputted wrong  

- **What Does Can't Find LB Mean?**  
  No landing block was detected during simulation.

- **What Does Can't Find JB (T#) Mean?**  
  No jump block was detected on the required tick.

### Extra
- **Important Things To Know**
  - Strategy is required  
  - # of 45s must be ≤ jumps  
  - Last tick must be AIR  
  - Strategy must include strafing  
  - Trim Strategy works  
  - Landing must be on top of a block  

### Showcases

[![Showcase 1](https://img.youtube.com/vi/ogHvuFBKtuM/0.jpg)](https://www.youtube.com/watch?v=ogHvuFBKtuM)
**▶ Watch Showcase 1**

[![Showcase 2](https://img.youtube.com/vi/tBZgtMyY9Es/0.jpg)](https://www.youtube.com/watch?v=tBZgtMyY9Es)
**▶ Watch Showcase 2**

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
**▶ Watch Showcase**

---

## Position Checker

### Usage (`/bm pos`)
- **pos toggle**  
  Toggles all pos checkers on/off without deleting them

- **pos add `<x/z/both> <airtime>`**  
  Adds new pos checker for either X or Z axis for a specific airtick

- **pos remove `<checker_num>`**  
  Removes an added pos checker, use `/bm pos list` for checker numbers

- **pos clear**  
  Removes all pos checkers

- **pos list**  
  Lists all added pos checkers and their number

- **pos limit**  
  Adds limits so that pos checkers for a jump will only send if first air tick was within limits

### Usage (`/bm pos limit`)
- **pos limit xmin `<coordinate>`**  
  Sets minimum player X coordinate required

- **pos limit xmax `<coordinate>`**  
  Sets maximum player X coordinate required

- **pos limit zmin `<coordinate>`**  
  Sets minimum player Z coordinate required

- **pos limit zmax `<coordinate>`**  
  Sets maximum player Z coordinate required

- **pos limit frompos `<x> <z> <block_range>`**  
  Set coordinate limits from a position

- **pos limit fromcurrent `<block_range>`**  
  Uses your current position

- **pos limit fromstrat `<block_range>`**  
  Uses the final jump position from your strategy

### Showcase

[![Showcase](https://img.youtube.com/vi/WVmCx3AI-g4/0.jpg)](https://www.youtube.com/watch?v=WVmCx3AI-g4)
**▶ Watch Showcase**

---

## License

Copyright (c) 2026 Burg  

This mod is licensed under the GNU Lesser General Public License v2.1 or later.
