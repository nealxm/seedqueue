package me.contaria.seedqueue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import me.contaria.seedqueue.compat.ModCompat;
import me.contaria.seedqueue.gui.config.SeedQueueKeybindingsScreen;
import me.contaria.seedqueue.keybindings.SeedQueueKeyBindings;
import me.contaria.seedqueue.keybindings.SeedQueueMultiKeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;
import org.mcsr.speedrunapi.config.SpeedrunConfigAPI;
import org.mcsr.speedrunapi.config.SpeedrunConfigContainer;
import org.mcsr.speedrunapi.config.api.SpeedrunConfig;
import org.mcsr.speedrunapi.config.api.SpeedrunOption;
import org.mcsr.speedrunapi.config.api.annotations.Config;
import org.mcsr.speedrunapi.config.api.annotations.InitializeOn;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@InitializeOn(InitializeOn.InitPoint.PRELAUNCH)
public class SeedQueueConfig implements SpeedrunConfig {

    @Config.Ignored
    public static final int AUTO = 0;

    @Config.Ignored
    private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();

    @Config.Ignored
    private SpeedrunConfigContainer<?> container;

    @Config.Category("queue")
    @Config.Numbers.Whole.Bounds(min = 0, max = 50, enforce = Config.Numbers.EnforceBounds.MIN_ONLY)
    public int maxCapacity = 0;

    @Config.Category("queue")
    @Config.Numbers.Whole.Bounds(min = 0, max = 20, enforce = Config.Numbers.EnforceBounds.MIN_ONLY)
    public int maxConcurrently = 1;

    @Config.Category("queue")
    @Config.Numbers.Whole.Bounds(min = 1, max = 20, enforce = Config.Numbers.EnforceBounds.MIN_ONLY)
    public int maxConcurrently_onWall = 1;

    @Config.Category("queue")
    @Config.Numbers.Whole.Bounds(max = 100)
    public int maxWorldGenerationPercentage = 100;

    @Config.Category("chunkmap")
    public ChunkMapVisibility chunkMapVisibility = ChunkMapVisibility.TRUE;

    @Config.Category("chunkmap")
    @Config.Numbers.Whole.Bounds(min = 1, max = 5)
    public int chunkMapScale = 2;

    @Config.Ignored
    public boolean canUseWall = ModCompat.HAS_WORLDPREVIEW && ModCompat.HAS_STANDARDSETTINGS && ModCompat.HAS_SODIUM;

    @Config.Category("wall")
    public boolean useWall = false;

    @Config.Category("wall")
    @Config.Numbers.Whole.Bounds(min = 1, max = 10, enforce = Config.Numbers.EnforceBounds.MIN_ONLY)
    public int rows = 2;

    @Config.Category("wall")
    @Config.Numbers.Whole.Bounds(min = 1, max = 10, enforce = Config.Numbers.EnforceBounds.MIN_ONLY)
    public int columns = 2;

    @Config.Category("wall")
    @Config.Numbers.Whole.Bounds(min = 0, max = 16384, enforce = Config.Numbers.EnforceBounds.MIN_ONLY)
    @Config.Numbers.TextField
    public int simulatedWindowWidth;

    @Config.Category("wall")
    @Config.Numbers.Whole.Bounds(min = 0, max = 16384, enforce = Config.Numbers.EnforceBounds.MIN_ONLY)
    @Config.Numbers.TextField
    public int simulatedWindowHeight;

    @Config.Category("wall")
    public boolean replaceLockedPreviews = true;

    @Config.Category("wall")
    @Config.Numbers.Whole.Bounds(max = 1000)
    public int resetCooldown = 150;

    @Config.Category("wall")
    public boolean bypassWall = false;

    @Config.Category("performance")
    @Config.Numbers.Whole.Bounds(min = 1, max = 255)
    public int wallFPS = 60;

    @Config.Category("performance")
    @Config.Numbers.Whole.Bounds(min = 1, max = 255)
    public int previewFPS = 15;

    @Config.Category("performance")
    @Config.Numbers.Whole.Bounds(min = 0, max = 50, enforce = Config.Numbers.EnforceBounds.MIN_ONLY)
    public int backgroundPreviews = 0;

    @Config.Category("performance")
    public boolean freezeLockedPreviews = false;

    @Config.Category("advanced")
    public boolean showAdvancedSettings = false;

    @Config.Category("threading")
    @Config.Numbers.Whole.Bounds(min = Thread.MIN_PRIORITY, max = Thread.NORM_PRIORITY)
    public int seedQueueThreadPriority = Thread.NORM_PRIORITY;

