package fr.lucreeper74.createmetallurgy.compat.jade;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.callback.JadeRayTraceCallback;
import snownee.jade.api.callback.JadeTooltipCollectedCallback;
import snownee.jade.api.config.IWailaConfig;
import snownee.jade.api.ui.IBoxElement;
import snownee.jade.util.JsonConfig;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class TargetModifierLoader extends SimpleJsonResourceReloadListener implements JadeRayTraceCallback, JadeTooltipCollectedCallback {
    protected final ListMultimap<Object, ResourceLocation> tagsToRemove = ArrayListMultimap.create();
    protected final Map<Object, Block> replacementBlocks = Maps.newHashMap();

    public TargetModifierLoader() {
        super(JsonConfig.GSON, "jade/target_modifier");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        tagsToRemove.clear();
        replacementBlocks.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            ResourceLocation id = entry.getKey();
            try {
                JsonObject jsonObject = entry.getValue().getAsJsonObject();
                String type = GsonHelper.getAsString(jsonObject, "type");
                List<?> targets = parseTargets(GsonHelper.getAsJsonObject(jsonObject, "target"));
                if ("remove_elements".equals(type)) {
                    JsonElement tagElement = GsonHelper.getNonNull(jsonObject, "tag");
                    List<ResourceLocation> tags;
                    if (tagElement.isJsonArray()) {
                        tags = tagElement.getAsJsonArray()
                                .asList()
                                .stream()
                                .map(JsonElement::getAsString)
                                .map(ResourceLocation::parse)
                                .toList();
                    } else {
                        tags = List.of(ResourceLocation.parse(tagElement.getAsString()));
                    }
                    for (Object target : targets) {
                        tagsToRemove.putAll(target, tags);
                    }
                } else if ("replace".equals(type)) {
                    JsonObject with = GsonHelper.getAsJsonObject(jsonObject, "with");
                    Block block = parseBlocks(with).getFirst();
                    for (Object target : targets) {
                        replacementBlocks.put(target, block);
                    }
                } else {
                    throw new IllegalArgumentException("Unknown type: " + type);
                }
            } catch (Exception e) {
                if (IWailaConfig.get().getGeneral().isDebug()) {
                    CreateMetallurgy.LOGGER.error("Failed to load target modifier {}", id, e);
                }
            }
        }
    }

    private static List<?> parseTargets(JsonObject jsonObject) {
        if (jsonObject.has("block") && jsonObject.has("entity")) {
            throw new IllegalArgumentException("Cannot have both block and entity");
        }
        if (jsonObject.has("block")) {
            return parseBlocks(jsonObject);
        }
        if (jsonObject.has("entity")) {
            return parseEntities(jsonObject);
        }
        throw new IllegalArgumentException("Must have either block or entity");
    }

    private static List<? extends EntityType<?>> parseEntities(JsonObject jsonObject) {
        String entityId = GsonHelper.getAsString(jsonObject, "entity");
        List<? extends EntityType<?>> list;
        if (entityId.startsWith("#")) {
            list = Streams.stream(BuiltInRegistries.ENTITY_TYPE.getTagOrEmpty(TagKey.create(
                    Registries.ENTITY_TYPE,
                    ResourceLocation.parse(entityId.substring(1))))).map(Holder::value).toList();
            Preconditions.checkArgument(!list.isEmpty(), "No entity type found for tag: " + entityId);
        } else {
            list = BuiltInRegistries.ENTITY_TYPE.getOptional(ResourceLocation.parse(entityId))
                    .map(List::of)
                    .orElse(List.of());
            Preconditions.checkArgument(!list.isEmpty(), "No entity type found for id: " + entityId);
        }
        return list;
    }

    private static List<Block> parseBlocks(JsonObject jsonObject) {
        String blockId = GsonHelper.getAsString(jsonObject, "block");
        if (blockId.startsWith("#")) {
            List<Block> blocks = Streams.stream(BuiltInRegistries.BLOCK.getTagOrEmpty(TagKey.create(
                    Registries.BLOCK,
                    ResourceLocation.parse(blockId.substring(1))))).map(Holder::value).toList();
            Preconditions.checkArgument(!blocks.isEmpty(), "No block found for tag: " + blockId);
            return blocks;
        } else {
            List<Block> list = BuiltInRegistries.BLOCK.getOptional(ResourceLocation.parse(blockId)).map(List::of).orElse(List.of());
            Preconditions.checkArgument(!list.isEmpty(), "No block found for id: " + blockId);
            return list;
        }
    }

    public void reload() {
        Minecraft mc = Minecraft.getInstance();
        Map<ResourceLocation, JsonElement> map = prepare(mc.getResourceManager(), InactiveProfiler.INSTANCE);
        apply(map, mc.getResourceManager(), InactiveProfiler.INSTANCE);
    }

    @Override
    public @Nullable Accessor<?> onRayTrace(HitResult hitResult, @Nullable Accessor<?> accessor, @Nullable Accessor<?> originalAccessor) {
        Object identifier = getTargetIdentifier(accessor);
        if (identifier == null) {
            return accessor;
        }
        Block replacement = replacementBlocks.get(identifier);
        if (replacement == null) {
            return accessor;
        }
        if (replacement == Blocks.AIR) {
            return null;
        }
        if (accessor == null || accessor.getPlayer().isCreative()) {
            return accessor;
        }
        if (CreateMetallurgyJade.client.maybeLowVisionUser() || !IWailaConfig.get().getGeneral().getBuiltinCamouflage()) {
            return accessor;
        }
        if (accessor instanceof BlockAccessor blockAccessor) {
            return CreateMetallurgyJade.client.blockAccessor().from(blockAccessor).blockState(replacement.defaultBlockState()).build();
        }
        if (accessor instanceof EntityAccessor) {
            BlockHitResult blockHitResult = new BlockHitResult(
                    hitResult.getLocation(),
                    accessor.getPlayer().getDirection().getOpposite(),
                    BlockPos.containing(hitResult.getLocation()),
                    false);
            return CreateMetallurgyJade.client.blockAccessor()
                    .hit(blockHitResult)
                    .player(accessor.getPlayer())
                    .blockState(replacement.defaultBlockState())
                    .build();
        }
        return accessor;
    }

    @Override
    public void onTooltipCollected(IBoxElement rootElement, Accessor<?> accessor) {
        Object identifier = getTargetIdentifier(accessor);
        if (identifier == null) {
            return;
        }
        for (ResourceLocation tag : tagsToRemove.get(identifier)) {
            rootElement.getTooltip().remove(tag);
        }
    }

    @Nullable
    public static Object getTargetIdentifier(@Nullable Accessor<?> accessor) {
        if (accessor instanceof BlockAccessor blockAccessor)
            return blockAccessor.getBlock();
        else if (accessor instanceof EntityAccessor entityAccessor)
            return entityAccessor.getEntity().getType();
        return null;
    }
}