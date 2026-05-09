package fr.lucreeper74.createmetallurgy.data.recipes.vanilla;

import com.google.common.base.Supplier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.mixin.accessor.MappedRegistryAccessor;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.data.recipes.CMRecipeProvider;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@SuppressWarnings("unused")
public class CMStandardRecipeGen extends CMRecipeProvider {

    private Marker MATERIALS = enterFolder("materials");

    GeneratedRecipe

            RAW_WOLFRAMITE = create(CMItems.RAW_WOLFRAMITE).returns(9)
            .unlockedBy(CMBlocks.RAW_WOLFRAMITE_BLOCK::get)
            .viaShapeless(b -> b.requires(CMBlocks.RAW_WOLFRAMITE_BLOCK.get())),

    RAW_WOLFRAMITE_BLOCK = create(CMBlocks.RAW_WOLFRAMITE_BLOCK).unlockedBy(CMItems.RAW_WOLFRAMITE::get)
            .viaShaped(b -> b.define('C', CMItems.RAW_WOLFRAMITE.get())
                    .pattern("CCC")
                    .pattern("CCC")
                    .pattern("CCC")),

    TUNGSTEN_NUGGET = create(CMItems.TUNGSTEN_NUGGET).returns(9)
            .unlockedBy(CMItems.TUNGSTEN_INGOT::get)
            .viaShapeless(b -> b.requires(CMItems.TUNGSTEN_INGOT.get())),

    TUNGSTEN_INGOT = create(CMItems.TUNGSTEN_INGOT).unlockedBy(CMItems.TUNGSTEN_NUGGET::get)
            .viaShaped(b -> b.define('C', CMItems.TUNGSTEN_NUGGET.get())
                    .pattern("CCC")
                    .pattern("CCC")
                    .pattern("CCC")),

    TUNGSTEN_INGOT_FROM_BLOCK = create(CMItems.TUNGSTEN_INGOT).withSuffix("_from_block")
            .returns(9)
            .unlockedBy(CMItems.TUNGSTEN_INGOT::get)
            .viaShapeless(b -> b.requires(CMBlocks.TUNGSTEN_BLOCK.get())),

    TUNGSTEN_BLOCK = create(CMBlocks.TUNGSTEN_BLOCK).unlockedBy(CMItems.TUNGSTEN_INGOT::get)
            .viaShaped(b -> b.define('C', CMItems.TUNGSTEN_INGOT.get())
                    .pattern("CCC")
                    .pattern("CCC")
                    .pattern("CCC")),

    OBDURIUM_INGOT_FROM_BLOCK = create(CMItems.OBDURIUM_INGOT).withSuffix("_from_block")
            .returns(9)
            .unlockedBy(CMItems.OBDURIUM_INGOT::get)
            .viaShapeless(b -> b.requires(CMBlocks.OBDURIUM_BLOCK.get())),

    OBDURIUM_BLOCK = create(CMBlocks.OBDURIUM_BLOCK).unlockedBy(CMItems.OBDURIUM_INGOT::get)
            .viaShaped(b -> b.define('C', CMItems.OBDURIUM_INGOT.get())
                    .pattern("CCC")
                    .pattern("CCC")
                    .pattern("CCC")),

    STEEL_INGOT_FROM_BLOCK = create(CMItems.STEEL_INGOT).withSuffix("_from_block")
            .returns(9)
            .unlockedBy(CMItems.STEEL_INGOT::get)
            .viaShapeless(b -> b.requires(CMBlocks.STEEL_BLOCK.get())),

    STEEL_BLOCK = create(CMBlocks.STEEL_BLOCK).unlockedBy(CMItems.STEEL_INGOT::get)
            .viaShaped(b -> b.define('C', CMItems.STEEL_INGOT.get())
                    .pattern("CCC")
                    .pattern("CCC")
                    .pattern("CCC")),

