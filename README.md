# ForgeSpecialRecipeExample

![Imgur](https://imgur.com/l56XvMs.png)

JSONによるレシピ記述では、NBTタグを扱うことができないため、SpecialRecipeクラスを継承する必要があります。

## 作り方

1.16.5です。

### 1.SpecialRecipeを継承したクラスを作成する

以下は実装例です

```java 
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
```

### 2.Forgeに登録する

SpecialRecipe登録用クラスを作成してこんな風に

```java
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
```

あとは`@Mod`アノテーションのついたクラスのコンストラクタで呼べばおk

```java
@Mod(MOD_ID)
public class ExampleMod {

    public static final String MOD_ID = "examplemod";

    public ExampleMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // レシピシステムを登録
        RegisterRecipe.register(modEventBus);
    }
}
```

### 3.JSONファイルを書く
Forgeに登録しただけだと動かない（まじ？）ので、JSONファイルを一つ作成します

ぱすは

```java
resources.data.examplemod.recipes
```

の中に`potion_craft.json`みたいな感じでファイルを作成します。`potion_craft`の部分は、`RECIPE.register()`の第一引数と同じのを入れておけばいいと思う。

そして中身はこうです。`MODのID:JSONファイル名`です。

```json
{
  "type": "examplemod:potion_craft"
}
```