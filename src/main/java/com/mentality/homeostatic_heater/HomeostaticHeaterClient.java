package com.mentality.homeostatic_heater;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;

/** A small tooltip next to the crosshair while sneaking and looking at a heater. */
public final class HomeostaticHeaterClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(HomeostaticHeaterClient::drawHeaterStatus);
    }

    private static void drawHeaterStatus(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null || client.currentScreen != null
                || client.options.hudHidden || !client.options.sneakKey.isPressed()
                || !(client.crosshairTarget instanceof BlockHitResult hit)) return;

        BlockEntity blockEntity = client.world.getBlockEntity(hit.getBlockPos());
        if (!(blockEntity instanceof CoalHeaterBlockEntity heater)
                || !(client.world.getBlockState(hit.getBlockPos()).getBlock() instanceof CoalHeaterBlock)) return;

        Text title = Text.translatable("block.homeostatic_heater.coal_heater");
        Text detail;
        if (heater.isAlwaysActiveForDisplay()) {
            detail = Text.translatable("hud.homeostatic_heater.always_active");
        } else if (!heater.isActiveForDisplay()) {
            detail = Text.translatable("hud.homeostatic_heater.inactive");
        } else {
            FuelDuration time = heater.getRemainingForDisplay();
            detail = Text.translatable("hud.homeostatic_heater.remaining", time.minutes(), time.paddedSeconds());
        }

        int width = Math.max(client.textRenderer.getWidth(title), client.textRenderer.getWidth(detail)) + 12;
        int height = 28;
        int x = context.getScaledWindowWidth() / 2 + 12;
        int y = context.getScaledWindowHeight() / 2 + 10;
        if (x + width > context.getScaledWindowWidth() - 4) x -= width + 24;
        if (y + height > context.getScaledWindowHeight() - 4) y -= height + 20;

        context.fill(x, y, x + width, y + height, 0xD0101010);
        context.fill(x, y, x + width, y + 1, 0xFF8A552D);
        context.fill(x, y + height - 1, x + width, y + height, 0xFF8A552D);
        context.drawTextWithShadow(client.textRenderer, title, x + 6, y + 4, 0xFFCC8844);
        context.drawTextWithShadow(client.textRenderer, detail, x + 6, y + 15, 0xFFE8E1D5);
    }
}