    SLAG_FROM_BLOCK = create(CMItems.SLAG).withSuffix("_from_block")
            .returns(9)
            .unlockedBy(CMItems.SLAG::get)
            .viaShapeless(b -> b.requires(CMBlocks.SLAG_BLOCK.get())),

    SLAG_BLOCK = create(CMBlocks.SLAG_BLOCK).unlockedBy(CMItems.SLAG::get)
            .viaShaped(b -> b.define('S', CMItems.SLAG.get())
                    .pattern("SSS")
                    .pattern("SSS")
                    .pattern("SSS")),

    COKE_FROM_BLOCK = create(CMItems.COKE).withSuffix("_from_block")
            .returns(9)
            .unlockedBy(CMItems.COKE::get)
            .viaShapeless(b -> b.requires(CMBlocks.COKE_BLOCK.get())),

    COKE_BLOCK = create(CMBlocks.COKE_BLOCK).unlockedBy(CMItems.COKE::get)
            .viaShaped(b -> b.define('C', CMItems.COKE.get())
                    .pattern("CCC")
                    .pattern("CCC")
                    .pattern("CCC")),

    GRAPHITE = create(CMItems.GRAPHITE).unlockedByTag(() -> ItemTags.COALS)
            .viaShapeless(b -> b.requires(Items.CLAY_BALL)
                    .requires(Ingredient.of(ItemTags.COALS), 8)),

    SANDPAPER_BELT = create(CMItems.SANDPAPER_BELT).unlockedByTag(T::sandpaper)
            .viaShaped(b -> b.define('D', T.sandpaper())
                    .pattern("DDD")
                    .pattern("DDD")),

    TUNGSTEN_WIRE = create(CMItems.TUNGSTEN_WIRE).returns(2).unlockedByTag(T::tungstenIngot)
            .viaShapeless(b -> b.requires(T.tungstenIngot())
                    .requires(Items.SHEARS)),

    TUNGSTEN_WIRE_SPOOL = create(CMItems.TUNGSTEN_WIRE_SPOOL).unlockedByTag(T::tungstenWire)
            .viaShaped(b -> b.define('W', T.tungstenWire())
                    .define('S', Items.STICK)
                    .pattern(" W ")
                    .pattern("WSW")
                    .pattern(" W ")),

    COKE = create(CMItems.COKE::get).withSuffix("_from_coal")
            .viaCooking(T::coal)
            .rewardXP(.5f)
            .forDuration(200)
            .inBlastFurnace(),

    REFRACTORY_MORTAR_BLOCK = create(CMBlocks.REFRACTORY_MORTAR).unlockedBy(T::refractoryMortar)
            .viaShaped(b -> b.define('M', T.refractoryMortarBall())
                    .pattern("MM")
                    .pattern("MM")),

    REFRACTORY_MORTAR_BALL = create(CMItems.REFRACTORY_MORTAR_BALL).withSuffix("_from_block")
            .returns(4)
            .unlockedBy(T::refractoryMortar)
            .viaShapeless(b -> b.requires(T.refractoryMortar()));


    private Marker CONTENT = enterFolder("content");

    GeneratedRecipe

            BELT_GRINDER = create(CMBlocks.BELT_GRINDER_BLOCK).unlockedBy(T::sandpaperBelt)
            .viaShaped(b -> b.define('B', T.sandpaperBelt())
                    .define('C', T.andesiteCasing())
                    .define('I', T.shaft())
                    .pattern("B")
                    .pattern("C")
                    .pattern("I")),

    CASTING_TABLE = create(CMBlocks.CASTING_TABLE_BLOCK).unlockedBy(T::andesiteAlloy)
            .viaShaped(b -> b.define('A', T.andesiteAlloy())
                    .pattern("AAA")
                    .pattern("A A")
                    .pattern("A A")),

    CASTING_BASIN = create(CMBlocks.CASTING_BASIN_BLOCK).unlockedBy(T::andesiteAlloy)
            .viaShaped(b -> b.define('A', T.andesiteAlloy())
                    .pattern("A A")
                    .pattern("A A")
                    .pattern(" A ")),

