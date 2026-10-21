# Texture generation prompts

Created with the built-in image generation tool. The front, side, and top
`*_source.png` images are generated masters; game textures are 16×16
nearest-neighbour reductions with small opaque palettes. The second lit-front
master (`coal_heater_front_lit_frame2_source.png`) changes only the ember
pattern. Its 16×16 reduction is composited over the original only where the
embers are red/orange, keeping all metal pixels identical between frames. The
two frames are stacked in `coal_heater_front_lit.png` and interpolated by its
`.png.mcmeta` file. The inventory item now uses the 3D block model; the older
`coal_heater_item_source.png` and flat item texture are unused historical art.

Alternate-frame edit prompt: "Change ONLY the orange/yellow/red embers behind
the dark vertical grille. Slightly move a few bright pixels upward and gently
change the glow shape, like a quiet steady coal fire. Preserve every grille
bar, the outer iron frame, all metal colors, orthographic composition and
scale exactly. No extra flames, sparks, smoke, text, background or new detail."

## Current simplified texture prompts

- Lit front: "Simplify the reference heavily into vanilla-style 16x16 pixel
  art. Preserve the dark iron heater identity but use broad dark gray regions,
  exactly three thick vertical grille bars, one large orange-red glowing coal
  area behind them, a simple border, and no bolts, tiny lights, horizontal
  grille details, noise, gradients, or fine shading. Opaque square tile."
- Unlit front: "Keep the same square metal frame, three broad vertical grille
  bars, proportions, metal colors, and composition. Replace only the glowing
  coal area behind bars with dark charcoal gray. No added detail."
- Side: "Plain charcoal metal panel, two simple dark horizontal vent slits,
  broad gray border, at most six gray shades. Remove rivets, grain, scratches,
  highlights, gradients, and noise."
- Top: "Plain charcoal metal plate, three simple dark vent slits across center,
  broad gray border, at most six gray shades. Remove rivets, grain, scratches,
  highlights, gradients, and noise."

All current prompts use a flat, orthographic, edge-to-edge square tile and
strict hard pixel edges. The unlit texture uses the lit texture as its edit
reference; the other textures use their earlier generated face as reference.

## Previous design prompts

## Lit front

> Use case: stylized-concept. Asset type: source artwork for a 16x16 Minecraft
> block texture, square seamless face tile. Draw a compact coal-fired villa
> heater front face, unmistakable at tiny pixel size. Dark iron casing,
> symmetrical square grille, warm amber-red coal glow at center, two subtle
> redstone-red indicator dots. Crisp flat Minecraft-compatible pixel art,
> hard pixel edges, limited palette, orthographic straight-on view, exact
> square composition. Face fills canvas edge to edge, fully opaque, no
> transparency, no lighting or shadow outside the face, no perspective, no
> border/margin, no letters, no watermark.

## Unlit front

> Use case: precise-object-edit. Use the lit front as edit target. Turn off the
> fire inside this coal heater. Keep the iron frame, grille, vents,
> proportions, camera, and pixel-art style exactly unchanged. Behind the
> grille, replace all red/orange flames with matte dark charcoal pieces. Turn
> the small red indicator lights to dim gray. Preserve an opaque, edge-to-edge
> square tile without lettering or borders beyond its casing.

## Side

> Use case: stylized-concept. Draw the side panel of a compact coal-fired
> heater as a flat orthographic, square, edge-to-edge tile. Dark iron sheet
> metal panel, two horizontal rectangular ventilation slits across upper and
> lower thirds, small corner rivets, faint gray highlights, no flames or
> lights. Crisp Minecraft-compatible pixel art, limited charcoal and gunmetal
> gray palette, fully opaque. No perspective, backdrop, text, or watermark.

## Top

> Use case: stylized-concept. Draw the top surface of a compact coal-fired
> heater as a flat orthographic square tile. Dark iron top plate with four
> rows of narrow vent slits centered in the plate, corner rivets, subtle
> metal border. Crisp Minecraft-compatible pixel art, limited charcoal and
> gunmetal gray palette, fully opaque. No perspective, background, text, or
> watermark.

## Inventory icon

> Use case: stylized-concept. Use the lit front as design reference. Draw a
> compact coal-fired heater as a small isometric 3/4 pixel-art object. Show
> front, right side vents, and top vent plate. Center the object in the square
> and let it fill most of the canvas. Crisp Minecraft-compatible pixel art,
> hard square pixels, limited charcoal metal and warm ember palette. Use a
> genuinely transparent background; no floor, outside shadow, text,
> watermark, or border.
