package com.example.examplemod;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * レシピシステムを追加する
 *
 * JSONファイルも書かないといけない
 *
 * <code>resources.data.examplemod.recipes</code> 参照
 */
public class RegisterRecipe {

    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ExampleMod.MOD_ID);

    /**
     * ポーション合成用クラフトシステム
     */
    public static final RegistryObject<SpecialRecipeSerializer<PotionCraft>> POTION_CRAFT = RECIPE.register("potion_craft", () -> new SpecialRecipeSerializer<>(PotionCraft::new));

    /**
     * {@link ExampleMod}で呼ぶ
     */
    public static void register(IEventBus eventBus) {
        RECIPE.register(eventBus);
    }

}
