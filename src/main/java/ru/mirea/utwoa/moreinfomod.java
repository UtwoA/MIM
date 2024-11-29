package ru.mirea.utwoa;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;

import java.awt.*;

@Mod("moreinfomod")
public class moreinfomod {
    public static final String MOD_ID = "moreinfomod";

    public moreinfomod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        // Для инструментов и брони.
        if (stack.getItem() instanceof TieredItem || stack.getItem() instanceof ArmorItem) {
            int maxDurability = stack.getMaxDamage();
            int currentDurability = maxDurability - stack.getDamageValue();
            double percentage = ((double) currentDurability / maxDurability) * 100.0;

            // Вычисление цвета: от красного к зеленому.
            int color = durabilityToColor(percentage);

            // Отображение информации в тултипе.
            event.getToolTip().add(
                    Component.literal(String.format("Durability: %d/%d (%.1f%%)", currentDurability, maxDurability, percentage))
                            .withStyle(style -> style.withColor(color))
            );
        }
    }

    @SubscribeEvent
    public void onRenderHotbarInfo(RenderGuiOverlayEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player != null) {
            ItemStack stack = player.getMainHandItem();

            if (!stack.isEmpty() && stack.isDamageableItem()) {  // Проверяем, может ли предмет ломаться
                // Подготовка информации о прочности.
                int maxDurability = stack.getMaxDamage();
                int currentDurability = maxDurability - stack.getDamageValue();
                double percentage = ((double) currentDurability / maxDurability) * 100.0;
                int color = durabilityToColor(percentage);

                // Позиционируем текст в крайнем левом углу.
                int x = 10;  // Отступ от левого края экрана
                int screenHeight = minecraft.getWindow().getGuiScaledHeight();
                int y = screenHeight * 11 / 10;  // Отступ снизу экрана, рядом с HotBar.

                // Масштабируем текст с помощью PoseStack.
                PoseStack poseStack = event.getGuiGraphics().pose();
                poseStack.pushPose();
                poseStack.scale(0.8F, 0.8F, 0.8F);  // Уменьшаем размер текста

                // Рендерим текст с информацией о прочности.
                String durabilityText = String.format("%d/%d (%.1f%%)", currentDurability, maxDurability, percentage);
                event.getGuiGraphics().drawString(minecraft.font, durabilityText, x, y, color);

                // Возвращаем стек преобразований в исходное положение.
                poseStack.popPose();
            }
        }
    }


    private int durabilityToColor(double percentage) {
        // Преобразование процента прочности в цвет (от красного к зеленому).
        float red = (float) Math.max(0, (100 - percentage) / 100);
        float green = (float) Math.max(0, percentage / 100);
        return new Color(red, green, 0).getRGB();
    }
}