    FOUNDRY_LID = create(CMBlocks.FOUNDRY_LID_BLOCK).unlockedBy(T::andesiteAlloy)
            .viaShaped(b -> b.define('A', T.andesiteAlloy())
                    .pattern("AAA")
                    .pattern("A A")),

    FOUNDRY_BASIN = create(CMBlocks.FOUNDRY_BASIN_BLOCK).unlockedBy(T::refractoryMortar)
            .viaShaped(b -> b.define('A', T.andesiteAlloy())
                    .define('P', T.refractoryMortar())
                    .pattern("A A")
                    .pattern("APA")
                    .pattern("AAA")),

    FOUNDRY_MIXER = create(CMBlocks.FOUNDRY_MIXER_BLOCK).unlockedBy(T::refractoryMortar)
            .viaShaped(b -> b.define('A', T.cog())
                    .define('B', T.copperCasing())
                    .define('C', CMItems.STURDY_WHISK.get())
                    .pattern("A")
                    .pattern("B")
                    .pattern("C")),

    FOUNDRY_UNIT = create(CMItems.GAUGE_ATTACHMENT).unlockedByTag(T::steelIngot)
            .viaShaped(b -> b.define('S', T.steelIngot())
                    .define('C', Items.COMPASS)
                    .pattern("SCS")),

    FAUCET = create(CMBlocks.FAUCET_BLOCK).unlockedBy(T::andesiteAlloy)
            .viaShaped(b -> b.define('A', T.andesiteAlloy())
                    .pattern("A A")
                    .pattern(" A ")),

    STURDY_WHISK = create(CMItems.STURDY_WHISK).unlockedByTag(T::tungstenSheet)
            .viaShaped(b -> b.define('A', T.andesiteAlloy())
                    .define('B', AllItems.STURDY_SHEET.get())
                    .pattern(" A ")
                    .pattern("BAB")
                    .pattern("BBB")),

    LADLE_FILTER = create(CMItems.LADLE_FILTER).unlockedByTag(T::steelIngot).returns(2)
            .viaShaped(b -> b.define('W', ItemTags.WOOL)
                    .define('S', T.steelIngot())
                    .pattern("SW")),

    LABELING_STATION = create(CMBlocks.LABELING_STATION_BLOCK).unlockedByTag(T::steelIngot)
            .viaShaped(b -> b.define('T', Items.NAME_TAG)
                    .define('S', T.steelIngot())
                    .define('R', Items.REDSTONE)
                    .pattern(" S ")
                    .pattern("STS")
                    .pattern("RSR"));

    //

    static class Marker {}

    String currentFolder = "";

    Marker enterFolder(String folder) {
        currentFolder = folder;
        return new Marker();
    }

    GeneratedRecipeBuilder create(Supplier<ItemLike> result) {
        return new GeneratedRecipeBuilder(currentFolder, result);
    }

    GeneratedRecipeBuilder create(ResourceLocation result) {
        return new GeneratedRecipeBuilder(currentFolder, result);
    }

    GeneratedRecipeBuilder create(ItemProviderEntry<? extends ItemLike, ? extends ItemLike> result) {
        return create(result::get);
    }

    GeneratedRecipe createSpecial(Function<CraftingBookCategory, Recipe<?>> builder, String recipeType,
                                  String path) {
        ResourceLocation location = CreateMetallurgy.asResource(recipeType + "/" + currentFolder + "/" + path);
        return register(consumer -> {
            SpecialRecipeBuilder b = SpecialRecipeBuilder.special(builder);
            b.save(consumer, location.toString());
        });
    }

    class GeneratedRecipeBuilder {

        private String path;
        private String suffix;
        private Supplier<? extends ItemLike> result;
        private ResourceLocation compatDatagenOutput;
        List<ICondition> recipeConditions;

