package fr.lucreeper74.createmetallurgy.content.items.ladle_filter;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.logistics.AddressEditBox;
import com.simibubi.create.content.logistics.filter.AbstractFilterScreen;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItem;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleStyles;
import fr.lucreeper74.createmetallurgy.content.items.ladle_filter.LadleFilterScreenPacket.Option;
import fr.lucreeper74.createmetallurgy.utils.CMLang;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.lwjgl.glfw.GLFW;

import java.util.List;

import static com.simibubi.create.foundation.gui.AllGuiTextures.PLAYER_INVENTORY;

public class LadleFilterScreen extends AbstractFilterScreen<LadleFilterMenu> {

    private static final ResourceLocation BACKGROUND_TEXTURE = CreateMetallurgy.asResource("textures/gui/ladle_filter.png");
    private static final String PREFIX = "gui.ladle_filter.";

    private MutableComponent filledAmountTitle = CMLang.translateDirect(PREFIX + "filled_amount");
    private MutableComponent filledAmountHint = CMLang.translateDirect(PREFIX + "filled_amount_hint");
    private MutableComponent comparatorTitle = CMLang.translateDirect(PREFIX + "comparator");

    public static final List<MutableComponent> COMPARATORS_LANG_LIST = List.of(
            CMLang.translateDirect("generic.symbol.equal"),
            CMLang.translateDirect("generic.symbol.greater"),
            CMLang.translateDirect("generic.symbol.greater_eq"),
            CMLang.translateDirect("generic.symbol.less"),
            CMLang.translateDirect("generic.symbol.less_eq")
    );

    private ScrollInput filledAmount;
    private SelectionScrollInput comparator;

    private AddressEditBox addressBox;
    private boolean deferFocus;

    ItemStack lastFluidFilterItem = ItemStack.EMPTY;
    Fluid fluidFilter = Fluids.EMPTY;

    public LadleFilterScreen(LadleFilterMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, AllGuiTextures.PACKAGE_FILTER);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int invX = getLeftOfCentered(PLAYER_INVENTORY.getWidth());
        int invY = topPos + background.getHeight() + 4;
        renderPlayerInventory(graphics, invX, invY);

        int x = leftPos;
        int y = topPos;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        graphics.blit(BACKGROUND_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        graphics.drawString(font, title, x + (background.getWidth() - 8) / 2 - font.width(title) / 2, y + 4, 0x3D3C48, false);
        GuiGameElement.of(menu.contentHolder).<GuiGameElement.GuiRenderBuilder>at(x + background.getWidth() + 8, y + background.getHeight() - 52, -200).scale(4).render(graphics);
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        if (deferFocus) {
            deferFocus = false;
            setFocused(addressBox);
        }

        ItemStack stackInSlot = menu.ghostInventory.getStackInSlot(0);
        if (!ItemStack.matches(stackInSlot, lastFluidFilterItem))
            fluidFilterChanged(stackInSlot);

        addressBox.tick();
    }

    @Override
    protected boolean isButtonEnabled(IconButton button) {
        return false;
    }

    @Override
    protected void init() {
        setWindowOffset(-11, 7);
        super.init();

        int x = leftPos;
        int y = topPos;

        addressBox = new AddressEditBox(this, this.font, x + 44, y + 28, 129, 9, false);
        addressBox.setTextColor(0xFFFFFF);
        addressBox.setValue(menu.address);
        addressBox.setResponder(this::sendAddress);
        addRenderableWidget(addressBox);

        filledAmount = new ScrollInput(x + 55, y + 55, 46, 18)
                .titled(filledAmountTitle)
                .addHint(filledAmountHint)
                .withRange(-1, LadleItem.LADLE_CAPACITY + 1)
                .calling(state -> sendScrollInputs())
                .withStepFunction(sc ->
                        filledAmount.getState() < 0 ? 1 :
                                sc.shift ? 500 : 50)
                .setState(menu.filledAmount);
        addRenderableWidgets(filledAmount);

        comparator = (SelectionScrollInput) new SelectionScrollInput(x + 38, y + 55, 16, 18)
                .forOptions(COMPARATORS_LANG_LIST)
                .titled(comparatorTitle)
                .calling(state -> sendScrollInputs())
                .setState(menu.comparator);
        addRenderableWidgets(comparator);

        fluidFilterChanged(menu.ghostInventory.getStackInSlot(0));

        handleIndicators();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);

        int x = leftPos;
        int y = topPos;

        int filledAmount = menu.filledAmount;
        boolean isDisabled = filledAmount < 0;
        graphics.drawString(font,
                Component.literal(isDisabled ? "0" : String.valueOf(filledAmount))
                        .append(CreateLang.translateDirect("generic.unit.millibuckets")),
                x + 60, y + 60, isDisabled ? ChatFormatting.GRAY.getColor() : 0xFFFFFFFF, true);

        graphics.drawString(font,
                COMPARATORS_LANG_LIST.get(menu.comparator),
                x + 44, y + 60, isDisabled ? ChatFormatting.GRAY.getColor() : 0xFFFFFFFF, true);


        PoseStack ms = graphics.pose();
        ms.pushPose();
        ms.translate(leftPos + 16, topPos + 23, 0);
        GuiGameElement.of(LadleStyles.getDefault()).render(graphics);
        ms.popPose();
    }

    private void fluidFilterChanged(ItemStack stack) {
        lastFluidFilterItem = stack;
        Fluid lastFluidFilter = fluidFilter;

        if (stack.isEmpty())
            fluidFilter = Fluids.EMPTY;

        IFluidHandlerItem fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandler != null)
            fluidFilter = fluidHandler.getFluidInTank(0).getFluid();

        if (!lastFluidFilter.isSame(fluidFilter))
            sendFluidFilter();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (addressBox.mouseScrolled(mouseX, mouseY, scrollX, scrollY))
            return true;
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == GLFW.GLFW_KEY_ENTER)
            setFocused(null);
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    protected void contentsCleared() {
        filledAmount.setState(-1);
        addressBox.setValue("*");
        deferFocus = true;
        comparator.setState(0);
    }

    @Override
    public void removed() {
        sendScrollInputs();
    }

    public void sendScrollInputs() {
        int filledAmount = this.filledAmount.getState();
        int comparator = this.comparator.getState();
        menu.filledAmount = filledAmount;
        menu.comparator = comparator;

        CompoundTag tag = new CompoundTag();
        tag.putInt("FilledAmount", filledAmount);
        tag.putInt("Comparator", comparator);

        CatnipServices.NETWORK.sendToServer(new LadleFilterScreenPacket(Option.UPDATE_PERCENT, tag));
    }

    public void sendAddress(String address) {
        menu.address = address;
        CompoundTag tag = new CompoundTag();
        tag.putString("Address", address);
        CatnipServices.NETWORK.sendToServer(new LadleFilterScreenPacket(Option.UPDATE_ADDRESS, tag));
    }

    public void sendFluidFilter() {
        menu.fluidFilter = fluidFilter;
        menu.ghostInventory.setStackInSlot(0, fluidFilter.getBucket().getDefaultInstance());
        CompoundTag tag = new CompoundTag();
        if (fluidFilter != null)
            tag.put("FluidFilter", new FluidStack(fluidFilter, 1)
                    .saveOptional(Minecraft.getInstance().level.registryAccess()));
        CatnipServices.NETWORK.sendToServer(new LadleFilterScreenPacket(Option.UPDATE_FLUID, tag));
    }
}