    @Config.Category("threading")
    @Config.Numbers.Whole.Bounds(min = Thread.MIN_PRIORITY, max = Thread.NORM_PRIORITY)
    public int serverThreadPriority = 4;

    @Config.Category("threading")
    @Config.Numbers.Whole.Bounds(min = 0, max = 32, enforce = Config.Numbers.EnforceBounds.MIN_ONLY)
    public int backgroundExecutorThreads = AUTO;

    @Config.Category("threading")
    @Config.Numbers.Whole.Bounds(min = Thread.MIN_PRIORITY, max = Thread.NORM_PRIORITY)
    public int backgroundExecutorThreadPriority = 3;

    @Config.Category("threading")
    @Config.Numbers.Whole.Bounds(min = 0, max = 32, enforce = Config.Numbers.EnforceBounds.MIN_ONLY)
    public int wallExecutorThreads = AUTO;

    @Config.Category("threading")
    @Config.Numbers.Whole.Bounds(min = Thread.MIN_PRIORITY, max = Thread.NORM_PRIORITY)
    public int wallExecutorThreadPriority = 4;

    @Config.Category("threading")
    @Config.Numbers.Whole.Bounds(min = 0, max = 8, enforce = Config.Numbers.EnforceBounds.MIN_ONLY)
    public int chunkUpdateThreads = AUTO;

    @Config.Category("threading")
    @Config.Numbers.Whole.Bounds(min = Thread.MIN_PRIORITY, max = Thread.NORM_PRIORITY)
    public int chunkUpdateThreadPriority = 3;

    @Config.Category("experimental")
    public boolean evaluatePacketsServerSide = false;

    @Config.Category("experimental")
    public boolean alwaysDeferChunkUpdates = false;

    @Config.Category("experimental")
    public boolean doNotYieldRenderThread = false;

    @Config.Category("experimental")
    public boolean reduceSchedulingBudget = false;

    @Config.Category("debug")
    public boolean useWatchdog = false;

    @Config.Category("debug")
    public boolean doNotWaitForChunksToBuild = false;

    @Config.Category("debug")
    public boolean showDebugMenu = false;

    @Config.Category("debug")
    @Config.Numbers.Whole.Bounds(max = Integer.MAX_VALUE)
    @Config.Numbers.TextField
    public int benchmarkResets = 0;

    @Config.Category("misc")
    @Config.Name("seedqueue.menu.keys")
    public final SeedQueueMultiKeyBinding[] keyBindings = new SeedQueueMultiKeyBinding[]{
            SeedQueueKeyBindings.play,
            SeedQueueKeyBindings.focusReset,
            SeedQueueKeyBindings.reset,
            SeedQueueKeyBindings.lock,
            SeedQueueKeyBindings.resetAll,
            SeedQueueKeyBindings.resetColumn,
            SeedQueueKeyBindings.resetRow,
            SeedQueueKeyBindings.playNextLock
    };

    {
        SeedQueue.config = this;
    }

    public int getBackgroundExecutorThreads() {
        if (this.backgroundExecutorThreads == AUTO) {
            return Math.max(1, Math.min(this.maxConcurrently, PROCESSORS));
        }
        return this.backgroundExecutorThreads;
    }

    public int getWallExecutorThreads() {
        if (this.wallExecutorThreads == AUTO) {
            return Math.max(1, PROCESSORS);
        }
        return this.wallExecutorThreads;
    }

    public int getChunkUpdateThreads() {
        if (this.chunkUpdateThreads == AUTO) {
            return Math.min(Math.max(2, (int) Math.ceil((double) PROCESSORS / this.maxConcurrently_onWall)), PROCESSORS);
        }
        return this.chunkUpdateThreads;
    }

    public boolean shouldUseWall() {
        return this.canUseWall && this.maxCapacity > 0 && this.useWall;
    }

    public boolean hasSimulatedWindowSize() {
        return this.simulatedWindowWidth != 0 && this.simulatedWindowHeight != 0;
    }

    // see Window#calculateScaleFactor
    public int calculateSimulatedScaleFactor(int guiScale, boolean forceUnicodeFont) {
        int scaleFactor = 1;
        while (scaleFactor != guiScale && scaleFactor < this.simulatedWindowWidth && scaleFactor < this.simulatedWindowHeight && this.simulatedWindowWidth / (scaleFactor + 1) >= 320 && this.simulatedWindowHeight / (scaleFactor + 1) >= 240) {
            scaleFactor++;
        }
        if (forceUnicodeFont) {
            scaleFactor += guiScale % 2;
        }
        return scaleFactor;
    }