        private Supplier<ItemPredicate> unlockedBy;
        private int amount;

        private GeneratedRecipeBuilder(String path) {
            this.path = path;
            this.recipeConditions = new ArrayList<>();
            this.suffix = "";
            this.amount = 1;
        }

        public GeneratedRecipeBuilder(String path, Supplier<? extends ItemLike> result) {
            this(path);
            this.result = result;
        }

        public GeneratedRecipeBuilder(String path, ResourceLocation result) {
            this(path);
            this.compatDatagenOutput = result;
        }

        GeneratedRecipeBuilder returns(int amount) {
            this.amount = amount;
            return this;
        }

        GeneratedRecipeBuilder unlockedBy(Supplier<? extends ItemLike> item) {
            this.unlockedBy = () -> ItemPredicate.Builder.item()
                    .of(item.get())
                    .build();
            return this;
        }

        GeneratedRecipeBuilder unlockedByTag(Supplier<TagKey<Item>> tag) {
            this.unlockedBy = () -> ItemPredicate.Builder.item()
                    .of(tag.get())
                    .build();
            return this;
        }

        GeneratedRecipeBuilder whenModLoaded(String modid) {
            return withCondition(new ModLoadedCondition(modid));
        }

        GeneratedRecipeBuilder whenModMissing(String modid) {
            return withCondition(new NotCondition(new ModLoadedCondition(modid)));
        }

        GeneratedRecipeBuilder withCondition(ICondition condition) {
            recipeConditions.add(condition);
            return this;
        }

        GeneratedRecipeBuilder withSuffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        GeneratedRecipe viaShaped(UnaryOperator<ShapedRecipeBuilder> builder) {
            return register(consumer -> {
                ShapedRecipeBuilder b = builder.apply(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result.get(), amount));
                if (unlockedBy != null)
                    b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
                b.save(consumer, createLocation("crafting"));
            });
        }

