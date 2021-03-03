package com.example.examplemod;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * 独自レシピシステムの中身。
 * <p>
 * 条件など
 */
public class PotionCraft extends SpecialRecipe {

    public PotionCraft(ResourceLocation idIn) {
        super(idIn);
    }

    /**
     * レシピがあっているか（完成品を表示させるか）を判断する
     */
    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        boolean hasPotion = false;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack itemStack = inv.getStackInSlot(i);
            // ポーション（と空気）以外はレシピを無効にしたい
            if (itemStack.getItem() == Items.POTION || itemStack.getItem() == Items.AIR) {
                hasPotion = true;
            } else {
                // 一つでもポーション以外が入ってきた場合は即刻return
                return false;
            }
        }
        return hasPotion;
    }

    /**
     * 完成品を返す
     * <p>
     * ポーション関係はユーティリティクラスがあるのでそれに乗っかればおｋ
     */
    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {

        // ポーション効果の配列。作業台に乗ってるポーションの効果をすべてここに入れる
        ArrayList<EffectInstance> effectArrayList = new ArrayList<>();

        // NBTタグをくっつけていく
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack itemStack = inv.getStackInSlot(i);
            // ポーション効果を配列で受け取る
            List<EffectInstance> effectList = PotionUtils.getEffectsFromStack(itemStack);
            // 追加
            effectArrayList.addAll(effectList);
        }

        // 完成品
        ItemStack potion = new ItemStack(Items.POTION);
        PotionUtils.appendEffects(potion, effectArrayList);

        // なんか二個目が生成されるので、作業台からっぽなら空を返す
        return inv.isEmpty() ? ItemStack.EMPTY : potion;
    }

    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RegisterRecipe.POTION_CRAFT.get();
    }

}
