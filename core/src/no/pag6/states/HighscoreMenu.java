package no.pag6.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import no.pag6.game.PAG6Game;
import no.pag6.helpers.AssetLoader;
import no.pag6.ui.SimpleButton;

import java.util.Arrays;
import java.util.List;

public class HighscoreMenu extends State {

    // Game assets
    private GlyphLayout gl = new GlyphLayout();
    private BitmapFont font;

    // Game UI
    private SimpleButton backButton;
    private Sprite highscoresTitle;

    // Highscores
    List<String> highscorePlayers;
    List<String> highscores;

    public HighscoreMenu(PAG6Game game) {
        super(game);

        // Init objects and assets
        initUI();

        getPlayerHighscores();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        // Render sprites
        game.spriteBatch.setProjectionMatrix(cam.combined);
        game.spriteBatch.begin();
        game.spriteBatch.enableBlending();

        drawUI();

        game.spriteBatch.end();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchPoint.set(screenX, screenY, 0);
        projected = viewport.unproject(touchPoint);

        backButton.isTouchDown(projected.x, projected.y);

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        touchPoint.set(screenX, screenY, 0);
        projected = viewport.unproject(touchPoint);

        if (backButton.isTouchUp(projected.x, projected.y)) {
            game.getGameStateManager().popScreen();
        }

        return true;
    }

    private void initUI() {
        TextureRegion region;
        float regionWidth, regionHeight;

        // Buttons
        region = AssetLoader.backButtonUp;
        regionWidth = region.getRegionWidth();
        regionHeight = region.getRegionHeight();
        backButton = new SimpleButton(V_WIDTH/2 - regionWidth/2, V_HEIGHT*4/24 - regionHeight/2,
                regionWidth*UI_SCALE*1.1f, regionHeight*UI_SCALE*1.1f,
                AssetLoader.backButtonUp, AssetLoader.backButtonDown);

        // Title
        region = AssetLoader.highscoresTitle;
        regionWidth = region.getRegionWidth()*UI_SCALE;
        regionHeight = region.getRegionHeight()*UI_SCALE;
        highscoresTitle = new Sprite(region);
        highscoresTitle.setSize(regionWidth*UI_SCALE*1.1f, regionHeight*UI_SCALE*1.1f);
        highscoresTitle.setPosition(V_WIDTH/2 - regionWidth/2, V_HEIGHT*20/24 - regionHeight/2);

        // Font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arialbd.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 38;
        parameter.color = Color.BLACK;
        font = generator.generateFont(parameter);
        generator.dispose();
    }

    private void getPlayerHighscores() {
        highscorePlayers = Arrays.asList(AssetLoader.getHighscorePlayers().split(","));
        highscores = Arrays.asList(AssetLoader.getHighscores().split(","));

        int maxIndex = (HIGHSCORES_TO_SHOW < highscores.size()) ? HIGHSCORES_TO_SHOW : highscores.size();
        highscorePlayers = highscorePlayers.subList(0, maxIndex);
        highscores = highscores.subList(0, maxIndex);
    }

    private void drawUI() {
        backButton.draw(game.spriteBatch);

        // Player highscores
        if (highscorePlayers.get(0).equals("")) {
            gl.setText(font, "NO HIGHSCORES YET");
            font.draw(game.spriteBatch, gl, V_WIDTH/2 - gl.width/2, V_HEIGHT*18/24 + gl.height/2);
        } else {
            // Draw highscores
            String highscoresString = "";

            for (int i = 0; i < highscores.size(); i++) {
                highscoresString += highscorePlayers.get(i) + ": " + highscores.get(i) + "\n";
            }

            gl.setText(font, highscoresString);
            font.draw(game.spriteBatch, gl, V_WIDTH/2 - gl.width/2, V_HEIGHT*18/24);
        }

        highscoresTitle.draw(game.spriteBatch);
    }

}
