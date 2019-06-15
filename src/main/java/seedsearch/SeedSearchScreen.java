package seedsearch;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.ui.buttons.Button;

public class SeedSearchScreen {

    private boolean hasStartedSearch = false;
    private Hitbox startSeedHb = new Hitbox(700.0F * Settings.scale, 60.0F * Settings.scale);
    private Hitbox endSeedHb = new Hitbox(700.0F * Settings.scale, 60.0F * Settings.scale);
    private Button button = new Button(0, 0, ImageMaster.REWARD_SCREEN_TAKE_BUTTON);
    private Color startSeedColor;
    private Color endSeedColor;
    private static final String START_SEED_TEXT = "Start Seed:";
    private static final String END_SEED_TEXT = "End Seed:";

    public SeedSearchScreen() {
        this.startSeedHb.move(Settings.WIDTH / 4.0F, Settings.HEIGHT - 100.0F * Settings.scale);
        this.endSeedHb.move(Settings.WIDTH * 3.0F / 4.0F, Settings.HEIGHT - 100.0F * Settings.scale);
        button.x = (Settings.WIDTH - button.width) / 2;
        button.y = (Settings.HEIGHT - 400.0F * Settings.scale);
    }

    public void update() {
        this.startSeedHb.update();
        this.endSeedHb.update();
        this.button.update();

        if(button.pressed) {
            hasStartedSearch = true;
            button.pressed = false;
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Settings.SHADOW_COLOR);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT);
        renderSeedSettings(sb);
    }

    public void renderSeedSettings(SpriteBatch sb) {
        startSeedColor = Settings.GOLD_COLOR;
        endSeedColor = Settings.GOLD_COLOR;
        button.render(sb);

        String buttonText;
        if(hasStartedSearch) {
            buttonText = "Cancel Search";
        } else {
            buttonText = "Begin Search";
        }


        FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont_N, "Starting Seed: ", this.startSeedHb.cX, this.startSeedHb.cY, startSeedColor);
        FontHelper.renderFontCenteredHeight(sb, FontHelper.cardTitleFont_N, Long.toString(SeedSearch.settings.startSeed), this.startSeedHb.cX +
                FontHelper.getSmartWidth(FontHelper.cardTitleFont_N, "Starting Seed: ", 9999.0F, 0.0F)/2,
                this.startSeedHb.cY, Settings.BLUE_TEXT_COLOR);
        FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont_N, "Ending Seed: ", this.endSeedHb.cX, this.endSeedHb.cY, endSeedColor);
        FontHelper.renderFontCenteredHeight(sb, FontHelper.cardTitleFont_N, Long.toString(SeedSearch.settings.endSeed), this.endSeedHb.cX +
                        FontHelper.getSmartWidth(FontHelper.cardTitleFont_N, "Ending Seed: ", 9999.0F, 0.0F)/2,
                this.endSeedHb.cY, Settings.BLUE_TEXT_COLOR);

        FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, buttonText, button.x + button.width / 2, button.y + button.height / 2, Settings.CREAM_COLOR);
        if (hasStartedSearch) {
            FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont_N, String.format("Seeds searched: %d/%d", SeedSearch.runner.seedsSearched(), SeedSearch.runner.seedsToSearch()), Settings.WIDTH / 2, 200.0F * Settings.scale, Settings.CREAM_COLOR);
            FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont_N, String.format("Matching seeds found: %d", 0), Settings.WIDTH / 2, 100.0F * Settings.scale, Settings.CREAM_COLOR);
        }

        if (!hasStartedSearch) {
            if (this.startSeedHb.hovered) {
                startSeedColor = Settings.GREEN_TEXT_COLOR;
                TipHelper.renderGenericTip(InputHelper.mX + 50.0F * Settings.scale, InputHelper.mY - 100.0F * Settings.scale,
                        "Starting Seed", "The first seed to search, from -9,223,372,036,854,775,808 to 9,223,372,036,854,775,807");
            }

            if (this.endSeedHb.hovered) {
                endSeedColor = Settings.GREEN_TEXT_COLOR;
                TipHelper.renderGenericTip(InputHelper.mX + 50.0F * Settings.scale, InputHelper.mY - 100.0F * Settings.scale,
                        "Ending Seed", "When to stop searching, from -9,223,372,036,854,775,808 to 9,223,372,036,854,775,807");
            }
        }
    }

}