    @Override
    public @Nullable SpeedrunOption<?> parseField(Field field, SpeedrunConfig config, String... idPrefix) {
        if ("useWall".equals(field.getName())) {
            return new SpeedrunConfigAPI.CustomOption.Builder<Boolean>(config, this, field, idPrefix)
                    .createWidget((option, config_, configStorage, optionField) -> {
                        if (!this.canUseWall) {
                            ButtonWidget button = new ButtonWidget(0, 0, 150, 20, new TranslatableText("seedqueue.menu.config.useWall.notAvailable"), b -> {}, ((b, matrices, mouseX, mouseY) -> {
                                List<StringRenderable> tooltip = new ArrayList<>(MinecraftClient.getInstance().textRenderer.wrapLines(new TranslatableText("seedqueue.menu.config.useWall.notAvailable.tooltip"), 200));
                                for (int i = 1; i <= 3; i++) {
                                    tooltip.add(new TranslatableText("seedqueue.menu.config.useWall.notAvailable.tooltip." + i));
                                }
                                Objects.requireNonNull(MinecraftClient.getInstance().currentScreen).renderTooltip(matrices, tooltip, mouseX, mouseY);
                            }));
                            button.active = false;
                            return button;
                        }
                        return new ButtonWidget(0, 0, 150, 20, ScreenTexts.getToggleText(option.get()), button -> {
                            option.set(!option.get());
                            button.setMessage(ScreenTexts.getToggleText(option.get()));
                        });
                    })
                    .build();
        }
        if ("showAdvancedSettings".equals(field.getName())) {
            return new SpeedrunConfigAPI.CustomOption.Builder<Boolean>(config, this, field, idPrefix)
                    .createWidget((option, config_, configStorage, optionField) -> new ButtonWidget(0, 0, 150, 20, ScreenTexts.getToggleText(option.get()), button -> {
                        if (!option.get()) {
                            Screen configScreen = MinecraftClient.getInstance().currentScreen;
                            MinecraftClient.getInstance().openScreen(new ConfirmScreen(confirm -> {
                                option.set(confirm);
                                MinecraftClient.getInstance().openScreen(configScreen);
                            }, new TranslatableText("seedqueue.menu.config.showAdvancedSettings.confirm.title"), new TranslatableText("seedqueue.menu.config.showAdvancedSettings.confirm.message"), ScreenTexts.YES, ScreenTexts.CANCEL));
                        } else {
                            option.set(false);
                            MinecraftClient.getInstance().openScreen(MinecraftClient.getInstance().currentScreen);
                        }
                    }))
                    .build();
        }
        if (SeedQueueMultiKeyBinding[].class.equals(field.getType())) {
            return new SpeedrunConfigAPI.CustomOption.Builder<SeedQueueMultiKeyBinding[]>(config, this, field, idPrefix)
                    .fromJson((option, config_, configStorage, optionField, jsonElement) -> {
                        for (SeedQueueMultiKeyBinding keyBinding : option.get()) {
                            keyBinding.fromJson(jsonElement.getAsJsonObject().get(keyBinding.getTranslationKey()));
                        }
                    })
                    .toJson((option, config_, configStorage, optionField) -> {
                        JsonObject jsonObject = new JsonObject();
                        for (SeedQueueMultiKeyBinding keyBinding : option.get()) {
                            jsonObject.add(keyBinding.getTranslationKey(), keyBinding.toJson());
                        }
                        return jsonObject;
                    })
                    .setter((option, config_, configStorage, optionField, value) -> {
                        throw new UnsupportedOperationException();
                    })
                    .createWidget((option, config_, configStorage, optionField) -> new ButtonWidget(0, 0, 150, 20, new TranslatableText("seedqueue.menu.keys.configure"), button -> MinecraftClient.getInstance().openScreen(new SeedQueueKeybindingsScreen(MinecraftClient.getInstance().currentScreen, this.keyBindings))))
                    .build();
        }
        return SpeedrunConfig.super.parseField(field, config, idPrefix);
    }

    public void reload() throws IOException, JsonParseException {
        if (this.container != null) {
            this.container.load();
        }
    }

    @Override
    public void finishInitialization(SpeedrunConfigContainer<?> container) {
        this.container = container;
    }

    @Override
    public boolean shouldShowCategory(String category) {
        if (!this.showAdvancedSettings) {
            return !category.equals("threading") && !category.equals("experimental") && !category.equals("debug");
        }
        return true;
    }

    @Override
    public String modID() {
        return "seedqueue";
    }

    @Override
    public boolean isAvailable() {
        return !SeedQueue.isActive();
    }

    public enum ChunkMapVisibility {
        TRUE,
        TRANSPARENT,
        FALSE
    }
}