        GeneratedRecipe viaShapeless(UnaryOperator<ShapelessRecipeBuilder> builder) {
            return register(consumer -> {
                ShapelessRecipeBuilder b = builder.apply(ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result.get(), amount));
                if (unlockedBy != null)
                    b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
                b.save(consumer, createLocation("crafting"));
            });
        }

        GeneratedRecipe viaNetheriteSmithing(Supplier<? extends Item> base, Supplier<Ingredient> upgradeMaterial) {
            return register(consumer -> {
                SmithingTransformRecipeBuilder b =
                        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                                Ingredient.of(base.get()), upgradeMaterial.get(), RecipeCategory.COMBAT, result.get()
                                        .asItem());
                b.unlocks("has_item", inventoryTrigger(ItemPredicate.Builder.item()
                        .of(base.get())
                        .build()));
                b.save(consumer, createLocation("crafting"));
            });
        }

        private ResourceLocation createSimpleLocation(String recipeType) {
            return CreateMetallurgy.asResource(recipeType + "/" + getRegistryName().getPath() + suffix);
        }

        private ResourceLocation createLocation(String recipeType) {
            return CreateMetallurgy.asResource(recipeType + "/" + path + "/" + getRegistryName().getPath() + suffix);
        }

        private ResourceLocation getRegistryName() {
            return compatDatagenOutput == null ? RegisteredObjectsHelper.getKeyOrThrow(result.get()
                                                                                       .asItem()) : compatDatagenOutput;
        }

        GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder viaCooking(Supplier<? extends ItemLike> item) {
            return unlockedBy(item).viaCookingIngredient(() -> Ingredient.of(item.get()));
        }

        GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder viaCookingTag(Supplier<TagKey<Item>> tag) {
            return unlockedByTag(tag).viaCookingIngredient(() -> Ingredient.of(tag.get()));
        }

        GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder viaCookingIngredient(Supplier<Ingredient> ingredient) {
            return new GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder(ingredient);
        }

        class GeneratedCookingRecipeBuilder {

            private Supplier<Ingredient> ingredient;
            private float exp;
            private int cookingTime;

            private final RecipeSerializer<? extends AbstractCookingRecipe> FURNACE = RecipeSerializer.SMELTING_RECIPE,
                    SMOKER = RecipeSerializer.SMOKING_RECIPE, BLAST = RecipeSerializer.BLASTING_RECIPE,
                    CAMPFIRE = RecipeSerializer.CAMPFIRE_COOKING_RECIPE;

            GeneratedCookingRecipeBuilder(Supplier<Ingredient> ingredient) {
                this.ingredient = ingredient;
                cookingTime = 200;
                exp = 0;
            }

            GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder forDuration(int duration) {
                cookingTime = duration;
                return this;
            }

            GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder rewardXP(float xp) {
                exp = xp;
                return this;
            }

            GeneratedRecipe inFurnace() {
                return inFurnace(b -> b);
            }

            GeneratedRecipe inFurnace(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                return create(RecipeSerializer.SMELTING_RECIPE, builder, SmeltingRecipe::new, 1);
            }

            GeneratedRecipe inSmoker() {
                return inSmoker(b -> b);
            }

            GeneratedRecipe inSmoker(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                create(RecipeSerializer.SMELTING_RECIPE, builder, SmeltingRecipe::new, 1);
                create(RecipeSerializer.CAMPFIRE_COOKING_RECIPE, builder, CampfireCookingRecipe::new, 3);
                return create(RecipeSerializer.SMOKING_RECIPE, builder, SmokingRecipe::new, .5f);
            }

            GeneratedRecipe inBlastFurnace() {
                return inBlastFurnace(b -> b);
            }

            GeneratedRecipe inBlastFurnace(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                create(RecipeSerializer.SMELTING_RECIPE, builder, SmeltingRecipe::new, 1);
                return create(RecipeSerializer.BLASTING_RECIPE, builder, BlastingRecipe::new, .5f);
            }

            private <T extends AbstractCookingRecipe> GeneratedRecipe create(RecipeSerializer<T> serializer,
                                                                             UnaryOperator<SimpleCookingRecipeBuilder> builder, AbstractCookingRecipe.Factory<T> factory, float cookingTimeModifier) {
                return register(recipeOutput -> {
                    boolean isOtherMod = compatDatagenOutput != null;

                    SimpleCookingRecipeBuilder b = builder.apply(SimpleCookingRecipeBuilder.generic(ingredient.get(),
                            RecipeCategory.MISC, isOtherMod ? Items.DIRT : result.get(), exp,
                            (int) (cookingTime * cookingTimeModifier), serializer, factory));

                    if (unlockedBy != null)
                        b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));

                    RecipeOutput conditionalOutput = recipeOutput.withConditions(recipeConditions.toArray(new ICondition[0]));

                    b.save(
                            isOtherMod ? new ModdedCookingRecipeOutput(conditionalOutput, compatDatagenOutput) : conditionalOutput,
                            createSimpleLocation(RegisteredObjectsHelper.getKeyOrThrow(serializer).getPath())
                    );
                });
            }
        }
    }

    //

    @Override
    public String getName() {
        return "Create: Metallurgy's Standard Recipes";
    }

    public CMStandardRecipeGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    private static class ModdedCookingRecipeOutputShim implements Recipe<RecipeInput> {

        private static final Map<RecipeType<?>, ModdedCookingRecipeOutputShim.Serializer> serializers = new ConcurrentHashMap<>();

        private final Recipe<?> wrapped;
        private final ResourceLocation overrideID;

        private ModdedCookingRecipeOutputShim(Recipe<?> wrapped, ResourceLocation overrideID) {
            this.wrapped = wrapped;
            this.overrideID = overrideID;
        }

        @Override
        public boolean matches(RecipeInput recipeInput, Level level) {
            throw new AssertionError("Only for datagen output");
        }

        @Override
        public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
            throw new AssertionError("Only for datagen output");
        }

        @Override
        public boolean canCraftInDimensions(int pWidth, int pHeight) {
            throw new AssertionError("Only for datagen output");
        }

        @Override
        public ItemStack getResultItem(HolderLookup.Provider registries) {
            throw new AssertionError("Only for datagen output");
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return serializers.computeIfAbsent(
                    getType(),
                    t -> ModdedCookingRecipeOutputShim.Serializer.create(wrapped)
            );
        }

        @Override
        public RecipeType<?> getType() {
            return wrapped.getType();
        }

        private record Serializer(
                MapCodec<Recipe<?>> wrappedCodec) implements RecipeSerializer<ModdedCookingRecipeOutputShim> {
            private static ModdedCookingRecipeOutputShim.Serializer create(Recipe<?> wrapped) {
                RecipeSerializer<?> wrappedSerializer = wrapped.getSerializer();
                @SuppressWarnings("unchecked")
                ModdedCookingRecipeOutputShim.Serializer serializer = new ModdedCookingRecipeOutputShim.Serializer((MapCodec<Recipe<?>>) wrappedSerializer.codec());

                // Need to do some registry injection to get the Recipe/Registry#byNameCodec to encode the right type for this
                // getResourceKey and getId
                // byValue and toId
                // Holder.Reference: key
                if (BuiltInRegistries.RECIPE_SERIALIZER instanceof MappedRegistryAccessor<?> mra) {
                    @SuppressWarnings("unchecked")
                    MappedRegistryAccessor<RecipeSerializer<?>> mra$ = (MappedRegistryAccessor<RecipeSerializer<?>>) mra;

                    int wrappedId = mra$.getToId().getOrDefault(wrappedSerializer, -1);
                    ResourceKey<RecipeSerializer<?>> wrappedKey = mra$.getByValue().get(wrappedSerializer).key();

                    mra$.getToId().put(serializer, wrappedId);
                    //noinspection DataFlowIssue - it is ok to pass null as the owner, because this is only being used for serialization
                    mra$.getByValue().put(serializer, Holder.Reference.createStandAlone(null, wrappedKey));
                } else {
                    throw new AssertionError("ModdedCookingRecipeOutputShim will not be able to" +
                            " serialize without injecting into a registry. Expected" +
                            " BuiltInRegistries.RECIPE_SERIALIZER to be of class MappedRegistry, is of class " +
                            BuiltInRegistries.RECIPE_SERIALIZER.getClass()
                    );
                }
                return serializer;
            }

            @Override
            public MapCodec<ModdedCookingRecipeOutputShim> codec() {
                return RecordCodecBuilder.mapCodec(instance -> instance.group(
                        wrappedCodec.forGetter(i -> i.wrapped),
                        ModdedCookingRecipeOutputShim.FakeItemStack.CODEC.fieldOf("result").forGetter(i -> new ModdedCookingRecipeOutputShim.FakeItemStack(i.overrideID))
                ).apply(instance, (wrappedRecipe, fakeItemStack) -> {
                    throw new AssertionError("Only for datagen output");
                }));
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, ModdedCookingRecipeOutputShim> streamCodec() {
                throw new AssertionError("Only for datagen output");
            }
        }

        private record FakeItemStack(ResourceLocation id) {
            public static Codec<ModdedCookingRecipeOutputShim.FakeItemStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("id").forGetter(ModdedCookingRecipeOutputShim.FakeItemStack::id)
            ).apply(instance, ModdedCookingRecipeOutputShim.FakeItemStack::new));
        }
    }

    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    private record ModdedCookingRecipeOutput(RecipeOutput wrapped, ResourceLocation outputOverride) implements RecipeOutput {

        @Override
        public Advancement.Builder advancement() {
            return wrapped.advancement();
        }

        @Override
        public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
            wrapped.accept(id, new ModdedCookingRecipeOutputShim(recipe, outputOverride), advancement, conditions);
        }
    }
}